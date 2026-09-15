package com.ecommerce.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "ProductRequest",
        description = "Request payload used to create or update a product"
)
public record ProductRequestDTO(

        @Schema(
                description = "Product name",
                example = "Laptop",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String name,

        @Schema(
                description = "Product price",
                example = "50000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal price,

        @Schema(
                description = "Product category",
                example = "Electronics",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String category
) {
}