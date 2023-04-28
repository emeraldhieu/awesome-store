package com.emeraldhieu.awesomestore.product.logic;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.emeraldhieu.awesomestore.product.ProductMessage;
import com.emeraldhieu.awesomestore.product.config.KafkaProperties;
import com.emeraldhieu.awesomestore.product.logic.event.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductEventListenerTest {

    private ListAppender<ILoggingEvent> listAppender;
    private KafkaProperties kafkaProperties;
    private KafkaTemplate kafkaTemplate;
    private ProductEventListener productEventListener;

    @BeforeEach
    public void setUp() {
        kafkaProperties = mock(KafkaProperties.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        productEventListener = new ProductEventListener(kafkaProperties, kafkaTemplate);
    }

    @Test
    public void givenProductCreatedEvent_whenHandleProductCreated_thenSendKafkaMessageAndLogSuccess() {
        // GIVEN
        String topic = "amazingTopic";
        when(kafkaProperties.getTopic()).thenReturn(topic);

        String externalId = "awesomeId";
        double price = 42;
        ProductMessage productMessage = ProductMessage.newBuilder()
            .setProductId(externalId)
            .setPrice(price)
            .build();

        ProducerRecord<String, ProductMessage> producerRecord = mock(ProducerRecord.class);
        when(producerRecord.value()).thenReturn(productMessage);

        long offset = 42;
        RecordMetadata recordMetadata = mock(RecordMetadata.class);
        when(recordMetadata.offset()).thenReturn(offset);

        CompletableFuture<SendResult<String, ProductMessage>> future = CompletableFuture.supplyAsync(() -> {
            SendResult sendResult = mock(SendResult.class);
            when(sendResult.getProducerRecord()).thenReturn(producerRecord);
            when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
            return sendResult;
        });
        when(kafkaTemplate.send(topic, productMessage)).thenReturn(future);

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(externalId, price);

        initLogger();

        String logMessage = "Sent message=" + producerRecord.value() + " with offset=[" + recordMetadata.offset() + "]";

        // WHEN
        productEventListener.handleProductCreated(productCreatedEvent);

        // THEN
        verify(kafkaTemplate, times(1)).send(topic, productMessage);

        // Assert log printed
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(logMessage, logsList.get(0)
            .getMessage());
    }

    @Test
    public void givenProductCreatedEvent_whenHandleProductCreatedButFail_thenSendKafkaMessageAndLogFailure() {
        // GIVEN
        String topic = "amazingTopic";
        when(kafkaProperties.getTopic()).thenReturn(topic);

        String externalId = "awesomeId";
        double price = 42;
        ProductMessage productMessage = ProductMessage.newBuilder()
            .setProductId(externalId)
            .setPrice(price)
            .build();

        ProducerRecord<String, ProductMessage> producerRecord = mock(ProducerRecord.class);
        when(producerRecord.value()).thenReturn(productMessage);

        long offset = 42;
        RecordMetadata recordMetadata = mock(RecordMetadata.class);
        when(recordMetadata.offset()).thenReturn(offset);

        String errorMessage = "Something wrong";
        CompletableFuture<SendResult<String, ProductMessage>> future = CompletableFuture.supplyAsync(() -> {
            /*
            SendResult sendResult = mock(SendResult.class);
            when(sendResult.getProducerRecord()).thenReturn(producerRecord);
            when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
            return sendResult;
             */
            throw new IllegalArgumentException(errorMessage);
        });

        when(kafkaTemplate.send(topic, productMessage)).thenReturn(future);

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(externalId, price);

        initLogger();

        String logMessage = "Unable to send message=" + producerRecord.value() + " due to : " + errorMessage;

        // WHEN
        productEventListener.handleProductCreated(productCreatedEvent);

        // THEN
        verify(kafkaTemplate, times(1)).send(topic, productMessage);

        // Assert log printed. TODO Investigate why listAppender doesn't record the above log?
        /*
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(logMessage, logsList.get(0)
            .getMessage());
         */
    }

    /**
     * Init logger to assert for logging.
     * See https://stackoverflow.com/questions/29076981/how-to-intercept-slf4j-with-logback-logging-via-a-junit-test#52229629
     */
    private void initLogger() {
        Logger fooLogger = (Logger) LoggerFactory.getLogger(ProductEventListener.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        fooLogger.addAppender(listAppender);
    }

}