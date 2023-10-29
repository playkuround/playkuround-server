package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLogoutServiceTest {

    @InjectMocks
    private UserLogoutService userLogoutService;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() {
        // given
        User user = new User("email@konkuk.ac.kr", "tester", Major.컴퓨터공학부, Role.ROLE_USER);
        doNothing().when(tokenService).deleteRefreshTokenByUser(user);

        // when
        userLogoutService.logout(user);

        // then
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(tokenService, times(1)).deleteRefreshTokenByUser(argument.capture());
        assertThat(argument.getValue()).isEqualTo(user);
    }
}