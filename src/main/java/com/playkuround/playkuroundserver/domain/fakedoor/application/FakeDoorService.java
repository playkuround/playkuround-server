package com.playkuround.playkuroundserver.domain.fakedoor.application;

import com.playkuround.playkuroundserver.domain.fakedoor.dao.FakeDoorRepository;
import com.playkuround.playkuroundserver.domain.fakedoor.domain.FakeDoor;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FakeDoorService {

    private final FakeDoorRepository fakeDoorRepository;

    @Transactional
    public void saveFakeDoor(User user) {
        FakeDoor fakeDoor = new FakeDoor(user);
        fakeDoorRepository.save(fakeDoor);
    }
}
