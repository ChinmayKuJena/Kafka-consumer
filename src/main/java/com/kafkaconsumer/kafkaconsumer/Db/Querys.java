package com.kafkaconsumer.kafkaconsumer.Db;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class Querys {
    org.slf4j.Logger log = LoggerFactory.getLogger(Querys.class);

    public String generateCreateTableQuery(String tableName, JsonNode jsonNode) {
        return generateCreateTableQuery2(tableName, jsonNode);
    }

    private String generateCreateTableQuery2(String tableName, JsonNode jsonNode) {
        StringBuilder createTableQuery = new StringBuilder(
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" + "id SERIAL PRIMARY KEY, ");
        try {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> fieldEntry = fields.next();
                String fieldName = fieldEntry.getKey();
                createTableQuery.append(fieldName).append(" VARCHAR(255), ");
            }
            createTableQuery.delete(createTableQuery.length() - 2, createTableQuery.length());
            createTableQuery.append(")");

            return createTableQuery.toString();
        } catch (Exception e) {
            log.error("Error generating CREATE TABLE query: ", e);
            return null;
        }
    }

    public String generateInsertDataQuery(String tableName, JsonNode jsonNode) {
        return generateInsertDataQuery2(tableName, jsonNode);
    }

    private String generateInsertDataQuery2(String tableName, JsonNode jsonNode) {
        StringBuilder insertDataQuery = new StringBuilder("INSERT INTO " + tableName + " (");
        try {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> fieldEntry = fields.next();
                String fieldName = fieldEntry.getKey();
                insertDataQuery.append(fieldName).append(", ");
            }

            insertDataQuery.delete(insertDataQuery.length() - 2, insertDataQuery.length());
            insertDataQuery.append(") VALUES (");

            fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> fieldEntry = fields.next();
                insertDataQuery.append("'").append(fieldEntry.getValue().asText()).append("', ");
            }

            insertDataQuery.delete(insertDataQuery.length() - 2, insertDataQuery.length());
            insertDataQuery.append(")");

            return insertDataQuery.toString();
        } catch (Exception e) {
            log.error("Error generating INSERT INTO query: ", e);
            return null;
        }
    }
}
