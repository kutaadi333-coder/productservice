package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ProductPageResponse;
import com.ecommerce.productservice.dto.ProductRequestDTO;
import com.ecommerce.productservice.dto.ProductResponseDTO;
import com.ecommerce.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE
    @Operation(
            summary = "Create a new product",
            description = "Creates a new product using the provided product details."
    )
    @Tag(
            name = "Products",
            description = "Product creation and retrieval APIs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestBody ProductRequestDTO request
    ) {
        ProductResponseDTO response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    // GET ALL
    @Operation(
            summary = "Get products",
            description = "Returns a paginated list of products with optional sorting and filtering."
    )
    @Tag(
            name = "Product Search",
            description = "Product pagination, sorting, filtering and search APIs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductPageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or filtering parameters"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<ProductPageResponse> getProducts(

            @Parameter(
                    description = "Page number. Starts from 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of products per page.",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            int size,

            @Parameter(
                    description = "Field used for sorting.",
                    example = "price"
            )
            @RequestParam(defaultValue = "id")
            String sortBy,

            @Parameter(
                    description = "Sorting direction: asc or desc.",
                    example = "asc"
            )
            @RequestParam(defaultValue = "asc")
            String sortDir,

            @Parameter(
                    description = "Filter products by category.",
                    example = "Electronics"
            )
            @RequestParam(required = false)
            String category,

            @Parameter(
                    description = "Minimum product price.",
                    example = "1000"
            )
            @RequestParam(required = false)
            BigDecimal minPrice,

            @Parameter(
                    description = "Maximum product price.",
                    example = "50000"
            )
            @RequestParam(required = false)
            BigDecimal maxPrice,

            @Parameter(
                    description = "Search keyword in product name.",
                    example = "Laptop"
            )
            @RequestParam(required = false)
            String keyword
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 0"
            );
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Size must be greater than 0"
            );
        }

        if (minPrice != null &&
                minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice != null &&
                maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Maximum price cannot be negative"
            );
        }

        if (minPrice != null &&
                maxPrice != null &&
                minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        ProductPageResponse response = productService.getAllProducts(
                page,
                size,
                sortBy,
                sortDir,
                category,
                minPrice,
                maxPrice,
                keyword
        );

        return ResponseEntity.ok(response);
    }

    // GET BY ID
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a single product using its unique product ID."
    )
    @Tag(
            name = "Products",
            description = "Product creation and retrieval APIs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(

            @Parameter(
                    description = "Unique product ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    // PUT
    @Operation(
            summary = "Update a product",
            description = "Performs a full update of an existing product."
    )
    @Tag(
            name = "Products",
            description = "Product creation and update APIs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(

            @Parameter(
                    description = "Unique product ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @RequestBody ProductRequestDTO request
    ) {
        ProductResponseDTO response =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(response);
    }

    // PATCH
    @Operation(
            summary = "Partially update a product",
            description = "Updates only the product fields provided in the request."
    )
    @Tag(
            name = "Products",
            description = "Product partial update APIs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product partially updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> patchProduct(

            @Parameter(
                    description = "Unique product ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @RequestBody ProductRequestDTO request
    ) {
        ProductResponseDTO response =
                productService.patchProduct(id, request);

        return ResponseEntity.ok(response);
    }

    // DELETE
    @Operation(
            summary = "Delete a product",
            description = "Deletes an existing product using its unique product ID."
    )
    @Tag(
            name = "Product Administration",
            description = "Product administration and deletion APIs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(

            @Parameter(
                    description = "Unique product ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}