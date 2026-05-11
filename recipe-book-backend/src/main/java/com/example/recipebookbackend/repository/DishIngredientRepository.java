package com.example.recipebookbackend.repository;

import com.example.recipebookbackend.entity.DishIngredient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishIngredientRepository extends JpaRepository<DishIngredient, Long> {
    List<DishIngredient> findByProductId(Long productId);
}