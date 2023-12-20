package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.dto.request.AttendanceRegisterRequest;
import com.playkuround.playkuroundserver.domain.attendance.dto.response.AttendanceRegisterResponse;
import com.playkuround.playkuroundserver.domain.attendance.exception.DuplicateAttendanceException;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceRegisterServiceTest {

    @InjectMocks
    private AttendanceRegisterService attendanceRegisterService;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Test
    void 첫_출석_시_뱃지와_출석정보가_저장된다() {
        // given
        User user = TestUtil.createUser();
        when(attendanceRepository.existsByUserAndCreatedAtAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(attendanceRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(badgeRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(badgeRepository.findByUser(any())).thenReturn(new ArrayList<>());

        // when
        AttendanceRegisterRequest request = new AttendanceRegisterRequest(37.539927, 127.073006);
        AttendanceRegisterResponse result = attendanceRegisterService.registerAttendance(user, request);

        // then
        assertThat(result.getNewBadges()).hasSize(1);
        assertThat(result.getNewBadges().get(0).getName()).isEqualTo(BadgeType.ATTENDANCE_1.name());

        ArgumentCaptor<Attendance> attendanceArgument = ArgumentCaptor.forClass(Attendance.class);
        verify(attendanceRepository, times(1)).save(attendanceArgument.capture());
        assertThat(attendanceArgument.getValue().getUser()).isEqualTo(user);

        ArgumentCaptor<Badge> badgeArgument = ArgumentCaptor.forClass(Badge.class);
        verify(badgeRepository, times(1)).save(badgeArgument.capture());
        assertThat(badgeArgument.getValue().getBadgeType()).isEqualTo(BadgeType.ATTENDANCE_1);
    }

    @Test
    void 출석_범위에_벗어나면_에러가_발생한다() {
        // expect
        User user = TestUtil.createUser();
        AttendanceRegisterRequest request = new AttendanceRegisterRequest(0.0, 0.0);
        assertThrows(InvalidAttendanceLocationException.class,
                () -> attendanceRegisterService.registerAttendance(user, request));
    }

    @Test
    void 출석은_하루에_한번만_가능하다() {
        // given
        when(attendanceRepository.existsByUserAndCreatedAtAfter(any(User.class), any(LocalDateTime.class)))
                .thenReturn(true);

        // expect
        User user = TestUtil.createUser();
        AttendanceRegisterRequest request = new AttendanceRegisterRequest(37.539927, 127.073006);
        assertThrows(DuplicateAttendanceException.class,
                () -> attendanceRegisterService.registerAttendance(user, request));
    }

}