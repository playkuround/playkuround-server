package com.playkuround.playkuroundserver.domain.attendance.api;

import com.jayway.jsonpath.JsonPath;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class AttendanceSearchApiTest {

    @MockBean
    DateTimeProvider dateTimeProvider;
    @SpyBean
    AuditingHandler auditingHandler;
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
    @DisplayName("지난 한달 출석 조회")
    void attendanceSearch() throws Exception {
        // given
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);

        User user = userFindDao.findByEmail("tester@konkuk.ac.kr");
        Location location = new Location(37.539927, 127.073006);

        LocalDateTime todayLocalDate = LocalDateTime.now();
        List<String> dateList = new ArrayList<>();
        for (int i = 29; i > 0; i -= 2) {
            LocalDateTime thatLocalDateTime = todayLocalDate.minusDays(i);
            String formatDate = thatLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateList.add(formatDate);

            doReturn(Optional.of(thatLocalDateTime)).when(dateTimeProvider).getNow();
            attendanceRegisterService.registerAttendance(user, location);
        }

        // expected
        MvcResult mvcResult = mockMvc.perform(get("/api/attendances")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        List<String> target = JsonPath.parse(json).read("$.response.attendances");

        assertThat(dateList).isEqualTo(target);
    }

}
