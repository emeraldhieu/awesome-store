package com.emeraldhieu.awesomestore.product.logic;

import com.emeraldhieu.awesomestore.product.ProductMessage;
import com.emeraldhieu.awesomestore.product.config.KafkaProperties;
import com.emeraldhieu.awesomestore.product.logic.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * An event listener that handles product events.
 * This class applies Observer Pattern which has some benefits:
 * + Decouple Kafka code from the main flow for clean code and maintainability
 * + Be able to use multiple listeners to process the same event
 * + Be able to add/remove event listeners (observers) without altering the main flow
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, ProductMessage> kafkaTemplate;

    @EventListener
    public void handleProductCreated(ProductCreatedEvent event) {
        ProductMessage productMessage = ProductMessage.newBuilder()
            .setProductId(event.externalId())
            .build();
        CompletableFuture<SendResult<String, ProductMessage>> future =
            kafkaTemplate.send(kafkaProperties.getTopic(), productMessage);
        future.whenComplete((result, throwable) -> {
            log.info("Sent message=" + result.getProducerRecord().value() + " with offset=[" + result.getRecordMetadata().offset() + "]");
            if (throwable != null) {
                log.info("Unable to send message=" + result.getProducerRecord().value() + " due to : " + throwable.getMessage());
            }
        });
    }
}
