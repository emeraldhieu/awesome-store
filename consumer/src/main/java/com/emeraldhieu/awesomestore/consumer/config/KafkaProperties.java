package com.emeraldhieu.awesomestore.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * {@link ConfigurationProperties} is scanned by {@link ConfigurationPropertiesScan}.
 */

@ConfigurationProperties(prefix = "application.kafka")
@Data
public class KafkaProperties {
    private String bootstrapAddress;
    private String topic;
    private int partitions;
    private int replicationFactor;
    private String groupId;
}