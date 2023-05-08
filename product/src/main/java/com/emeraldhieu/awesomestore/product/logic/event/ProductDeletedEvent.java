package com.emeraldhieu.awesomestore.product.logic.event;

import com.emeraldhieu.awesomestore.product.logic.ProductEventListener;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * @see ProductEventListener
 */
@Builder(toBuilder = true)
@Getter
@Jacksonized
@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProductDeletedEvent {
    private final String id;
    private final String name;
    private final double price;
}
