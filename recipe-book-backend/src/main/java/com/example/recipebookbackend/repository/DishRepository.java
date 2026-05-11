package com.example.recipebookbackend.repository;

import com.example.recipebookbackend.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {
}