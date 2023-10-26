package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserProfileDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
        User user = new User(email, nickname, major);
        UserProfileDto.Response userProfile = userProfileService.getUserProfile(user);

        // then
        assertThat(userProfile.getEmail()).isEqualTo(email);
        assertThat(userProfile.getNickname()).isEqualTo(nickname);
        assertThat(userProfile.getMajor()).isEqualTo(major.name());
        assertThat(userProfile.getConsecutiveAttendanceDays()).isEqualTo(0);
        assertThat(userProfile.getLastAttendanceDate()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("닉네임 중복 테스트 - 중복된 경우 True 리턴")
    void duplicateNickname() {
        String nickname = "tester";
        when(userRepository.existsByNickname(nickname)).thenReturn(true);
        boolean result = userProfileService.checkDuplicateNickname(nickname);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임 중복 테스트 - 중복된 경우 False 리턴")
    void notDuplicateNickname() {
        String nickname = "tester";
        when(userRepository.existsByNickname(nickname)).thenReturn(false);
        boolean result = userProfileService.checkDuplicateNickname(nickname);
        assertThat(result).isFalse();
    }

}