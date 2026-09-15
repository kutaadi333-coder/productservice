package com.ecommerce.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "ProductV2Response",
        description = "Extended product information returned by the V2 API"
)
public record ProductV2ResponseDTO(

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
        String category,

        @Schema(
                description = "Available stock quantity",
                example = "25"
        )
        Integer stock,

        @Schema(
                description = "Current product status",
                example = "AVAILABLE"
        )
        String status
) {
}