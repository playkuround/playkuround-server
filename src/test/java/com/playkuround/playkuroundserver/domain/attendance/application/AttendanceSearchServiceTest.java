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
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
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
    @DisplayName("한달동안의 출석정보가 정렬되어 반환된다")
    void findAttendanceForMonth_1() {
        // given
        when(dateTimeService.getLocalDateNow())
                .thenReturn(LocalDate.of(2024, 7, 1));

        Random random = new Random();
        LocalDateTime now = LocalDateTime.of(2024, 7, 1, 0, 0);
        User user = TestUtil.createUser();
        Location location = new Location(37.539927, 127.073006);

        List<Attendance> attendances = new ArrayList<>();
        List<LocalDateTime> expected = new ArrayList<>();
        IntStream.iterate(1, x -> x + 1)
                .limit(30)
                .map(x -> random.nextInt(30))
                .distinct()
                .sorted()
                .forEach(x -> {
                    Attendance attendance = Attendance.of(user, location, now.plusDays(x));

                    attendances.add(attendance);
                    expected.add(attendance.getAttendanceDateTime());
                });
        when(attendanceRepository.findByUserAndAttendanceDateTimeAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(attendances);


        // when
        List<LocalDateTime> result = attendanceSearchService.findAttendance(user, 30);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("출석정보가 없으면 빈리스트가 반환된다")
    void findAttendanceForMonth_2() {
        // given
        when(dateTimeService.getLocalDateNow())
                .thenReturn(LocalDate.of(2024, 7, 1));
        when(attendanceRepository.findByUserAndAttendanceDateTimeAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // when
        User user = TestUtil.createUser();
        List<LocalDateTime> result = attendanceSearchService.findAttendance(user, 30);

        // then
        assertThat(result).isEmpty();
    }
}