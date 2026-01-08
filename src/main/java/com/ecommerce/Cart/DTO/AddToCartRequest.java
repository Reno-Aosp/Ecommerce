package com.ecommerce.Cart.DTO;

public record AddToCartRequest(
        Long productId,
        Integer quantity
) {}
