package com.example.recipebookbackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class DishIngredientRequest {

    @NotNull(message = "ID продукта обязателен")
    private Long productId;

    @NotNull(message = "Количество обязательно")
    @DecimalMin(value = "0.000001", inclusive = true, message = "Количество должно быть больше 0")
    private Double quantity;

    public DishIngredientRequest() {
    }

    public Long getProductId() {
        return productId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}