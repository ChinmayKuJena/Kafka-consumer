package com.kafkaconsumer.kafkaconsumer.Controller;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import com.kafkaconsumer.kafkaconsumer.Service.KafkaConsumerService;

@RestController
public class KafkaDynamicConsumerController {
    private static final Logger log = LoggerFactory.getLogger(KafkaDynamicConsumerController.class);

    private final KafkaConsumerService kafkaConsumerService;

    public KafkaDynamicConsumerController(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @GetMapping("/kafka/consume/{topics}")
    public String consumeMessagesFromTopics(@PathVariable String topics) {
        // kafkaConsumerService.topicNameSubscribe(topics);
        try {
            kafkaConsumerService.subscribeToTopic(topics);
            return "Consuming messages from topics: " + topics;
        } catch (Exception e) {
            log.error("Error=== ", e);
            return "Wait And retry";
        }
    }

    // @PostMapping("/unsubscribe/{topic}")
    // public String unsubscribeFromTopic(@PathVariable String topic) {
    // kafkaConsumerService.unsubscribeFromTopic(topic);
    // return "Unsubscribed from topic: " + topic;
    // }

    @PostMapping("/close")
    public String closeConsumers() {
        kafkaConsumerService.shutdown();
        return "Closed all consumers";
    }
}
