package com.ecommerce.Cart.DTO;

import java.math.BigDecimal;
import java.util.List;

public record CartDTO(
        List<CartItemDTO> items,
        BigDecimal total
) {}
