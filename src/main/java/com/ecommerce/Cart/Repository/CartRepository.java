package com.ecommerce.Cart.Repository;

import com.ecommerce.Cart.Entity.Cart;
import com.ecommerce.User.Entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Long userId);
}
