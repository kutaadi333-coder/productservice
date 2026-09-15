package com.ecommerce.productservice.dto;

import java.math.BigDecimal;

public record ProductV1ResponseDTO(
        Long id,
        String name,
        BigDecimal price
) {
}