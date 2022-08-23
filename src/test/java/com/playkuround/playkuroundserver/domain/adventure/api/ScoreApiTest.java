package com.playkuround.playkuroundserver.domain.adventure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.score.application.ScoreService;
import com.playkuround.playkuroundserver.domain.score.dao.ScoreFindDao;
import com.playkuround.playkuroundserver.domain.score.dao.ScoreRepository;
import com.playkuround.playkuroundserver.domain.score.domain.Score;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.score.dto.SaveScore;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc //MockMvc 사용
@SpringBootTest
@ActiveProfiles("test")
class ScoreApiTest {

    @Autowired
    private ObjectMapper objectMapper; // 스프링에서 자동으로 주입해줌

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScoreFindDao scoreFindDao;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clean() {
        scoreRepository.deleteAll();
        userRepository.deleteAll();
    }

    private final String userEmail = "test@email.com";
    private String accessToken;


    @BeforeEach
    void registerUser() {
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        accessToken = userLoginService.login(userEmail).getAccessToken();
    }

    @Test
    @DisplayName("score 저장(출석)")
    void saveScoreAttendance() throws Exception {
        // given
        //String content = objectMapper.writeValueAsString(new SaveScore("ATTENDANCE"));
        String content = objectMapper.writeValueAsString(new SaveScore(ScoreType.ATTENDANCE));

        // expected
        mockMvc.perform(post("/api/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andDo(print());

        assertEquals(1L, scoreRepository.count());
        Score score = scoreRepository.findAll().get(0);
        assertEquals(ScoreType.ATTENDANCE, score.getScoreType());
        assertEquals(ScoreType.ATTENDANCE.getPoint(), scoreFindDao.findTotalScorePointByUserEmail(userEmail));
    }

    @Test
    @DisplayName("score 저장(탐험)")
    void saveScoreAdventure() throws Exception {
        // given
        //String content = objectMapper.writeValueAsString(new SaveScore("ADVENTURE"));
        String content = objectMapper.writeValueAsString(new SaveScore(ScoreType.ADVENTURE));

        // expected
        mockMvc.perform(post("/api/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andDo(print());

        assertEquals(1L, scoreRepository.count());
        Score score = scoreRepository.findAll().get(0);
        assertEquals(ScoreType.ADVENTURE, score.getScoreType());
        assertEquals(ScoreType.ADVENTURE.getPoint(), scoreFindDao.findTotalScorePointByUserEmail(userEmail));
    }

    @Test
    @DisplayName("score 저장(추가 탐험)")
    void saveScoreExtraAdventure() throws Exception {
        // given
        //String content = objectMapper.writeValueAsString(new SaveScore("EXTRA_ADVENTURE"));
        String content = objectMapper.writeValueAsString(new SaveScore(ScoreType.EXTRA_ADVENTURE));

        // expected
        mockMvc.perform(post("/api/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andDo(print());

        assertEquals(1L, scoreRepository.count());
        Score score = scoreRepository.findAll().get(0);
        assertEquals(ScoreType.EXTRA_ADVENTURE, score.getScoreType());
        assertEquals(ScoreType.EXTRA_ADVENTURE.getPoint(), scoreFindDao.findTotalScorePointByUserEmail(userEmail));
    }

}
