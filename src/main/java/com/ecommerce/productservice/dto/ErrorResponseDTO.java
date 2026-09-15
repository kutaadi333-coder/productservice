package com.ecommerce.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "ErrorResponse",
        description = "Standard error response returned when an API request fails"
)
public record ErrorResponseDTO(

        @Schema(
                description = "HTTP status code",
                example = "404"
        )
        int status,

        @Schema(
                description = "Short description of the error",
                example = "Not Found"
        )
        String error,

        @Schema(
                description = "Detailed error message",
                example = "Product not found with id: 99"
        )
        String message,

        @Schema(
                description = "Date and time when the error occurred",
                example = "2026-09-11T13:30:00"
        )
        LocalDateTime timestamp
) {
}