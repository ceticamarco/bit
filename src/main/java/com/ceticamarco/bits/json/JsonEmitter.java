package com.ceticamarco.bits.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonEmitter<T> {
    private ObjectMapper objectMapper;
    private T entity;

    public JsonEmitter(T entity) {
        // Initialize and configure object mapper
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        this.entity = entity;
    }

    public String emitJsonKey() {
        String jsonString = "";

        try {
            jsonString = objectMapper.writeValueAsString(this.entity);
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        return jsonString;
    }

    public String emitJsonKey(String key) {
        String jsonString = "";

        try {
            var jsonNode = objectMapper.createObjectNode().put(key, this.entity.toString());
            jsonString = objectMapper.writeValueAsString(jsonNode);
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        return jsonString;
    }
}
