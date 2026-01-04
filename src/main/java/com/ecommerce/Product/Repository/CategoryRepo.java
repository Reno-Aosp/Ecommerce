package com.ecommerce.Product.Repository;

import com.ecommerce.Product.Entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}
