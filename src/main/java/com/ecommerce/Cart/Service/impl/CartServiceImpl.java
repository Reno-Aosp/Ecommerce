package com.ecommerce.Cart.Service.impl;

import com.ecommerce.Cart.DTO.AddToCartRequest;
import com.ecommerce.Cart.DTO.CartDTO;
import com.ecommerce.Cart.DTO.CartItemDTO;
import com.ecommerce.Cart.Entity.Cart;
import com.ecommerce.Cart.Entity.CartItem;
import com.ecommerce.Cart.Repository.CartItemRepository;
import com.ecommerce.Cart.Repository.CartRepository;
import com.ecommerce.Product.Entity.Product;
import com.ecommerce.Product.Repository.ProductRepo;
import com.ecommerce.User.Entity.User;
import com.ecommerce.User.Repository.UserRepo;
import com.ecommerce.Cart.Service.CartService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepo productRepository;
    private final UserRepo userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepo productRepository,
                           UserRepo  userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // TODO: later use Spring Security's Authentication instead of Principal argument
    private User getCurrentUser(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setTotal(BigDecimal.ZERO);
                    return cartRepository.save(cart);
                });
    }

    private void recalculateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotal(total);
    }

    private CartDTO mapToCartDto(Cart cart) {
        List<CartItemDTO> itemDtos = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                ))
                .toList();

        return new CartDTO(itemDtos, cart.getTotal());
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCurrentUserCart() {
        // In real app pass Principal or SecurityContext; simplified here
        throw new UnsupportedOperationException("Use overload with Principal");
    }

    // Overload to actually use in controller (if you inject Principal there)
    public CartDTO getCurrentUserCart(Principal principal) {
        User user = getCurrentUser(principal);
        Cart cart = getOrCreateCart(user);
        return mapToCartDto(cart);
    }

    @Override
    public CartDTO addItem(AddToCartRequest request) {
        throw new UnsupportedOperationException("Use overload with Principal");
    }

    public CartDTO addItem(AddToCartRequest request, Principal principal) {
        User user = getCurrentUser(principal);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cart);
                    ci.setProduct(product);
                    ci.setQuantity(0);
                    ci.setUnitPrice(product.getPrice());
                    cart.getItems().add(ci);
                    return ci;
                });

        int newQty = item.getQuantity() + request.quantity();
        item.setQuantity(newQty);
        item.setUnitPrice(product.getPrice());
        item.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(newQty)));

        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return mapToCartDto(cart);
    }

    @Override
    public CartDTO updateItem(AddToCartRequest request) {
        throw new UnsupportedOperationException("Use overload with Principal");
    }

    public CartDTO updateItem(AddToCartRequest request, Principal principal) {
        User user = getCurrentUser(principal);
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(request.productId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not in cart"));

        if (request.quantity() <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(request.quantity());
            item.setLineTotal(item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(request.quantity())));
        }

        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return mapToCartDto(cart);
    }

    @Override
    public CartDTO removeItem(Long productId) {
        throw new UnsupportedOperationException("Use overload with Principal");
    }

    public CartDTO removeItem(Long productId, Principal principal) {
        User user = getCurrentUser(principal);
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        }

        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return mapToCartDto(cart);
    }

    @Override
    public CartDTO clearCart() {
        throw new UnsupportedOperationException("Use overload with Principal");
    }

    public CartDTO clearCart(Principal principal) {
        User user = getCurrentUser(principal);
        Cart cart = getOrCreateCart(user);

        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return mapToCartDto(cart);
    }
}
