package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.dto.response.AttendanceRegisterResponse;
import com.playkuround.playkuroundserver.domain.attendance.exception.DuplicateAttendanceException;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void 출석_시_뱃지와_출석정보가_저장되고_유저의_출석횟수가_증가한다() {
        // given
        NewlyRegisteredBadge newlyRegisteredBadge = new NewlyRegisteredBadge();
        newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_1);
        newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_ARBOR_DAY);

        when(attendanceRepository.existsByUserAndCreatedAtAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(null);
        when(badgeService.updateNewlyAttendanceBadges(any(User.class)))
                .thenReturn(newlyRegisteredBadge);

        // when
        User user = TestUtil.createUser();
        Location location = new Location(37.539927, 127.073006);
        AttendanceRegisterResponse result = attendanceRegisterService.registerAttendance(user, location);

        // then
        assertThat(user.getAttendanceDays()).isEqualTo(1);

        assertThat(result.getNewBadges()).hasSize(2);
        assertThat(result.getNewBadges()).extracting("name")
                .containsExactlyInAnyOrder(BadgeType.ATTENDANCE_1.name(), BadgeType.ATTENDANCE_ARBOR_DAY.name());

        ArgumentCaptor<Attendance> attendanceArgument = ArgumentCaptor.forClass(Attendance.class);
        verify(attendanceRepository, times(1)).save(attendanceArgument.capture());
        assertThat(attendanceArgument.getValue().getUser()).isEqualTo(user);
        assertThat(attendanceArgument.getValue().getLatitude()).isEqualTo(location.latitude());
        assertThat(attendanceArgument.getValue().getLongitude()).isEqualTo(location.longitude());
    }

    @Test
    void 출석_범위에_벗어나면_에러가_발생한다() {
        // expect
        User user = TestUtil.createUser();
        Location location = new Location(0.0, 0.0);
        assertThrows(InvalidAttendanceLocationException.class,
                () -> attendanceRegisterService.registerAttendance(user, location));
    }

    @Test
    void 출석은_하루에_한번만_가능하다() {
        // given
        when(attendanceRepository.existsByUserAndCreatedAtAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(true);

        // expect
        User user = TestUtil.createUser();
        Location location = new Location(37.539927, 127.073006);
        assertThrows(DuplicateAttendanceException.class,
                () -> attendanceRegisterService.registerAttendance(user, location));
    }

}