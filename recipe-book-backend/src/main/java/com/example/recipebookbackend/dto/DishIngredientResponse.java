package com.example.recipebookbackend.dto;

public class DishIngredientResponse {

    private Long productId;
    private String productName;
    private Double quantity;

    public DishIngredientResponse() {
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}