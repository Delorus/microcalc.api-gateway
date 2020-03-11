package ru.sherb.microcalc.apiservice.kafka;

/**
 * @author maksim
 * @since 09.03.2020
 */
public class TopicNotFoundException extends RuntimeException {

    public TopicNotFoundException(String operator) {
        super("Not found topic for operator = [" + operator + "]");
    }
}
