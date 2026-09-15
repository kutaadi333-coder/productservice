package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ProductV2ResponseDTO;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/products")
@Tag(
        name = "Product V2",
        description = "Extended Product API version 2"
)
public class ProductV2Controller {

    private final ProductRepository productRepository;

    public ProductV2Controller(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Operation(
            summary = "Get all products - V2",
            description = "Returns all products with extended information including stock and status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ProductV2ResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<ProductV2ResponseDTO>> getProductsV2() {

        List<ProductV2ResponseDTO> products = productRepository
                .findAll()
                .stream()
                .map(this::convertToV2Response)
                .toList();

        return ResponseEntity.ok(products);
    }

    private ProductV2ResponseDTO convertToV2Response(Product product) {

        return new ProductV2ResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getStock(),
                product.getStatus()
        );
    }
}