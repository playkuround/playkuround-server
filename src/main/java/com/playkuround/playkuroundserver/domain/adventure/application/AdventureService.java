package com.playkuround.playkuroundserver.domain.adventure.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.request.AdventureSaveRequest;
import com.playkuround.playkuroundserver.domain.adventure.dto.response.AdventureSaveResponse;
import com.playkuround.playkuroundserver.domain.adventure.exception.InvalidLandmarkLocationException;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.exception.BadgeTypeNotFoundException;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.score.application.ScoreService;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdventureService {

    private final ScoreService scoreService;
    private final BadgeRepository badgeRepository;
    private final LandmarkRepository landmarkRepository;
    private final AdventureRepository adventureRepository;

    public AdventureSaveResponse saveAdventure(User user, AdventureSaveRequest request) {
        Landmark landmark = landmarkRepository.findById(request.getLandmarkId())
                .orElseThrow(() -> new LandmarkNotFoundException(request.getLandmarkId()));
        validateLocation(landmark, request.getLatitude(), request.getLongitude());
        ScoreType scoreType = ScoreType.fromString(request.getScoreType());

        // 1. Total Score 저장
        scoreService.saveScore(user, scoreType, request.getScore());

        // 2. Adventure 저장
        Adventure adventure = new Adventure(user, landmark, scoreType, request.getScore());
        adventureRepository.save(adventure);

        // 3. 뱃지 저장
        AdventureSaveResponse response = updateNewBadges(user, landmark);// TODO. 뱃지 클래스로 분리하기
        response.setCorrectionScore(request.getScore());

        // 4. 랜드마크 최고 점수 갱신
        updateLandmarkHighestScore(user, landmark);
        return response;
    }

    private void validateLocation(Landmark landmark, double latitude, double longitude) {
        double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
        if (distance > landmark.getRecognitionRadius()) {
            throw new InvalidLandmarkLocationException();
        }
    }

    private AdventureSaveResponse updateNewBadges(User user, Landmark requestSaveLandmark) {
        AdventureSaveResponse response = new AdventureSaveResponse();

        // 이미 모든 랜드마크 종류를 다 탐험했다면, 탐험 관련 뱃지는 이미 가지고 있음
        if (badgeRepository.existsByUserAndBadgeType(user, BadgeType.CONQUEROR)) {
            return response;
        }

        // 1. (탐험한 랜드마크의 종류 개수)에 따른 뱃지
        Long numberOfLandmarkType = adventureRepository.countDistinctLandmarkByUser(user);
        try {
            BadgeType badgeType = BadgeType.findBadgeTypeByLandmarkTypeCount(numberOfLandmarkType);
            badgeRepository.save(Badge.createBadge(user, badgeType));
            response.addBadge(badgeType);
        } catch (BadgeTypeNotFoundException ignored) {
        }

        // 2. 탐험 장소에 따른 배지
        Long saveLandmarkId = requestSaveLandmark.getId();
        try {
            BadgeType badgeType = BadgeType.findBadgeTypeByLandmarkId(saveLandmarkId);
            Long adventureCountForBadge = -1L;
            if (badgeType == BadgeType.ENGINEER) {
                adventureCountForBadge = adventureRepository.countAdventureForENGINEER();
            }
            else if (badgeType == BadgeType.ARTIST) {
                adventureCountForBadge = adventureRepository.countAdventureForARTIST();
            }
            else if (badgeType == BadgeType.CEO) {
                adventureCountForBadge = adventureRepository.countAdventureForCEO();
            }
            else if (badgeType == BadgeType.NATIONAL_PLAYER) {
                adventureCountForBadge = adventureRepository.countAdventureForNATIONAL_PLAYER();
            }
            else if (badgeType == BadgeType.NEIL_ARMSTRONG) {
                adventureCountForBadge = adventureRepository.countAdventureForNEIL_ARMSTRONG();
            }

            if (adventureCountForBadge.equals(BadgeType.requiredAdventureCountForBadge(badgeType))) {
                badgeRepository.save(Badge.createBadge(user, badgeType));
                response.addBadge(badgeType);
            }
        } catch (BadgeTypeNotFoundException ignored) {
        }
        return response;
    }

    private void updateLandmarkHighestScore(User user, Landmark landmark) {
        Long sumCorrectionScore = adventureRepository.sumCorrectionScore(user, landmark);
        landmark.updateFirstUser(user, sumCorrectionScore);
    }

    @Transactional(readOnly = true)
    public ResponseFindAdventure findAdventureByUserEmail(User user) {
        return ResponseFindAdventure.of(adventureRepository.findDistinctLandmarkIdByUser(user));
    }

}
