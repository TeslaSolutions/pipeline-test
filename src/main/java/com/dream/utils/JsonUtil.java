package com.dream.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class JsonUtil {
    @Autowired
    private ObjectMapper objectMapper;

    private static ObjectMapper objectMapperInstance;

    @PostConstruct
    public void postConstruct() {
        objectMapperInstance = objectMapper;
    }

    static {

        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);


        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.findAndRegisterModules();

        objectMapperInstance = objectMapper;

    }

    public static <T> String toJson(T object) {
        try {
            String s = objectMapperInstance.writeValueAsString(object);
            return s;
        } catch (JsonProcessingException e) {
            return "<JSON ERROR>";
        }
    }

    public static <T> T fromJson(final TypeReference<T> type, final String str) {
        T data = null;
        try {
            data = objectMapperInstance.readValue(str, type);
        } catch (Exception e) {
            // Handle the problem
        }
        return data;
    }

}
