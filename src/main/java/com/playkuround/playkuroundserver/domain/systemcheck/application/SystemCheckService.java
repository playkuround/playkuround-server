package com.playkuround.playkuroundserver.domain.systemcheck.application;

import com.playkuround.playkuroundserver.domain.systemcheck.dao.SystemCheckRepository;
import com.playkuround.playkuroundserver.domain.systemcheck.domain.SystemCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemCheckService {

    private final SystemCheckRepository systemCheckRepository;

    public boolean isSystemAvailable() {
        Optional<SystemCheck> optionalSystemCheck = systemCheckRepository.findTopByOrderByUpdatedAtDesc();
        return optionalSystemCheck.map(SystemCheck::isAvailable)
                .orElse(false);
    }

    @Transactional
    public void changeSystemAvailable(boolean available) {
        systemCheckRepository.deleteAll();
        SystemCheck systemCheck = new SystemCheck(available);
        systemCheckRepository.save(systemCheck);
    }
}
