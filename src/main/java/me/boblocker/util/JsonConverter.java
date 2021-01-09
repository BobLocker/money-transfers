package me.boblocker.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.boblocker.core.annotation.Singleton;
import me.boblocker.exception.JsonConverterException;

import java.io.IOException;

@Singleton
public class JsonConverter {
    private final ObjectMapper objectMapper;

    public JsonConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper = objectMapper;
    }


    public <T> String toJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new JsonConverterException("Exception with converting to JSON", ex);
        }
    }

    public <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException ex) {
            throw new JsonConverterException("Exception with converting from JSON", ex);
        }
    }
}
