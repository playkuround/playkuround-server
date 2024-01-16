package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Test
    void 한달동안의_출석정보가_정렬되어_반환된다() {
        // given
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
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
                    Attendance attendance = Attendance.createAttendance(user, location);
                    ReflectionTestUtils.setField(attendance, "createdAt", now.plusDays(x));
                    attendances.add(attendance);
                    expected.add(attendance.getCreatedAt());
                });
        when(attendanceRepository.findByUserAndCreatedAtAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(attendances);

        // when
        List<LocalDateTime> target = attendanceSearchService.findAttendanceForMonth(user);

        // then
        assertThat(target).isEqualTo(expected);
    }

    @Test
    void 출석정보가_없으면_빈리스트가_반환된다() {
        // given
        when(attendanceRepository.findByUserAndCreatedAtAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // when
        User user = TestUtil.createUser();
        List<LocalDateTime> target = attendanceSearchService.findAttendanceForMonth(user);

        // then
        assertThat(target).isEmpty();
    }
}