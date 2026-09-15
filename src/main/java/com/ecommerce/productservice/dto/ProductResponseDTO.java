package com.ecommerce.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "ProductResponse",
        description = "Product information returned by the API"
)
public record ProductResponseDTO(

        @Schema(
                description = "Unique product identifier",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Product name",
                example = "Laptop"
        )
        String name,

        @Schema(
                description = "Product price",
                example = "50000"
        )
        BigDecimal price,

        @Schema(
                description = "Product category",
                example = "Electronics"
        )
        String category
) {
}