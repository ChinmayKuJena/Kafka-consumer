# Tourist Guide Web Application - Kafka Consumer Microservice

## Developer: Chinmay Kumar Jena


## Project Overview

This repository contains the Kafka consumer microservice for the Tourist Guide Web Application. This microservice dynamically consumes messages from any Kafka topic specified by the user and saves the data into a PostgreSQL database. The messages are consumed using a single consumer, enabling a flexible and efficient way to handle different topics.

## Key Features

- **Dynamic Topic Subscription:**
  - Allows consumption of messages from any Kafka topic specified by the user.

- **Single Consumer:**
  - Utilizes a single consumer to dynamically handle multiple topics without manual configuration.

- **Data Persistence:**
  - Saves consumed messages into a PostgreSQL database.

---

## Technologies Used

- **Backend Framework:** Spring Boot
- **Build Tool:** Gradle
- **Database:** PostgreSQL
- **Messaging Queue:** Apache Kafka
- **Consumer Management:** Dynamic Kafka consumer

---
