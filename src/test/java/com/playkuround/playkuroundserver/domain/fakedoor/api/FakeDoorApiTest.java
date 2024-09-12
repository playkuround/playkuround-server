package com.playkuround.playkuroundserver.domain.fakedoor.api;

import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.domain.fakedoor.dao.FakeDoorRepository;
import com.playkuround.playkuroundserver.domain.fakedoor.domain.FakeDoor;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
class FakeDoorApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FakeDoorRepository fakeDoorRepository;

    @AfterEach
    void clean() {
        fakeDoorRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("fakeDoor 저장 성공")
    void success() throws Exception {
        // when
        mockMvc.perform(post("/api/fake-door"))
                .andExpect(status().isCreated());

        // then
        List<FakeDoor> fakeDoorList = fakeDoorRepository.findAll();
        assertThat(fakeDoorList).hasSize(1);
    }
}