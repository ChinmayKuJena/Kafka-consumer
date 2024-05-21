package com.kafkaconsumer.kafkaconsumer.Service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kafkaconsumer.kafkaconsumer.Db.DynamicTableService;
import com.kafkaconsumer.kafkaconsumer.config.ConsumerProperties;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    @Autowired
    private ConsumerProperties consumerProperties;
    private volatile boolean shouldContinue = true;
    private List<KafkaConsumer<String, String>> consumers = new ArrayList<>();
    private List<Thread> consumerThreads = new ArrayList<>();
    private DynamicTableService dynamicTableService;

    public KafkaConsumerService(ConsumerProperties consumerProperties, DynamicTableService dynamicTableService) {
        this.consumerProperties = consumerProperties;
        this.dynamicTableService = dynamicTableService;
    }

    public void subscribeToTopic(String topic) {
        consumerSubscribeToTopic(topic);
    }

    private void consumerSubscribeToTopic(String topic) {
        try {

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties.properties());
            consumer.subscribe(List.of(topic));
            consumers.add(consumer);

            logger.info("Kafka consumer started for topic: {}", topic);

            Thread consumerThread = new Thread(() -> {
                while (shouldContinue) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        dynamicTableService.createTableAndInsert(topic, record.value());
                        logger.info("Received message from topic = {}: value={}", record.topic(), record.value());
                    }
                }
                consumer.close();
            });
            consumerThreads.add(consumerThread);
            consumerThread.start();
        } catch (Exception e) {

            logger.error("Error occurred while subscribing to topic: {}", topic, e);
            logger.error("Error occurred while subscribing to topic: {}", topic, e);
        }
    }

    // close all the kafka consumers
    public void shutdown() {
        shouldContinue = false;
        for (Thread consumerThread : consumerThreads) {
            try {

                consumerThread.join();
                logger.info(
                        "consumer closed...........");
            } catch (InterruptedException e) {
                logger.error("Error while waiting for consumer threads to finish.", e);
            }
        }
        for (KafkaConsumer<String, String> consumer : consumers) {
            consumer.close(); // close all the kafka consumers
        }
    }
}
