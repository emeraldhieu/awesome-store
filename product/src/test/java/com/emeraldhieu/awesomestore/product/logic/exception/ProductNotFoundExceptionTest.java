package com.emeraldhieu.awesomestore.product.logic.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductNotFoundExceptionTest {

    private ProductNotFoundException exception;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void givenProductId_whenCreateProductNotFounException_thenReturnAnExceptionWithProductId() {
        // GIVEN
        String productId = "product42";

        // WHEN
        exception = new ProductNotFoundException(productId);

        // THEN
        assertEquals(productId, exception.getProductId());
    }

}