package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.badge.application.attendance_badge.AttendanceBadgeList;
import com.playkuround.playkuroundserver.domain.badge.application.college.CollegeBadgeList;
import com.playkuround.playkuroundserver.domain.badge.application.college_special_badge.CollegeSpecialBadgeFactory;
import com.playkuround.playkuroundserver.domain.badge.application.specialday_badge.SpecialDayBadgeList;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.badge.exception.BadgeNotHaveException;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final DateTimeService dateTimeService;
    private final CollegeSpecialBadgeFactory collegeSpecialBadgeFactory;

    @Transactional(readOnly = true)
    public List<Badge> findBadgeByEmail(User user) {
        return badgeRepository.findByUser(user);
    }

    @Transactional
    public NewlyRegisteredBadge updateNewlyAttendanceBadges(User user) {
        Set<BadgeType> userBadgeSet = getUserBadgeSet(user);

        NewlyRegisteredBadge newlyRegisteredBadge = new NewlyRegisteredBadge();

        // 출석일 기준 뱃지
        AttendanceBadgeList.getAttendanceBadges().stream()
                .filter(attendanceBadge -> attendanceBadge.supports(userBadgeSet, user))
                .forEach(attendanceBadge -> {
                    BadgeType badgeType = attendanceBadge.getBadgeType();
                    badgeRepository.save(Badge.createBadge(user, badgeType));
                    newlyRegisteredBadge.addBadge(badgeType);
                });

        // 기념일 기준 뱃지
        SpecialDayBadgeList.getSpecialDayBadges().stream()
                .filter(specialDayBadge -> specialDayBadge.supports(userBadgeSet, dateTimeService.getLocalDateNow()))
                .forEach(specialDayBadge -> {
                    BadgeType badgeType = specialDayBadge.getBadgeType();
                    badgeRepository.save(Badge.createBadge(user, badgeType));
                    newlyRegisteredBadge.addBadge(badgeType);
                });

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

        // 단과대 특별 뱃지
        collegeSpecialBadgeFactory.getBadgeType(user, userBadgeSet, requestSaveLandmark)
                .ifPresent(badgeType -> {
                    newlyRegisteredBadge.addBadge(badgeType);
                    badgeRepository.save(Badge.createBadge(user, badgeType));
                });

        return newlyRegisteredBadge;
    }

    private Set<BadgeType> getUserBadgeSet(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(Badge::getBadgeType)
                .collect(Collectors.toSet());
    }

    @Transactional
    public boolean saveTheDreamOfDuckBadge(User user) {
        if (badgeRepository.existsByUserAndBadgeType(user, BadgeType.THE_DREAM_OF_DUCK)) {
            return false;
        }
        Badge badge = Badge.createBadge(user, BadgeType.THE_DREAM_OF_DUCK);
        badgeRepository.save(badge);
        return true;
    }

    @Transactional
    public boolean saveManualBadge(String userEmail, BadgeType badgeType, boolean registerMessage) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);
        if (badgeRepository.existsByUserAndBadgeType(user, badgeType)) {
            return false;
        }

        Badge badge = Badge.createBadge(user, badgeType);
        badgeRepository.save(badge);
        if (registerMessage) {
            user.addNewBadgeNotification(badgeType);
        }
        return true;
    }

    @Transactional
    public void representationBadge(User user, BadgeType badgeType) {
        if (!badgeRepository.existsByUserAndBadgeType(user, badgeType)) {
            throw new BadgeNotHaveException();
        }

        user.updateRepresentBadge(badgeType);
        userRepository.save(user);
    }

}
