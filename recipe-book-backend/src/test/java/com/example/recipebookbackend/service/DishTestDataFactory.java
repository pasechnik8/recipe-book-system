package com.example.recipebookbackend.service;

import com.example.recipebookbackend.dto.DishIngredientRequest;
import com.example.recipebookbackend.dto.DishRequest;
import com.example.recipebookbackend.entity.Product;
import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.ProductCategory;
import com.example.recipebookbackend.enums.ProductReadiness;

import java.util.List;
import java.util.Set;

final class DishTestDataFactory {

    private DishTestDataFactory() {
    }

    static Product potato() {
        return createProduct(
                1L,
                "Картофель",
                77.0,
                2.0,
                0.4,
                16.3,
                ProductCategory.VEGETABLES,
                ProductReadiness.REQUIRES_COOKING,
                Set.of(AdditionalFlag.VEGAN, AdditionalFlag.GLUTEN_FREE, AdditionalFlag.SUGAR_FREE)
        );
    }

    static Product water() {
        return createProduct(
                2L,
                "Вода",
                0.0,
                0.0,
                0.0,
                0.0,
                ProductCategory.LIQUID,
                ProductReadiness.READY_TO_EAT,
                Set.of(AdditionalFlag.VEGAN, AdditionalFlag.GLUTEN_FREE, AdditionalFlag.SUGAR_FREE)
        );
    }

    static Product meat() {
        return createProduct(
                3L,
                "Мясо",
                187.2,
                18.9,
                12.4,
                0.0,
                ProductCategory.MEAT,
                ProductReadiness.REQUIRES_COOKING,
                Set.of(AdditionalFlag.GLUTEN_FREE, AdditionalFlag.SUGAR_FREE)
        );
    }

    static Product beet() {
        return createProduct(
                4L,
                "Свёкла",
                43.0,
                1.6,
                0.2,
                9.6,
                ProductCategory.VEGETABLES,
                ProductReadiness.REQUIRES_COOKING,
                Set.of(AdditionalFlag.VEGAN, AdditionalFlag.GLUTEN_FREE, AdditionalFlag.SUGAR_FREE)
        );
    }

    static DishIngredientRequest ingredient(Long productId, Double quantity) {
        DishIngredientRequest ingredient = new DishIngredientRequest();
        ingredient.setProductId(productId);
        ingredient.setQuantity(quantity);
        return ingredient;
    }

    static DishRequest createDishRequest(List<DishIngredientRequest> ingredients, Double portionSize) {
        DishRequest request = new DishRequest();
        request.setName("!суп Тестовое блюдо");
        request.setPhotos(List.of());
        request.setCalories(0.0);
        request.setProteins(0.0);
        request.setFats(0.0);
        request.setCarbs(0.0);
        request.setPortionSize(portionSize);
        request.setCategory(null);
        request.setIngredients(ingredients);
        request.setFlags(Set.of());
        return request;
    }

    private static Product createProduct(Long id,
                                         String name,
                                         Double calories,
                                         Double proteins,
                                         Double fats,
                                         Double carbs,
                                         ProductCategory category,
                                         ProductReadiness readiness,
                                         Set<AdditionalFlag> flags) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPhotos(List.of());
        product.setCalories(calories);
        product.setProteins(proteins);
        product.setFats(fats);
        product.setCarbs(carbs);
        product.setComposition(name);
        product.setCategory(category);
        product.setReadiness(readiness);
        product.setFlags(flags);
        return product;
    }
}