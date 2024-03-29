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
import java.util.Map;

public class TestUtil {
    public static <T> T convertFromJsonStringToObject(String json, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        Map<String, Object> apiResponse = mapper.readValue(json, new TypeReference<>() {
        });
        return mapper.convertValue(apiResponse.get("response"), objectClass);
    }

    public static User createUser() {
        return createUser("tester@konkuk.ac.kr", "tester", Major.컴퓨터공학부);
    }

    public static User createUser(String email, String nickname, Major major) {
        return User.create(email, nickname, major, Role.ROLE_USER);
    }

    private TestUtil() {
    }
}
