package com.playkuround.playkuroundserver.domain.fakedoor.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.fakedoor.dao.FakeDoorRepository;
import com.playkuround.playkuroundserver.domain.fakedoor.domain.FakeDoor;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FakeDoorServiceTest {

    @InjectMocks
    private FakeDoorService fakeDoorService;

    @Mock
    private FakeDoorRepository fakeDoorRepository;

    @Test
    @DisplayName("fakeDoor 저장 성공")
    void saveFakeDoor() {
        // when
        User user = TestUtil.createUser();
        fakeDoorService.saveFakeDoor(user);

        // then
        ArgumentCaptor<FakeDoor> fakeDoorArgumentCaptor = ArgumentCaptor.forClass(FakeDoor.class);
        verify(fakeDoorRepository, times(1)).save(fakeDoorArgumentCaptor.capture());
        FakeDoor fakeDoor = fakeDoorArgumentCaptor.getValue();
        assertThat(fakeDoor.getUser()).isEqualTo(user);
    }

}