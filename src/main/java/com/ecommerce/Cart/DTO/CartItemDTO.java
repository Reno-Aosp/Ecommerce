package com.ecommerce.Cart.DTO;

import java.math.BigDecimal;

public record CartItemDTO(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
