package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.badge.application.art_design.ArtAndDesignBadgeFactory;
import com.playkuround.playkuroundserver.domain.badge.application.business_administration.BusinessAdministrationBadgeList;
import com.playkuround.playkuroundserver.domain.badge.application.college.CollegeBadgeList;
import com.playkuround.playkuroundserver.domain.badge.application.engineering.EngineeringBadgeMap;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.badge.dto.response.BadgeFindResponse;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final AdventureRepository adventureRepository;

    @Transactional(readOnly = true)
    public List<BadgeFindResponse> findBadgeByEmail(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(BadgeFindResponse::from)
                .toList();
    }

    @Transactional
    public NewlyRegisteredBadge updateNewlyAttendanceBadges(User user) {
        Set<BadgeType> userBadgeSet = getUserBadgeSet(user);

        NewlyRegisteredBadge newlyRegisteredBadge = new NewlyRegisteredBadge();

        if (!userBadgeSet.contains(BadgeType.ATTENDANCE_1)) {
            badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_1));
            newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_1);
        }
        else if (!userBadgeSet.contains(BadgeType.ATTENDANCE_5)) {
            if (isEligibleForAttendanceBadge(user, 5)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_5));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_5);
            }
        }
        else if (!userBadgeSet.contains(BadgeType.ATTENDANCE_10)) {
            if (isEligibleForAttendanceBadge(user, 10)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_10));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_10);
            }
        }
        else if (!userBadgeSet.contains(BadgeType.ATTENDANCE_30)) {
            if (isEligibleForAttendanceBadge(user, 30)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_30));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_30);
            }
        }
        else if (!userBadgeSet.contains(BadgeType.ATTENDANCE_100)) {
            if (isEligibleForAttendanceBadge(user, 100)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_100));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_100);
            }
        }

        if (DateUtils.isTodayFoundationDay()) {
            if (!userBadgeSet.contains(BadgeType.ATTENDANCE_FOUNDATION_DAY)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_FOUNDATION_DAY));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_FOUNDATION_DAY);
            }
        }
        else if (DateUtils.isTodayArborDay()) {
            if (!userBadgeSet.contains(BadgeType.ATTENDANCE_ARBOR_DAY)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_ARBOR_DAY));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_ARBOR_DAY);
            }
        }
        else if (DateUtils.isTodayChildrenDay()) {
            if (!userBadgeSet.contains(BadgeType.ATTENDANCE_CHILDREN_DAY)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_CHILDREN_DAY));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_CHILDREN_DAY);
            }
        }
        else if (DateUtils.isTodayWhiteDay()) {
            if (!userBadgeSet.contains(BadgeType.ATTENDANCE_WHITE_DAY)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_WHITE_DAY));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_WHITE_DAY);
            }
        }
        else if (DateUtils.isTodayDuckDay()) {
            if (!userBadgeSet.contains(BadgeType.ATTENDANCE_DUCK_DAY)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_DUCK_DAY));
                newlyRegisteredBadge.addBadge(BadgeType.ATTENDANCE_DUCK_DAY);
            }
        }

        return newlyRegisteredBadge;
    }

    @Transactional
    public NewlyRegisteredBadge updateNewlyAdventureBadges(User user, Landmark requestSaveLandmark) {
        Set<BadgeType> userBadgeSet = getUserBadgeSet(user);
        LandmarkType requestLandmarkType = requestSaveLandmark.getName();

        NewlyRegisteredBadge newlyRegisteredBadge = new NewlyRegisteredBadge();

        // 대학별
        CollegeBadgeList.getCollegeBadges().stream()
                .filter(collegeBadge -> collegeBadge.supports(requestLandmarkType))
                .forEach(collegeBadge -> {
                    BadgeType badge = collegeBadge.getBadge();
                    if (!userBadgeSet.contains(badge)) {
                        newlyRegisteredBadge.addBadge(badge);
                        badgeRepository.save(Badge.createBadge(user, badge));
                    }
                });

        // 경영대 특별
        if (requestLandmarkType == LandmarkType.경영관) {
            BusinessAdministrationBadgeList.getBadges().stream()
                    .filter(businessAdministrationBadge -> !userBadgeSet.contains(businessAdministrationBadge.badgeType()))
                    .findFirst()
                    .ifPresent(businessAdministrationBadge -> {
                        long count = adventureRepository.countByUserAndLandmark(user, requestSaveLandmark);
                        if (count == businessAdministrationBadge.requiredCount()) {
                            newlyRegisteredBadge.addBadge(businessAdministrationBadge.badgeType());
                            badgeRepository.save(Badge.createBadge(user, businessAdministrationBadge.badgeType()));
                        }
                    });
        }

        // 예디대 특별
        if (requestLandmarkType == LandmarkType.예디대 || requestLandmarkType == LandmarkType.공예관) {
            ArtAndDesignBadgeFactory artAndDesignBadgeFactory = new ArtAndDesignBadgeFactory();
            BadgeType badgeType = artAndDesignBadgeFactory.getBadgeType(userBadgeSet);
            if (badgeType != null) {
                newlyRegisteredBadge.addBadge(badgeType);
                badgeRepository.save(Badge.createBadge(user, badgeType));
            }
        }

        // 공대 특별
        EngineeringBadgeMap.BadgeAndRequiredCount badgeByLandmarkType = EngineeringBadgeMap.findBadgeByLandmarkType(requestLandmarkType);
        if (badgeByLandmarkType != null && !userBadgeSet.contains(badgeByLandmarkType.badgeType())) {
            long count = adventureRepository.countByUserAndLandmark(user, requestSaveLandmark);
            if (count == badgeByLandmarkType.requiredCount()) {
                newlyRegisteredBadge.addBadge(badgeByLandmarkType.badgeType());
                badgeRepository.save(Badge.createBadge(user, badgeByLandmarkType.badgeType()));
            }
        }

        return newlyRegisteredBadge;
    }

    private Set<BadgeType> getUserBadgeSet(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(Badge::getBadgeType)
                .collect(Collectors.toSet());
    }

    private boolean isEligibleForAttendanceBadge(User user, int requiredAttendanceDays) {
        return user.getAttendanceDays() == requiredAttendanceDays;
    }


}
