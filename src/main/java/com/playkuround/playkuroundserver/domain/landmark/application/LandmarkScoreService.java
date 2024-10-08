package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.LandmarkHighestScoreUser;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LandmarkScoreService {

    private final LandmarkRepository landmarkRepository;

    @Transactional(readOnly = true)
    public Optional<LandmarkHighestScoreUser> findHighestScoreUserByLandmark(Long landmarkId) {
        Landmark landmark = landmarkRepository.findById(landmarkId)
                .orElseThrow(() -> new LandmarkNotFoundException(landmarkId));

        User firstUser = landmark.getFirstUser();
        if (firstUser == null) {
            return Optional.empty();
        }

        LandmarkHighestScoreUser highestScoreUser = new LandmarkHighestScoreUser(
                landmark.getHighestScore(),
                firstUser.getNickname(),
                firstUser.getProfileBadge()
        );
        return Optional.of(highestScoreUser);
    }
}
