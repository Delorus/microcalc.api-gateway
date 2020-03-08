package ru.sherb.microcalc.apiservice.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import ru.sherb.microcalc.expr.ExprPart;
import ru.sherb.microcalc.expr.ExpressionSplitter;
import ru.sherb.microcalc.expr.SplitExpression;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksim
 * @since 04.03.2020
 */
@EmbeddedKafka(topics = {"plus", "minus", "divide", "multiply", "result"}, ports = 9092)
@SpringBootTest
//@Import(KafkaClientTest.TestKafkaConfig.class)
public class KafkaClientTest {

//    @Configuration
//    static class TestKafkaConfig {
//        @Autowired
//        private EmbeddedKafkaBroker kafkaBroker;
//
//        @Bean
//        public ProducerFactory<String, ExprPartMessage> producerFactory() {
//            Map<String, Object> props = KafkaTestUtils.producerProps(kafkaBroker);
//            return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>());
//        }
//
//        @Bean
//        public ConsumerFactory<String, ResultResponse> consumerFactory() {
//            Map<String, Object> props = KafkaTestUtils.consumerProps("api", "true", kafkaBroker);
//            return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(ResultResponse.class));
//        }
//
//        @Bean
//        public KafkaClient kafkaClient(ReplyingKafkaTemplate template) {
//            return new KafkaClient(template);
//        }

//        @Bean
//        public ConcurrentMessageListenerContainer<String, ResultResponse> kafkaListenerContainerFactory(
//                ConsumerFactory<String, ResultResponse> consumerFactory
//        ) {
//            return new ConcurrentMessageListenerContainer<>(consumerFactory,
//                                                            new ContainerProperties(PLUS_TOPIC, MINUS_TOPIC, MULT_TOPIC, DIVIDE_TOPIC));
//        }
//
//        @Bean
//        public ReplyingKafkaTemplate<String, ExprPartMessage, ResultResponse> replyingKafkaTemplate(
//                ProducerFactory<String, ExprPartMessage> factory,
//                ConcurrentMessageListenerContainer<String, ResultResponse> responceListener
//        ) {
//            return new ReplyingKafkaTemplate<>(factory, responceListener);
//        }
//    }

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    @Autowired
    private KafkaClient kafkaClient;

    @Test
    @Disabled("not working")
    public void testSendOnePartOfExpr() {
        // Setup
        Consumer<String, ExprPartMessage> consumer = setupKafkaConsumer("plus");
        Number expectedResult = 42;
        SplitExpression expression = ExpressionSplitter.split("1 + 2");
        ExprPart[] roots = expression.roots();

        // When
        CompletableFuture.runAsync(() -> {
            sendResponse(consumer, "plus", "1 2", 42);
        });
        List<Map.Entry<ExprPart, Number>> result = kafkaClient.sendPart(roots);

        // Then
        assertEquals(1, result.size());
        assertPartEquals(roots[0], result.get(0).getKey());

    }

    //todo extract to utils method

    private Consumer<String, ExprPartMessage> setupKafkaConsumer(String topic) {
        var props = KafkaTestUtils.consumerProps("consumer", "true", kafkaBroker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, ExprPartMessage> consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(ExprPartMessage.class))
                .createConsumer();

        consumer.subscribe(Collections.singleton(topic));
        return consumer;
    }

    private void sendResponse(Consumer<String, ExprPartMessage> consumer, String topic, String expected, Number returnValue) {
        ConsumerRecord<String, ExprPartMessage> record = KafkaTestUtils.getSingleRecord(consumer, topic, 100);
        assertNotNull(record);
        assertEquals(record.value().getValues(), expected);
        assertTrue(record.headers().headers(KafkaHeaders.REPLY_TOPIC).iterator().hasNext());

        var producerProps = KafkaTestUtils.producerProps(kafkaBroker);
        Producer <String, ResultResponse> producer = new DefaultKafkaProducerFactory<String, ResultResponse>(
                producerProps,
                new StringSerializer(),
                new JsonSerializer<>())
                .createProducer();

        String replyTopic = new String(record.headers().headers(KafkaHeaders.REPLY_TOPIC).iterator().next().value());
        producer.send(new ProducerRecord<>("result", replyTopic, new ResultResponse(record.value().getId(), returnValue)));
        producer.flush();
    }

    private void assertPartEquals(ExprPart expected, ExprPart actual) {
        assertEquals(expected.operator(), actual.operator());
        assertArrayEquals(expected.args(), actual.args());
    }
}