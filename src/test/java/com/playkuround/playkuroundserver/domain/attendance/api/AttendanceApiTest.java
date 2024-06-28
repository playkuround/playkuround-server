package com.playkuround.playkuroundserver.domain.attendance.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.domain.attendance.api.request.AttendanceRegisterRequest;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.util.Location;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
class AttendanceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String redisSetKey = "ranking";

    @AfterEach
    void clean() {
        attendanceRepository.deleteAllInBatch();
        badgeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        redisTemplate.delete(redisSetKey);
    }

    @Nested
    @DisplayName("출석 저장하기")
    class saveAttendance {

        @Test
        @WithMockCustomUser
        @DisplayName("최초로 출석을 하면 뱃지를 받는다.")
        void success_1() throws Exception {
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
                    .andExpect(jsonPath("$.response.newBadges.size()").value(1))
                    .andExpect(jsonPath("$.response.newBadges[0].name").value(BadgeType.ATTENDANCE_1.name()))
                    .andExpect(jsonPath("$.response.newBadges[0].description").value(BadgeType.ATTENDANCE_1.getDescription()))
                    .andDo(print());

            User user = userRepository.findAll().get(0);
            assertThat(user.getAttendanceDays()).isEqualTo(1);

            List<Badge> badges = badgeRepository.findAll();
            assertThat(badges.size()).isEqualTo(1);
            assertThat(badges.get(0).getUser().getId()).isEqualTo(user.getId());
            assertThat(badges.get(0).getBadgeType()).isEqualTo(BadgeType.ATTENDANCE_1);

            List<Attendance> attendances = attendanceRepository.findAll();
            assertThat(attendances.size()).isEqualTo(1);
            assertThat(attendances.get(0).getUser().getId()).isEqualTo(user.getId());

            ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
            Double myTotalScore = zSetOperations.score(redisSetKey, user.getEmail());
            assertThat(myTotalScore).isEqualTo(10.0);
        }

        @Test
        @WithMockCustomUser
        @DisplayName("출석은 하루에 한번만 가능하다.")
        void fail_1() throws Exception {
            // TODO : 12시 넘어가는 시점에는 테스트가 실패함
            // given
            User user = userRepository.findAll().get(0);
            attendanceRepository.save(Attendance.of(user, new Location(37.539927, 127.073006)));

            AttendanceRegisterRequest attendanceRegisterRequest = new AttendanceRegisterRequest(37.539927, 127.073006);
            String request = objectMapper.writeValueAsString(attendanceRegisterRequest);

            // expected
            mockMvc.perform(post("/api/attendances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.DUPLICATE_ATTENDANCE.getCode()))
                    .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.DUPLICATE_ATTENDANCE.getMessage()))
                    .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.DUPLICATE_ATTENDANCE.getStatus().value()))
                    .andDo(print());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("출석은 건대 내부에서 해야한다.")
        void fail_2() throws Exception {
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
                    .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_LOCATION_KU.getCode()))
                    .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_LOCATION_KU.getMessage()))
                    .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_LOCATION_KU.getStatus().value()))
                    .andDo(print());
        }
    }


    @Nested
    @DisplayName("출석 조회하기")
    class searchAttendance {

        @MockBean
        private DateTimeProvider dateTimeProvider;

        @SpyBean
        private AuditingHandler auditingHandler;

        @Autowired
        private AttendanceRegisterService attendanceRegisterService;

        @Test
        @WithMockCustomUser
        @DisplayName("지난 한달 출석 조회")
        void attendanceSearch() throws Exception {
            // TODO : need refactoring
            // given
            MockitoAnnotations.openMocks(this);
            auditingHandler.setDateTimeProvider(dateTimeProvider);

            User user = userRepository.findAll().get(0);
            Location location = new Location(37.539927, 127.073006);

            LocalDateTime todayLocalDate = LocalDateTime.now();
            List<String> dateList = new ArrayList<>();
            for (int i = 29; i > 0; i -= 2) {
                LocalDateTime thatLocalDateTime = todayLocalDate.minusDays(i);
                String formatDate = thatLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dateList.add(formatDate);

                when(dateTimeProvider.getNow()).thenReturn(Optional.of(thatLocalDateTime));
                attendanceRegisterService.registerAttendance(user, location);
            }

            // expected
            MvcResult mvcResult = mockMvc.perform(get("/api/attendances"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andDo(print())
                    .andReturn();
            String json = mvcResult.getResponse().getContentAsString();
            List<String> target = JsonPath.parse(json).read("$.response.attendances");

            assertThat(dateList).isEqualTo(target);
        }
    }
}
