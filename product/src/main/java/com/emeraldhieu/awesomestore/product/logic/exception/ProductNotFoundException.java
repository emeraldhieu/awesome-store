package com.emeraldhieu.awesomestore.product.logic.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final String productId;

    public ProductNotFoundException(String productId) {
        super();
        this.productId = productId;
    }
}
