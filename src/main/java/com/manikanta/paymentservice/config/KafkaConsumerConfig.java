package com.manikanta.paymentservice.config;

import com.manikanta.paymentservice.event.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public DefaultKafkaConsumerFactory<String, OrderCreatedEvent> consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        props.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "com.manikanta.paymentservice.event"
        );

        props.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                "com.manikanta.paymentservice.event.OrderCreatedEvent"
        );

        props.put(
                JsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );

        props.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                false
        );

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(OrderCreatedEvent.class, false)
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler() {

        // Original attempt + 2 retries
        FixedBackOff fixedBackOff =
                new FixedBackOff(2000L, 2L);

        return new DefaultErrorHandler(fixedBackOff);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
    kafkaListenerContainerFactory(
            DefaultKafkaConsumerFactory<String, OrderCreatedEvent> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
                factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // Enable Micrometer observations for Kafka listener processing
        factory.getContainerProperties().setObservationEnabled(true);

        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.RECORD);

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}