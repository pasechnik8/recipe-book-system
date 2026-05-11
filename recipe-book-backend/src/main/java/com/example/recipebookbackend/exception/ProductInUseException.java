package com.example.recipebookbackend.exception;

import java.util.List;

public class ProductInUseException extends RuntimeException {

    private final Long productId;
    private final List<String> dishNames;

    public ProductInUseException(String message, Long productId, List<String> dishNames) {
        super(message);
        this.productId = productId;
        this.dishNames = dishNames;
    }

    public Long getProductId() {
        return productId;
    }

    public List<String> getDishNames() {
        return dishNames;
    }
}