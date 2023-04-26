package com.emeraldhieu.awesomestore.product.logic;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * An interface used to inverse dependency between controller and conrete service class.
 * It's the "D" in SOLID.
 * Benefits:
 * + Can reuse the policy layer because it doesn't depend on details but depends on interface
 * + Modifying detail layer doesn't require re-testing policy layer (because policy depends on interfaces)
 * See https://en.wikipedia.org/wiki/Dependency_inversion_principle#Dependency_inversion_pattern
 */
public interface ProductService {

    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(String id, ProductRequest productRequest);

    Page<ProductResponse> list(int offset, int limit, List<String> sortOrders);

    ProductResponse get(String id);

    void delete(String id);
}