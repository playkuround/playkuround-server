package com.playkuround.playkuroundserver.learning.securityPersistence;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
@Import({PersistenceController.class, PersistenceService.class})
@DisplayName("Spring security의 @AuthenticationPrincipal 테스트")
public class SecurityPersistenceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenManager tokenManager;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Spring Data JPA의 save 함수를 호출해 update 쿼리를 실행한다.")
    void springSecurityContextPersistent1() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);

        TokenDto tokenDto = tokenManager.createTokenDto(user.getEmail());
        String accessToken = tokenDto.getAccessToken();

        // expected
        mockMvc.perform(get("/api/persistence-test/success")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());

        user = userRepository.findAll().get(0);
        assertThat(user.getAttendanceDays()).isEqualTo(1);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("spring security에서 조회한 user 객체는 영속 상태가 아니기 때문에 dirty check가 되지 않는다.")
    void springSecurityContextPersistent2() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);

        TokenDto tokenDto = tokenManager.createTokenDto(user.getEmail());
        String accessToken = tokenDto.getAccessToken();

        // expected
        mockMvc.perform(get("/api/persistence-test/fail")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());

        user = userRepository.findAll().get(0);
        assertThat(user.getAttendanceDays()).isEqualTo(0);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("Transaction 내에서 save() 호출 이후 에러가 발생하면 rollback을 수행한다.")
    void rollback() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);

        TokenDto tokenDto = tokenManager.createTokenDto(user.getEmail());
        String accessToken = tokenDto.getAccessToken();

        // expected
        mockMvc.perform(get("/api/persistence-test/rollback")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isInternalServerError());

        user = userRepository.findAll().get(0);
        assertThat(user.getAttendanceDays()).isEqualTo(0);
    }
}
