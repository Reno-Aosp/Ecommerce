package com.ecommerce.Cart.Service;

import com.ecommerce.Cart.DTO.AddToCartRequest;
import com.ecommerce.Cart.DTO.CartDTO;

public interface CartService {

    CartDTO getCurrentUserCart();
    CartDTO addItem(AddToCartRequest request);
    CartDTO updateItem(AddToCartRequest request);
    CartDTO removeItem(Long productId);
    CartDTO clearCart();
}
