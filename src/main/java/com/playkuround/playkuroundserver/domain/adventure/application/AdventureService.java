package com.playkuround.playkuroundserver.domain.adventure.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.request.AdventureSaveRequest;
import com.playkuround.playkuroundserver.domain.adventure.dto.response.AdventureSaveResponse;
import com.playkuround.playkuroundserver.domain.adventure.exception.InvalidLandmarkLocationException;
import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.score.application.TotalScoreService;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdventureService {

    private final BadgeService badgeService;
    private final UserRepository userRepository;
    private final TotalScoreService totalScoreService;
    private final LandmarkRepository landmarkRepository;
    private final AdventureRepository adventureRepository;

    @Transactional
    public AdventureSaveResponse saveAdventure(User user, AdventureSaveRequest request) {
        Landmark landmark = landmarkRepository.findById(request.getLandmarkId())
                .orElseThrow(() -> new LandmarkNotFoundException(request.getLandmarkId()));
        Location location = new Location(request.getLatitude(), request.getLongitude());
        validateLocation(landmark, location);

        ScoreType scoreType = ScoreType.fromString(request.getScoreType());

        // 1. Total Score 저장 및 최고 점수 갱신
        Long myTotalScore = totalScoreService.saveScore(user, request.getScore());
        user.getHighestScore().updateHighestTotalScore(myTotalScore);
        user.getHighestScore().updateGameHighestScore(scoreType, request.getScore());
        userRepository.save(user);

        // 2. Adventure 저장
        Adventure adventure = new Adventure(user, landmark, scoreType, request.getScore());
        adventureRepository.save(adventure);

        // 3. 랜드마크 최고 점수 갱신
        updateLandmarkHighestScore(user, landmark);

        // 4. 뱃지 저장
        NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAdventureBadges(user, landmark);

        return AdventureSaveResponse.from(newlyRegisteredBadge);
    }

    private void validateLocation(Landmark landmark, Location location) {
        Location locationOfLandmark = new Location(landmark.getLatitude(), landmark.getLongitude());
        double distance = LocationDistanceUtils.distance(locationOfLandmark, location);
        if (distance > landmark.getRecognitionRadius()) {
            throw new InvalidLandmarkLocationException();
        }
    }

    private void updateLandmarkHighestScore(User user, Landmark landmark) {
        long sumScore = adventureRepository.sumScoreByUserAndLandmark(user, landmark);
        landmark.updateFirstUser(user, sumScore);
    }
}
