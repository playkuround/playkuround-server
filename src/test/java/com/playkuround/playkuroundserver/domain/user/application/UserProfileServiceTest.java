package com.playkuround.playkuroundserver.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserProfileResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("프로필 얻기 성공")
    void getProfile() {
        // when
        String email = "tester@konkuk.ac.kr";
        String nickname = "tester";
        Major major = Major.컴퓨터공학부;
        User user = new User(email, nickname, major, Role.ROLE_USER);
        UserProfileResponse userProfile = userProfileService.getUserProfile(user);

        // then
        assertThat(userProfile.getEmail()).isEqualTo(email);
        assertThat(userProfile.getNickname()).isEqualTo(nickname);
        assertThat(userProfile.getMajor()).isEqualTo(major.name());
        assertThat(userProfile.getConsecutiveAttendanceDays()).isEqualTo(0);
        assertThat(userProfile.getLastAttendanceDate()).isBefore(LocalDateTime.now());
        assertThat(userProfile.getHighestScore()).isEqualTo(0);
    }

    @Test
    @DisplayName("닉네임 중복 테스트 - 중복된 경우 True 리턴")
    void duplicateNickname() {
        // given
        String nickname = "tester";
        when(userRepository.existsByNickname(nickname)).thenReturn(true);

        // when
        boolean result = userProfileService.checkDuplicateNickname(nickname);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임 중복 테스트 - 중복되지 않는 경우 False 리턴")
    void notDuplicateNickname() {
        // given
        String nickname = "tester";
        when(userRepository.existsByNickname(nickname)).thenReturn(false);

        // when
        boolean result = userProfileService.checkDuplicateNickname(nickname);

        // then
        assertThat(result).isFalse();
    }

}