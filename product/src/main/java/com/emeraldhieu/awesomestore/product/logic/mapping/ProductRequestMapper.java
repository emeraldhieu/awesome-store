package com.emeraldhieu.awesomestore.product.logic.mapping;

import com.emeraldhieu.awesomestore.product.logic.Product;
import com.emeraldhieu.awesomestore.product.logic.ProductRequest;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductRequest}.
 */
@Mapper(componentModel = "spring")
public interface ProductRequestMapper extends RequestMapper<ProductRequest, Product> {

}