package com.emeraldhieu.awesomestore.product.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    private ProductService productService;
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        productService = mock(ProductService.class);
        productController = new ProductController(productService);
    }

    @Test
    void givenProductRequest_whenCreate_thenReturnProductResponse() {
        // GIVEN
        ProductRequest productRequest = ProductRequest.builder()
            .build();
        String id = "awesomeId";
        ProductResponse productResponse = ProductResponse.builder()
            .id(id)
            .build();
        when(productService.create(productRequest)).thenReturn(productResponse);

        // WHEN
        ResponseEntity<ProductResponse> productResponseEntity = productController.createProduct(productRequest);

        // THEN
        assertEquals(HttpStatus.CREATED, productResponseEntity.getStatusCode());
        String uri = String.format(ProductController.PRODUCT_PATTERN, id);
        assertEquals(uri, productResponseEntity.getHeaders().getLocation().toString());
    }

    @Test
    void deleteProduct() {
        // TODO Implement later
    }

    @Test
    void getProduct() {
        // TODO Implement later
    }

    @Test
    void listProducts() {
        // TODO Implement later
    }

    @Test
    void updateProduct() {
        // TODO Implement later
    }
}