package com.ecommerce.Cart.Controller;

import com.ecommerce.Cart.DTO.AddToCartRequest;
import com.ecommerce.Cart.DTO.CartDTO;
import com.ecommerce.Cart.Service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /api/cart
    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        CartDTO cart = cartService.getCurrentUserCart();
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    // POST /api/cart/items  (add new item or increase quantity)
    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItem(@RequestBody AddToCartRequest request) {
        CartDTO cart = cartService.addItem(request);
        if (cart == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cart);
    }

    // PUT /api/cart/items  (set quantity explicitly)
    @PutMapping("/items")
    public ResponseEntity<CartDTO> updateItem(@RequestBody AddToCartRequest request) {
        CartDTO cart = cartService.updateItem(request);
        if (cart == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cart);
    }

    // DELETE /api/cart/items/{productId}
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartDTO> removeItem(@PathVariable Long productId) {
        CartDTO cart = cartService.removeItem(productId);
        if (cart == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cart);
    }

    // DELETE /api/cart
    @DeleteMapping
    public ResponseEntity<CartDTO> clearCart() {
        CartDTO cart = cartService.clearCart();
        if (cart == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cart);
    }
}
