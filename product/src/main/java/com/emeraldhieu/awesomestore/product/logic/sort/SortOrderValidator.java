package com.emeraldhieu.awesomestore.product.logic.sort;

import com.emeraldhieu.awesomestore.product.logic.Product;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SortOrderValidator {

    public void validate(List<String> sortOrders) {
        sortOrders.forEach(sortOrderStr -> {
            SortOrder sortOrder = SortOrder.from(sortOrderStr);
            String propertyName = sortOrder.getPropertyName();
            String direction = sortOrder.getDirection();
            List<String> allowedPropertyNames = Arrays.stream(Product.class.getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList());

            if (!allowedPropertyNames.contains(propertyName)) {
                throw new InvalidSortOrderException("Invalid property %s".formatted(propertyName));
            }

            try {
                Sort.Direction.fromString(direction);
            } catch (Exception e) {
                throw new InvalidSortOrderException("Invalid direction %s".formatted(direction), e);
            }
        });
    }
}