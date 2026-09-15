package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductPageResponse;
import com.ecommerce.productservice.dto.ProductRequestDTO;
import com.ecommerce.productservice.dto.ProductResponseDTO;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.ProductNotFoundException;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // CREATE
    public ProductResponseDTO createProduct(ProductRequestDTO request) {

        Product product = new Product();

        product.setName(request.name());
        product.setPrice(request.price());
        product.setCategory(request.category());

        // Default values required by the database
        product.setStock(0);
        product.setStatus("AVAILABLE");

        Product savedProduct = productRepository.save(product);

        return convertToResponse(savedProduct);
    }

    // READ - Get all products with pagination, sorting and filtering
    public ProductPageResponse getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String keyword
    ) {

        Sort.Direction direction =
                sortDir.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;

        if (category != null ||
                minPrice != null ||
                maxPrice != null ||
                keyword != null) {

            productPage = productRepository.findProductsWithFilters(
                    category,
                    minPrice,
                    maxPrice,
                    keyword,
                    pageable
            );

        } else {

            productPage = productRepository.findAll(pageable);
        }

        List<ProductResponseDTO> content = productPage
                .getContent()
                .stream()
                .map(this::convertToResponse)
                .toList();

        return new ProductPageResponse(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    // READ - Get product by ID
    public ProductResponseDTO getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        return convertToResponse(product);
    }

    // UPDATE - Full update
    public ProductResponseDTO updateProduct(
            Long id,
            ProductRequestDTO request
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        product.setName(request.name());
        product.setPrice(request.price());
        product.setCategory(request.category());

        Product updatedProduct = productRepository.save(product);

        return convertToResponse(updatedProduct);
    }

    // UPDATE - Partial update
    public ProductResponseDTO patchProduct(
            Long id,
            ProductRequestDTO request
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        if (request.name() != null) {
            product.setName(request.name());
        }

        if (request.price() != null) {
            product.setPrice(request.price());
        }

        if (request.category() != null) {
            product.setCategory(request.category());
        }

        Product updatedProduct = productRepository.save(product);

        return convertToResponse(updatedProduct);
    }

    // DELETE
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(product);
    }

    // Convert Entity to Response DTO
    private ProductResponseDTO convertToResponse(Product product) {

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory()
        );
    }
}