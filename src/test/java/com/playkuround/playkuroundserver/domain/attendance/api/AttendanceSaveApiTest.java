package com.playkuround.playkuroundserver.domain.attendance.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.dto.request.AttendanceRegisterRequest;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.util.Location;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class AttendanceSaveApiTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFindDao userFindDao;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AttendanceRegisterService attendanceRegisterService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
        badgeRepository.deleteAll();
        attendanceRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("출석 + 최초 출석 배지 받기")
    void saveAttendance() throws Exception {
        // given
        AttendanceRegisterRequest attendanceRegisterRequest = new AttendanceRegisterRequest(37.539927, 127.073006);
        String request = objectMapper.writeValueAsString(attendanceRegisterRequest);

        // expected
        mockMvc.perform(post("/api/attendances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.newBadges[?(@.name == '%s')]", BadgeType.ATTENDANCE_1.name()).exists())
                .andExpect(jsonPath("$.response.newBadges[?(@.description == '%s')]", BadgeType.ATTENDANCE_1.getDescription()).exists())
                .andDo(print());

        List<Badge> badges = badgeRepository.findAll();
        assertThat(badges.size()).isEqualTo(1);
        assertThat(badges.get(0).getBadgeType()).isEqualTo(BadgeType.ATTENDANCE_1);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("에러발생 - 중복 출석 요청")
    void duplicateAttendance() throws Exception {
        // given
        User user = userFindDao.findByEmail("tester@konkuk.ac.kr");
        AttendanceRegisterRequest attendanceRegisterRequest = new AttendanceRegisterRequest(37.539927, 127.073006);
        attendanceRegisterService.registerAttendance(user, new Location(37.539927, 127.073006));

        String request = objectMapper.writeValueAsString(attendanceRegisterRequest);

        // expected
        mockMvc.perform(post("/api/attendances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.DUPLICATE_ATTENDANCE.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.DUPLICATE_ATTENDANCE.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.DUPLICATE_ATTENDANCE.getMessage()))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("에러발생 - 건대 밖에서 출석")
    void attendanceNotInKU() throws Exception {
        // given
        AttendanceRegisterRequest attendanceRegisterRequest = new AttendanceRegisterRequest(0.0, 0.0);
        String request = objectMapper.writeValueAsString(attendanceRegisterRequest);

        // expected
        mockMvc.perform(post("/api/attendances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_LOCATION_KU.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_LOCATION_KU.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_LOCATION_KU.getMessage()))
                .andDo(print());
    }

}
