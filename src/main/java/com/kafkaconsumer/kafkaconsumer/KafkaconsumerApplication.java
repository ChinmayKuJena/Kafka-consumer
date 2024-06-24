package com.kafkaconsumer.kafkaconsumer;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaconsumerApplication {
	static org.slf4j.Logger log = LoggerFactory.getLogger(KafkaconsumerApplication.class);

	// later add config datas
	public static void main(String[] args) {
		log.info("http://localhost:9999/kafka/consume/abc");
		SpringApplication.run(KafkaconsumerApplication.class, args);

	}

}
