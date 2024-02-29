package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
        assertThat(userProfile.getHighestScore()).isNull();
        assertThat(userProfile.getEmail()).isEqualTo(user.getEmail());
        assertThat(userProfile.getNickname()).isEqualTo(user.getNickname());
        assertThat(userProfile.getMajor()).isEqualTo(user.getMajor().name());
    }

    @Nested
    @DisplayName("닉네임 사용 가능 테스트")
    class isAvailableNickname {

        @Test
        @DisplayName("사용할 수 있는 닉네임이면 true 리턴")
        void success_1() {
            // given
            String nickname = "tester";
            when(userRepository.existsByNickname(nickname)).thenReturn(false);

            // when
            boolean result = userProfileService.isAvailableNickname(nickname);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("중복된 닉네임인 경우 false 리턴")
        void fail_1() {
            // given
            String nickname = "tester";
            when(userRepository.existsByNickname(nickname)).thenReturn(true);

            // when
            boolean result = userProfileService.isAvailableNickname(nickname);

            // then
            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"존나", "개새끼", "씨발", "시발"})
        @DisplayName("욕설 필터링에 걸리는 닉네임이면 false 리턴")
        void fail_2(String badWord) {
            assertThat(userProfileService.isAvailableNickname(badWord)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "nineLengt"})
        @EmptySource
        @DisplayName("닉네임은 2자 이상 8자 이하여야 한다.")
        void fail_3(String wrongNickname) {
            assertThat(userProfileService.isAvailableNickname(wrongNickname)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"!", "♥", " a", "a a", "😄"})
        @DisplayName("닉네임은 숫자, 한글, 영문자만 가능하다")
        void fail_4(String wrongNickname) {
            assertThat(userProfileService.isAvailableNickname(wrongNickname)).isFalse();
        }
    }

}