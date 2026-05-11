package com.example.recipebookbackend.dto;

import java.util.List;

public class DeleteProductConflictResponse {

    private String message;
    private Long productId;
    private List<String> dishNames;

    public DeleteProductConflictResponse() {
    }

    public DeleteProductConflictResponse(String message, Long productId, List<String> dishNames) {
        this.message = message;
        this.productId = productId;
        this.dishNames = dishNames;
    }

    public String getMessage() {
        return message;
    }

    public Long getProductId() {
        return productId;
    }

    public List<String> getDishNames() {
        return dishNames;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setDishNames(List<String> dishNames) {
        this.dishNames = dishNames;
    }
}