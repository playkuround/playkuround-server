package com.playkuround.playkuroundserver.domain.landmark.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.landmark.application.AdventureService;
import com.playkuround.playkuroundserver.domain.landmark.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.RequestSaveAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.domain.dao.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc //MockMvc 사용
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
class AdventureControllerTest {

    @Autowired
    private ObjectMapper objectMapper; // 스프링에서 자동으로 주입해줌

    @Autowired
    private AdventureService adventureService;

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

        RequestSaveAdventure requestSaveAdventure = new RequestSaveAdventure(1L, 0D, 0D);
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

    @Test
    @DisplayName("로그인 회원의 탐험 기록 조회")
    void findAdventure() throws Exception {
        // given
        User user = userRepository.save(new User("test@email.com", "nickname", Major.CS));
        TokenDto tokenDto = tokenManager.createTokenDto(user.getEmail());

        adventureService.saveAdventure(user.getEmail(), new RequestSaveAdventure(1L, 0D, 0D));
        adventureService.saveAdventure(user.getEmail(), new RequestSaveAdventure(2L, 0D, 0D));
        adventureService.saveAdventure(user.getEmail(), new RequestSaveAdventure(3L, 0D, 0D));
        adventureService.saveAdventure(user.getEmail(), new RequestSaveAdventure(4L, 0D, 0D));

        // expected
        MvcResult result = mockMvc.perform(get("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        List<ResponseFindAdventure> response =
                objectMapper.readValue(responseBody, new TypeReference<List<ResponseFindAdventure>>() {
                });


        assertEquals(4, response.size());
        assertThat(response, Matchers.containsInAnyOrder(
                hasProperty("landmarkId", is(1L)),
                hasProperty("landmarkId", is(2L)),
                hasProperty("landmarkId", is(3L)),
                hasProperty("landmarkId", is(4L))
        ));
    }

    @Test
    @DisplayName("특정 랜드마크에 가장 많이 방문한 회원 조회")
    void findMemberMostAdventure() throws Exception {
        // given
        User user1 = userRepository.save(new User("test@email.com", "tester1", Major.CS));
        User user2 = userRepository.save(new User("test2@email.com", "tester2", Major.CS));

        adventureService.saveAdventure(user1.getEmail(), new RequestSaveAdventure(1L, 0D, 0D));
        adventureService.saveAdventure(user1.getEmail(), new RequestSaveAdventure(1L, 0D, 0D));
        adventureService.saveAdventure(user2.getEmail(), new RequestSaveAdventure(1L, 0D, 0D));

        // expected
        // 1. 한 번이라도 더 방문한 회원 응답
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("tester1"))
                .andExpect(jsonPath("$.count").value(2))
                .andDo(print());

        // 2. 방문 횟수가 같다면, 방문한지 오래된 회원 응답
        adventureService.saveAdventure(user2.getEmail(), new RequestSaveAdventure(1L, 0D, 0D));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("tester1"))
                .andExpect(jsonPath("$.count").value(2))
                .andDo(print());

        adventureService.saveAdventure(user2.getEmail(), new RequestSaveAdventure(1L, 0D, 0D));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("tester2"))
                .andExpect(jsonPath("$.count").value(3))
                .andDo(print());

    }
}