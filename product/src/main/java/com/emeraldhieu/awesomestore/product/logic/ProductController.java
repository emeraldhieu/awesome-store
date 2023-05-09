package com.emeraldhieu.awesomestore.product.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A controller that implements an Open API contract.
 * Advantages:
 * + Readability: Everyone can understand, update, and review it
 * + Interaction: You can see and use the mock API using a tool like Swagger UI
 * + Widely adopted: Supported by libraries such as code generator
 */
@Validated
@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

    private final ProductService productService;
    static final String PRODUCT_PATTERN = "/products/%s";

    @Override
    public ResponseEntity<ProductResponse> createProduct(ProductRequest productRequest) {
        ProductResponse createdProduct = productService.create(productRequest);
        return ResponseEntity.created(URI.create(String.format(PRODUCT_PATTERN, createdProduct.getId())))
            .body(createdProduct);
    }

    @Override
    public ResponseEntity<Void> deleteProduct(String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ProductResponse> getProduct(String id) {
        ProductResponse retrievedProduct = productService.get(id);
        return ResponseEntity.ok(retrievedProduct);
    }

    @Override
    public ResponseEntity<List<ProductResponse>> listProducts(Integer offset, Integer limit, List<String> sortOrders) {
        Page<ProductResponse> productResponsePage = productService.list(offset, limit, sortOrders);
        List<ProductResponse> productResponses = productResponsePage.stream()
            .collect(Collectors.toList());
        return ResponseEntity.ok(productResponses);
    }

    @Override
    public ResponseEntity<ProductResponse> updateProduct(String id, ProductRequest productRequest) {
        ProductResponse updatedProduct = productService.update(id, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }
}
