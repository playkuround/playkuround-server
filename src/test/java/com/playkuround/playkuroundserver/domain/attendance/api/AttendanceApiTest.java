package com.playkuround.playkuroundserver.domain.attendance.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceRegisterDto;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc //MockMvc 사용
@SpringBootTest
@ActiveProfiles("test")
class AttendanceApiTest {

    @Autowired
    private ObjectMapper objectMapper; // 스프링에서 자동으로 주입해줌

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AttendanceRegisterService attendanceRegisterService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clean() {
        badgeRepository.deleteAll();
        attendanceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("출석 + 최초 출석 배지 받기")
    void saveAttendance() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        AttendanceRegisterDto.Request request = new AttendanceRegisterDto.Request(37.539927, 127.073006);
        String content = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/attendances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.newBadges[?(@.name == '%s')]", "ATTENDANCE_1").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("중복 출석 요청 - 에러발생")
    void duplicateAttendance() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        // 오늘 출석 완료
        AttendanceRegisterDto.Request request = new AttendanceRegisterDto.Request(37.539927, 127.073006);
        attendanceRegisterService.registerAttendance(userEmail, request);

        String content = objectMapper.writeValueAsString(request);
        // expected - 한번 더 출석
        mockMvc.perform(post("/api/attendances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(400))
                .andExpect(jsonPath("$.errorResponse.code").value("AT01"))
                .andExpect(jsonPath("$.errorResponse.message").value("이미 오늘 출석한 회원입니다."))
                .andDo(print());
    }
}