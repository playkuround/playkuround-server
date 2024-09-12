package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.exception.DuplicateAttendanceException;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.score.application.TotalScoreService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceRegisterServiceTest {

    @InjectMocks
    private AttendanceRegisterService attendanceRegisterService;

    @Mock
    private BadgeService badgeService;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TotalScoreService totalScoreService;

    @Mock
    private DateTimeService dateTimeService;

    @Test
    @DisplayName("출석 시 배지와 출석정보가 저장되고 유저의 출석횟수가 증가한다")
    void registerAttendance_1() {
        // given
        User user = TestUtil.createUser();
        LocalDate localDate = LocalDate.of(2024, 6, 30);

        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);
        when(attendanceRepository.existsByUserAndAttendanceDateTimeGreaterThanEqual(user, localDate.atStartOfDay()))
                .thenReturn(false);
        when(badgeService.updateNewlyAttendanceBadges(user))
                .thenReturn(List.of(BadgeType.ATTENDANCE_1, BadgeType.ATTENDANCE_ARBOR_DAY));

        Location location = new Location(37.539927, 127.073006);

        // when
        List<BadgeType> result = attendanceRegisterService.registerAttendance(user, location);

        // then
        assertThat(user.getAttendanceDays()).isEqualTo(1);
        assertThat(result).hasSize(2)
                .containsExactlyInAnyOrder(BadgeType.ATTENDANCE_1, BadgeType.ATTENDANCE_ARBOR_DAY);

        ArgumentCaptor<Attendance> attendanceArgument = ArgumentCaptor.forClass(Attendance.class);
        verify(attendanceRepository, times(1)).save(attendanceArgument.capture());
        Attendance attendance = attendanceArgument.getValue();
        assertThat(attendance.getUser()).isEqualTo(user);
        assertThat(attendance.getLatitude()).isEqualTo(location.latitude());
        assertThat(attendance.getLongitude()).isEqualTo(location.longitude());
    }

    @Test
    @DisplayName("출석 범위에 벗어나면 에러가 발생한다")
    void registerAttendance_2() {
        // given
        User user = TestUtil.createUser();
        Location location = new Location(0.0, 0.0);

        // expect
        assertThatThrownBy(() -> attendanceRegisterService.registerAttendance(user, location))
                .isInstanceOf(InvalidAttendanceLocationException.class);
    }

    @Test
    @DisplayName("출석은 하루에 한번만 가능하다")
    void registerAttendance_3() {
        // given
        User user = TestUtil.createUser();
        LocalDate localDate = LocalDate.of(2024, 6, 30);

        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);
        when(attendanceRepository.existsByUserAndAttendanceDateTimeGreaterThanEqual(user, localDate.atStartOfDay()))
                .thenReturn(true);

        Location location = new Location(37.539927, 127.073006);

        // expect
        assertThatThrownBy(() -> attendanceRegisterService.registerAttendance(user, location))
                .isInstanceOf(DuplicateAttendanceException.class);
    }

}