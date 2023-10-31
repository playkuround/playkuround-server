package com.playkuround.playkuroundserver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Map;

public class TestUtil {
    public static <T> Object convertjsonstringtoobject(String json, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        Map<String, Object> apiResponse = mapper.readValue(json, new TypeReference<>() {
        });
        return mapper.convertValue(apiResponse.get("response"), objectClass);
    }
}