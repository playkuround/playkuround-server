package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        User user = TestUtil.createUser();
        UserProfileResponse userProfile = userProfileService.getUserProfile(user);

        // then
        assertThat(userProfile.getEmail()).isEqualTo(user.getEmail());
        assertThat(userProfile.getNickname()).isEqualTo(user.getNickname());
        assertThat(userProfile.getMajor()).isEqualTo(user.getMajor().name());
        assertThat(userProfile.getHighestScore()).isEqualTo(null);
    }

    @Test
    @DisplayName("닉네임 사용 가능 테스트 - 중복된 경우 True 리턴")
    void duplicateNickname() {
        // given
        String nickname = "tester";
        when(userRepository.existsByNickname(nickname)).thenReturn(true);

        // when
        boolean result = userProfileService.isAvailableNickname(nickname);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임 사용 가능 테스트 - 중복되지 않는 경우 False 리턴")
    void notDuplicateNickname() {
        // given
        String nickname = "tester";
        when(userRepository.existsByNickname(nickname)).thenReturn(false);

        // when
        boolean result = userProfileService.isAvailableNickname(nickname);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("닉네임 사용 가능 테스트 - 닉네임은 2자 이상 8자 이하여야 한다.")
    void notSatisfiedLengthNickname() {
        assertThat(userProfileService.isAvailableNickname("")).isFalse();
        assertThat(userProfileService.isAvailableNickname("a")).isFalse();
        assertThat(userProfileService.isAvailableNickname("nineLengt")).isFalse();
    }

    @Test
    @DisplayName("닉네임 사용 가능 테스트 - 닉네임은 숫자, 한글, 영문자만 가능하다")
    void notSatisfiedCharTypeNickname() {
        assertThat(userProfileService.isAvailableNickname("!")).isFalse();
        assertThat(userProfileService.isAvailableNickname("♥")).isFalse();
        assertThat(userProfileService.isAvailableNickname("  aa")).isFalse();
        assertThat(userProfileService.isAvailableNickname("😄")).isFalse();
    }

}