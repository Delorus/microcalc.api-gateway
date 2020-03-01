package ru.sherb.microcalc.apiservice.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;
import ru.sherb.microcalc.apiservice.service.ExpressionSender;
import ru.sherb.microcalc.expr.ExprPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * @author maksim
 * @since 29.02.2020
 */
@Service
public class KafkaClient implements ExpressionSender {

    private final ReplyingKafkaTemplate<String, ExprPartMessage, ResultResponse> template;

    @Autowired
    public KafkaClient(ReplyingKafkaTemplate<String, ExprPartMessage, ResultResponse> template) {
        this.template = template;
    }

    @Override
    public List<Map.Entry<ExprPart, Number>> sendPart(ExprPart[] parts) {
        Map<String, ExprPart> partsById = new HashMap<>();
        List<RequestReplyFuture<String, ExprPartMessage, ResultResponse>> futures = new ArrayList<>();
        for (ExprPart part : parts) {
            Optional<String> maybeTopic = findTopicForOperator(part.operator());
            if (maybeTopic.isEmpty()) {
                continue;
            }

            ExprPartMessage message = new ExprPartMessage(part.args());
            partsById.put(message.getId(), part);
            futures.add(template.sendAndReceive(new ProducerRecord<>(maybeTopic.get(), message)));
        }

        List<Map.Entry<ExprPart, Number>> result = new ArrayList<>();
        for (RequestReplyFuture<String, ExprPartMessage, ResultResponse> future : futures) {
            try {
                ExprPartMessage partMessage = future.getSendFuture().get().getProducerRecord().value();
                ResultResponse record = future.get().value();
                ExprPart part = partsById.get(partMessage.getId());
                result.add(Map.entry(part, record.getResult()));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
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
