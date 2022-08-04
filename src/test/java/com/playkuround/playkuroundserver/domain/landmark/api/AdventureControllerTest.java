package com.playkuround.playkuroundserver.domain.landmark.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.landmark.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.RequestSaveAdventure;
import com.playkuround.playkuroundserver.domain.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.domain.dao.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc //MockMvc 사용
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
class AdventureControllerTest {

    @Autowired
    private ObjectMapper objectMapper; // 스프링에서 자동으로 주입해줌

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdventureRepository adventureRepository;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clean() {
        adventureRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("탐험 저장")
    void saveAdventure() throws Exception {

        // given
        User user = userRepository.save(new User("test@email.com", "nickname", Major.CS));
        TokenDto tokenDto = tokenManager.createTokenDto(user.getEmail());

        RequestSaveAdventure requestSaveAdventure = new RequestSaveAdventure(1L, 0d, 0d);
        String content = objectMapper.writeValueAsString(requestSaveAdventure);


        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                )
                .andExpect(status().isCreated())
                .andDo(print());

        assertEquals(1L, adventureRepository.count());
        Adventure adventure = adventureRepository.findAll().get(0);

        assertEquals(1L, adventure.getLandmark().getId());
        assertEquals(user.getId(), adventure.getUser().getId());


    }
}