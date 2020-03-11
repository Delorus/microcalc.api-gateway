package ru.sherb.microcalc.apiservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import ru.sherb.microcalc.apiservice.service.ExpressionSender;
import ru.sherb.microcalc.expr.ExprPart;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 29.02.2020
 */
@Service
public class KafkaClient implements ExpressionSender {

    private final ReplyingKafkaTemplate<String, String, Number> template;

    @Autowired
    public KafkaClient(ReplyingKafkaTemplate<String, String, Number> template) {
        this.template = template;
    }

    @Override
    public List<Entry<ExprPart, Number>> sendPart(ExprPart[] parts) {
        List<Entry<ExprPart, Future<Number>>> futures = Arrays.stream(parts)
                .map(part -> Map.entry(part, this.sendAndReceive(part)))
                .collect(Collectors.toList());

        return futures.stream()
                .map(entry -> Map.entry(entry.getKey(), resolveFuture(entry.getValue())))
                .collect(Collectors.toList());
    }

    private Future<Number> sendAndReceive(ExprPart part) {
        Optional<String> maybeTopic = findTopicForOperator(part.operator());
        if (maybeTopic.isEmpty()) {
            return CompletableFuture.failedFuture(new TopicNotFoundException(part.operator()));
        }

        CompletableFuture<Number> future = new CompletableFuture<>();

        String message = String.join(" ", part.args());

        ProducerRecord<String, String> record = new ProducerRecord<>(maybeTopic.get(), message);
        record.headers().add(KafkaHeaders.REPLY_TOPIC, "result".getBytes());
        template.sendAndReceive(record).addCallback(
                result -> Optional.ofNullable(result)
                        .map(ConsumerRecord::value)
                        .ifPresent(future::complete),
                future::completeExceptionally);

        return future;
    }

    private <T> T resolveFuture(Future<T> future) {
        try {
            //todo waiting time MUST BE lower than tcp socket timeout (in rest request)
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private Optional<String> findTopicForOperator(String operator) {
        String topic;
        switch (operator) {
            case "+":
                topic = "plus";
                break;
            case "-":
                topic = "minus";
                break;
            case "*":
                topic = "multiply";
                break;
            case "/":
                topic = "divide";
                break;
            default:
                topic = null;
        }

        return Optional.ofNullable(topic);
    }
}
