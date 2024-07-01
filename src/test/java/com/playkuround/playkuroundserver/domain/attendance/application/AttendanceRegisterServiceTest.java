package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.exception.DuplicateAttendanceException;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
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
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("출석 시 뱃지와 출석정보가 저장되고 유저의 출석횟수가 증가한다")
    void registerAttendance_1() {
        // given
        when(attendanceRepository.existsByUserAndAttendanceDateTimeAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(LocalDate.of(2024, 6, 30));

        NewlyRegisteredBadge newlyRegisteredBadge = new NewlyRegisteredBadge();
        newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_1);
        newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_ARBOR_DAY);
        when(badgeService.updateNewlyAttendanceBadges(any(User.class)))
                .thenReturn(newlyRegisteredBadge);

        User user = TestUtil.createUser();
        Location location = new Location(37.539927, 127.073006);

        // when
        NewlyRegisteredBadge result = attendanceRegisterService.registerAttendance(user, location);

        // then
        assertThat(user.getAttendanceDays()).isEqualTo(1);
        assertThat(result.getNewlyBadges()).extracting("name")
                .containsExactlyInAnyOrder(BadgeType.ATTENDANCE_1.name(), BadgeType.ATTENDANCE_ARBOR_DAY.name());

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
        when(attendanceRepository.existsByUserAndAttendanceDateTimeAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(true);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(LocalDate.of(2024, 6, 30));
        User user = TestUtil.createUser();
        Location location = new Location(37.539927, 127.073006);

        // expect
        assertThatThrownBy(() -> attendanceRegisterService.registerAttendance(user, location))
                .isInstanceOf(DuplicateAttendanceException.class);
    }

}