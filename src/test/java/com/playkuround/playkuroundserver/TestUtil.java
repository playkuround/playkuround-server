package com.playkuround.playkuroundserver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestUtil {
    public static <T> Object convertFromJsonStringToObject(String json, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        Map<String, Object> apiResponse = mapper.readValue(json, new TypeReference<>() {
        });
        return mapper.convertValue(apiResponse.get("response"), objectClass);
    }

    public static Object getJsonValue(String json, String target) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        Map<String, Object> apiResponse = mapper.readValue(json, new TypeReference<>() {
        });
        LinkedHashMap<String, Object> response = (LinkedHashMap) apiResponse.get("response");
        return response.get(target);
    }

    public static User createUser() {
        return createUser("tester@konkuk.ac.kr", "tester", Major.컴퓨터공학부);
    }

    public static User createUser(String email, String nickname, Major major) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .major(major)
                .role(Role.ROLE_USER)
                .build();
    }

    private TestUtil() {
    }
}
