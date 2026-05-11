package com.example.recipebookbackend.repository;

import com.example.recipebookbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}