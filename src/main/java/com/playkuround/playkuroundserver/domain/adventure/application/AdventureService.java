package com.playkuround.playkuroundserver.domain.adventure.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseMostVisitedUser;
import com.playkuround.playkuroundserver.domain.adventure.dto.VisitedUserDto;
import com.playkuround.playkuroundserver.domain.adventure.exception.InvalidLandmarkLocationException;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.exception.BadgeTypeNotFoundException;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final LandmarkRepository landmarkRepository;
    private final BadgeRepository badgeRepository;
    private final UserFindDao userFindDao;


    public AdventureSaveDto.Response saveAdventure(String userEmail, AdventureSaveDto.Request dto) {
        User user = userFindDao.findByEmail(userEmail);
        Landmark landmark = landmarkRepository.findById(dto.getLandmarkId())
                .orElseThrow(() -> new LandmarkNotFoundException(dto.getLandmarkId()));
        validateLocation(landmark, dto.getLatitude(), dto.getLongitude());
        adventureRepository.save(new Adventure(user, landmark));

        return findNewBadges(user, landmark);
    }

    private void validateLocation(Landmark landmark, double latitude, double longitude) {
        double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
        // 10미터 초과이면 에러
        if (distance > 10) throw new InvalidLandmarkLocationException();
    }

    private AdventureSaveDto.Response findNewBadges(User user, Landmark requestSaveLandmark) {
        AdventureSaveDto.Response ret = new AdventureSaveDto.Response();

        if (badgeRepository.existsByUserAndBadgeType(user, BadgeType.CONQUEROR)) {
            return ret;
        }

        // 1. 탐험 횟수에 따른 배지
        Long adventureCount = adventureRepository.countByUser(user);
        try {
            ret.addBadge(BadgeType.findBadgeTypeByAdventureCount(adventureCount));
        } catch (BadgeTypeNotFoundException ignored) {
        }

        // 2. 탐험 장소에 따른 배지
        Long saveLandmarkId = requestSaveLandmark.getId();
        try {
            BadgeType badgeType = BadgeType.findBadgeTypeByLandmarkId(saveLandmarkId);
            Long adventureCountForBadge = -1L;
            if (badgeType == BadgeType.ENGINEER)
                adventureCountForBadge = adventureRepository.countAdventureForENGINEER();
            if (badgeType == BadgeType.ARTIST)
                adventureCountForBadge = adventureRepository.countAdventureForARTIST();
            if (badgeType == BadgeType.CEO)
                adventureCountForBadge = adventureRepository.countAdventureForCEO();
            if (badgeType == BadgeType.NATIONAL_PLAYER)
                adventureCountForBadge = adventureRepository.countAdventureForNATIONAL_PLAYER();
            if (badgeType == BadgeType.NEIL_ARMSTRONG)
                adventureCountForBadge = adventureRepository.countAdventureForNEIL_ARMSTRONG();

            if (Objects.equals(adventureCountForBadge, BadgeType.requiredAdventureCountForBadge(badgeType))) {
                ret.addBadge(badgeType);
            }
        } catch (BadgeTypeNotFoundException ignored) {
        }

        return ret;
    }

    @Transactional(readOnly = true)
    public ResponseFindAdventure findAdventureByUserEmail(String userEmail) {
        User user = userFindDao.findByEmail(userEmail);
        return ResponseFindAdventure.of(adventureRepository.findDistinctLandmarkIdByUser(user));
    }

    @Transactional(readOnly = true)
    public ResponseMostVisitedUser findMemberMostLandmark(String userEmail, Long landmarkId) {
        /*
         * 해당 랜드마크에 가장 많이 방문한 회원
         * 횟수가 같다면 방문한지 오래된 회원 -> 정책 논의 필요
         */
        User user = userFindDao.findByEmail(userEmail);

        List<VisitedUserDto> visitedInfoList = adventureRepository.findVisitedUsersRank(landmarkId);
        return ResponseMostVisitedUser.of(visitedInfoList, user.getId());
    }

}
