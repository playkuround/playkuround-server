package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceSearchServiceTest {

    @InjectMocks
    private AttendanceSearchService attendanceSearchService;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private DateTimeService dateTimeService;

    @Test
    @DisplayName("30일 동안의 출석정보가 정렬되어 반환된다")
    void findAttendanceForMonth_1() {
        // given
        LocalDate localDate = LocalDate.of(2024, 7, 1);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);

        User user = TestUtil.createUser();
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Location location = new Location(37.539927, 127.073006);
        List<Attendance> attendances = List.of(
                Attendance.of(user, location, localDateTime.minusDays(1)),
                Attendance.of(user, location, localDateTime.minusDays(2)),
                Attendance.of(user, location, localDateTime.minusDays(15)),
                Attendance.of(user, location, localDateTime.minusDays(29)),
                Attendance.of(user, location, localDateTime.minusDays(30))
        );

        int agoDays = 30;
        LocalDateTime agoDateTime = localDate.minusDays(agoDays).atStartOfDay();
        when(attendanceRepository.findByUserAndAttendanceDateTimeGreaterThanEqual(user, agoDateTime))
                .thenReturn(attendances);

        // when
        List<LocalDateTime> result = attendanceSearchService.findAttendance(user, agoDays);

        // then
        List<LocalDateTime> expected = attendances.stream()
                .map(Attendance::getAttendanceDateTime)
                .sorted()
                .toList();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("출석정보가 없으면 빈리스트가 반환된다")
    void findAttendanceForMonth_2() {
        // given
        LocalDate localDate = LocalDate.of(2024, 7, 1);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);

        User user = TestUtil.createUser();
        int agoDays = 30;
        LocalDateTime agoDateTime = localDate.minusDays(agoDays).atStartOfDay();
        when(attendanceRepository.findByUserAndAttendanceDateTimeGreaterThanEqual(user, agoDateTime))
                .thenReturn(new ArrayList<>());

        // when
        List<LocalDateTime> result = attendanceSearchService.findAttendance(user, agoDays);

        // then
        assertThat(result).isEmpty();
    }

}