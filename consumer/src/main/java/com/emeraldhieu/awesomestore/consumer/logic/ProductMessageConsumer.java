package com.emeraldhieu.awesomestore.consumer.logic;

import com.emeraldhieu.awesomestore.product.ProductMessage;
import com.emeraldhieu.awesomestore.consumer.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consume messages from Kafka broker(s).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductMessageConsumer {

    private final KafkaProperties kafkaProperties;

    @KafkaListener(topics = "#{kafkaConfiguration.topic}", groupId = "#{kafkaConfiguration.groupId}")
    public void consumeMessage(ProductMessage productMessage) {
        log.info(String.format("Received message in group '%s': %s", kafkaProperties.getGroupId(), productMessage));
    }
}