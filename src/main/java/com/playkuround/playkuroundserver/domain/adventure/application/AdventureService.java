package com.playkuround.playkuroundserver.domain.adventure.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.adventure.exception.DuplicateAdventureException;
import com.playkuround.playkuroundserver.domain.adventure.exception.InvalidLandmarkLocationException;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.exception.BadgeTypeNotFoundException;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final LandmarkRepository landmarkRepository;
    private final BadgeRepository badgeRepository;

    public void saveAdventure(User user, AdventureSaveDto.Request request) {
        Landmark landmark = landmarkRepository.findById(request.getLandmarkId())
                .orElseThrow(() -> new LandmarkNotFoundException(request.getLandmarkId()));
        validateLocation(landmark, request.getLatitude(), request.getLongitude());

        if (adventureRepository.existsByUserAndLandmarkAndCreatedAtAfter(user, landmark, LocalDate.now().atStartOfDay())) {
            throw new DuplicateAdventureException();
        }

        adventureRepository.save(new Adventure(user, landmark));
        updateNewBadges(user, landmark);
    }

    private void validateLocation(Landmark landmark, double latitude, double longitude) {
        double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
        // 지정된 반경보다 멀리 있으면 오류
        if (distance > landmark.getRecognitionRadius()) throw new InvalidLandmarkLocationException();
    }

    private void updateNewBadges(User user, Landmark requestSaveLandmark) {
        // 이미 모든 랜드마크 종류를 다 탐험했다면, 탐험 관련 뱃지는 이미 가지고 있음
        if (badgeRepository.existsByUserAndBadgeType(user, BadgeType.CONQUEROR)) return;

        // 1. (탐험한 랜드마크의 종류 개수)에 따른 뱃지
        Long numberOfLandmarkType = adventureRepository.countDistinctLandmarkByUser(user);
        try {
            BadgeType badgeType = BadgeType.findBadgeTypeByLandmarkTypeCount(numberOfLandmarkType);
            badgeRepository.save(Badge.createBadge(user, badgeType));
        } catch (BadgeTypeNotFoundException ignored) {
        }

        // 2. 탐험 장소에 따른 배지
        Long saveLandmarkId = requestSaveLandmark.getId();
        try {
            BadgeType badgeType = BadgeType.findBadgeTypeByLandmarkId(saveLandmarkId);
            Long adventureCountForBadge = -1L;
            if (badgeType == BadgeType.ENGINEER)
                adventureCountForBadge = adventureRepository.countAdventureForENGINEER();
            else if (badgeType == BadgeType.ARTIST)
                adventureCountForBadge = adventureRepository.countAdventureForARTIST();
            else if (badgeType == BadgeType.CEO)
                adventureCountForBadge = adventureRepository.countAdventureForCEO();
            else if (badgeType == BadgeType.NATIONAL_PLAYER)
                adventureCountForBadge = adventureRepository.countAdventureForNATIONAL_PLAYER();
            else if (badgeType == BadgeType.NEIL_ARMSTRONG)
                adventureCountForBadge = adventureRepository.countAdventureForNEIL_ARMSTRONG();

            if (Objects.equals(adventureCountForBadge, BadgeType.requiredAdventureCountForBadge(badgeType))) {
                badgeRepository.save(Badge.createBadge(user, badgeType));
            }
        } catch (BadgeTypeNotFoundException ignored) {
        }
    }

    @Transactional(readOnly = true)
    public ResponseFindAdventure findAdventureByUserEmail(User user) {
        return ResponseFindAdventure.of(adventureRepository.findDistinctLandmarkIdByUser(user));
    }

}
