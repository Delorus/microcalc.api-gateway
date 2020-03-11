package ru.sherb.microcalc.apiservice.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

/**
 * @author maksim
 * @since 29.02.2020
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    public static final String PLUS_TOPIC = "plus";
    public static final String MINUS_TOPIC = "minus";
    public static final String DIVIDE_TOPIC = "divide";
    public static final String MULT_TOPIC = "multiply";

    @Bean
    @SuppressWarnings("unchecked")
    public ConcurrentMessageListenerContainer kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory
    ) {
        return new ConcurrentMessageListenerContainer(consumerFactory,
                                                      new ContainerProperties(PLUS_TOPIC, MINUS_TOPIC, MULT_TOPIC, DIVIDE_TOPIC));
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ReplyingKafkaTemplate<String, String, Number> replyingKafkaTemplate(
            ProducerFactory factory,
            ConcurrentMessageListenerContainer<String, Number> responceListener
    ) {
        return new ReplyingKafkaTemplate<>(factory, responceListener);
    }
}
