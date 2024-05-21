package com.kafkaconsumer.kafkaconsumer.Db;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;

@Service
public class DynamicTableService {

    private final Logger log = (Logger) LoggerFactory.getLogger(DynamicTableService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private Querys querys;

    public DynamicTableService(JdbcTemplate jdbcTemplate, Querys querys) {
        this.jdbcTemplate = jdbcTemplate;
        this.querys = querys;
    }

    public void createTableAndInsert(String topic, String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);

            String tableName = "table_" + topic.toLowerCase();
            try {
                if (!tableExists(tableName)) {
                    log.info("Creating new........ ==> " + tableName);
                    String createTableQuery = querys.generateCreateTableQuery(tableName, jsonNode);
                    jdbcTemplate.execute(createTableQuery);
                    log.info("Created ==> " + tableName);
                } else {
                    log.info(tableName + " already exists............");
                }
            } catch (Exception e) {
                log.error("Error = " + e);
            }
            try {

                log.info("Inserting Data......");
                String insertDataQuery = querys.generateInsertDataQuery(tableName, jsonNode);
                jdbcTemplate.update(insertDataQuery);
                log.info("Inserting Data Done......");
            } catch (Exception e) {
                log.error("Error == " + e);
            }

        } catch (JsonProcessingException e) {
            log.error("Error == " + e);
        }
    }

    private boolean tableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
            int count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count > 0;

        } catch (Exception e) {
            log.error("Error == " + e);
            return false;
        }
    }
}

// private String generateCreateTableQuery(String tableName, JsonNode jsonNode)
// {
// StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT
// EXISTS " + tableName + " ("
// + "id SERIAL PRIMARY KEY, ");
// try {
// /*
// * here field name is extracting from jsonnode
// * using iterator over jsonnode
// */
// Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
// while (fields.hasNext()) {
// Map.Entry<String, JsonNode> fieldEntry = fields.next();
// String fieldName = fieldEntry.getKey();
// createTableQuery.append(fieldName).append(" VARCHAR(255), ");
// }

// createTableQuery.delete(createTableQuery.length() - 2,
// createTableQuery.length()); // Remove trailing comma
// createTableQuery.append(")");

// return createTableQuery.toString();

// } catch (Exception e) {
// log.error("error = " + e);
// return null;
// }
// }

// private String generateInsertDataQuery(String tableName, JsonNode jsonNode) {
// StringBuilder insertDataQuery = new StringBuilder("INSERT INTO " + tableName
// + " (");

// Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
// while (fields.hasNext()) {
// Map.Entry<String, JsonNode> fieldEntry = fields.next();
// String fieldName = fieldEntry.getKey();
// insertDataQuery.append(fieldName).append(", ");
// }

// insertDataQuery.delete(insertDataQuery.length() - 2,
// insertDataQuery.length());
// insertDataQuery.append(") VALUES (");

// fields = jsonNode.fields();
// while (fields.hasNext()) {
// Map.Entry<String, JsonNode> fieldEntry = fields.next();
// insertDataQuery.append("'").append(fieldEntry.getValue().asText()).append("'
// ,");
// }

// insertDataQuery.delete(insertDataQuery.length() - 2,
// insertDataQuery.length());
// insertDataQuery.append(")");

// return insertDataQuery.toString();
// }