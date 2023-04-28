package com.emeraldhieu.awesomestore.product.logic.event;

public record ProductCreatedEvent(String externalId, double price) {
}
