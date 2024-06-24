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
