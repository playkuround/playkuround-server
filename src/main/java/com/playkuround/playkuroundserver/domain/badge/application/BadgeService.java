package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.badge.application.attendance_badge.AttendanceBadge;
import com.playkuround.playkuroundserver.domain.badge.application.attendance_badge.AttendanceBadgeList;
import com.playkuround.playkuroundserver.domain.badge.application.college.CollegeBadge;
import com.playkuround.playkuroundserver.domain.badge.application.college.CollegeBadgeList;
import com.playkuround.playkuroundserver.domain.badge.application.college_special_badge.CollegeSpecialBadge;
import com.playkuround.playkuroundserver.domain.badge.application.specialday_badge.SpecialDayBadge;
import com.playkuround.playkuroundserver.domain.badge.application.specialday_badge.SpecialDayBadgeList;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final DateTimeService dateTimeService;
    private final CollegeSpecialBadge collegeSpecialBadge;

    @Transactional(readOnly = true)
    public List<Badge> findBadgeByEmail(User user) {
        return badgeRepository.findByUser(user);
    }

    private Set<BadgeType> getUserBadgeSet(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(Badge::getBadgeType)
                .collect(Collectors.toSet());
    }

    public List<BadgeType> updateNewlyAttendanceBadges(User user) {
        List<BadgeType> newlyRegisteredBadge = new ArrayList<>();

        Set<BadgeType> userBadgeSet = getUserBadgeSet(user);

        List<BadgeType> attendanceBadges = saveAttendanceBadges(user, userBadgeSet);
        newlyRegisteredBadge.addAll(attendanceBadges);

        List<BadgeType> specialDayBadges = saveSpecialDayBadges(user, userBadgeSet);
        newlyRegisteredBadge.addAll(specialDayBadges);

        return newlyRegisteredBadge;
    }

    private List<BadgeType> saveAttendanceBadges(User user, Set<BadgeType> userBadgeSet) {
        List<BadgeType> newBadges = new ArrayList<>();

        AttendanceBadgeList.getAttendanceBadges().stream()
                .filter(attendanceBadge -> attendanceBadge.supports(user.getAttendanceDays()))
                .map(AttendanceBadge::getBadgeType)
                .filter(badgeType -> !userBadgeSet.contains(badgeType))
                .forEach(badgeType -> {
                    newBadges.add(badgeType);
                    badgeRepository.save(Badge.createBadge(user, badgeType));
                });

        return newBadges;
    }

    private List<BadgeType> saveSpecialDayBadges(User user, Set<BadgeType> userBadgeSet) {
        List<BadgeType> newBadges = new ArrayList<>();
        LocalDate now = dateTimeService.getLocalDateNow();

        SpecialDayBadgeList.getSpecialDayBadges().stream()
                .filter(specialDayBadge -> specialDayBadge.supports(now))
                .map(SpecialDayBadge::getBadgeType)
                .filter(badgeType -> !userBadgeSet.contains(badgeType))
                .forEach(badgeType -> {
                    newBadges.add(badgeType);
                    badgeRepository.save(Badge.createBadge(user, badgeType));
                });

        return newBadges;
    }

    public List<BadgeType> updateNewlyAdventureBadges(User user, Landmark requestSaveLandmark) {
        List<BadgeType> newlyRegisteredBadge = new ArrayList<>();

        Set<BadgeType> userBadgeSet = getUserBadgeSet(user);

        List<BadgeType> collegeBadges = saveCollegeBadges(user, requestSaveLandmark.getName(), userBadgeSet);
        newlyRegisteredBadge.addAll(collegeBadges);

        List<BadgeType> collegeSpecialBadges = saveCollegeSpecialBadges(user, requestSaveLandmark, userBadgeSet);
        newlyRegisteredBadge.addAll(collegeSpecialBadges);

        return newlyRegisteredBadge;
    }

    private List<BadgeType> saveCollegeBadges(User user, LandmarkType requestLandmarkType, Set<BadgeType> userBadgeSet) {
        List<BadgeType> newBadges = new ArrayList<>();

        CollegeBadgeList.getCollegeBadges().stream()
                .filter(collegeBadge -> collegeBadge.supports(requestLandmarkType))
                .map(CollegeBadge::getBadge)
                .filter(badgeType -> !userBadgeSet.contains(badgeType))
                .forEach(badgeType -> {
                    newBadges.add(badgeType);
                    badgeRepository.save(Badge.createBadge(user, badgeType));
                });

        return newBadges;
    }

    private List<BadgeType> saveCollegeSpecialBadges(User user, Landmark requestSaveLandmark, Set<BadgeType> userBadgeSet) {
        List<BadgeType> newBadges = new ArrayList<>();

        List<BadgeType> candidates = collegeSpecialBadge.getBadgeTypes(user, requestSaveLandmark);
        candidates.stream()
                .filter(badgeType -> !userBadgeSet.contains(badgeType))
                .forEach(badgeType -> {
                    newBadges.add(badgeType);
                    badgeRepository.save(Badge.createBadge(user, badgeType));
                });

        return newBadges;
    }

    public boolean saveTheDreamOfDuckBadge(User user) {
        if (badgeRepository.existsByUserAndBadgeType(user, BadgeType.THE_DREAM_OF_DUCK)) {
            return false;
        }
        Badge badge = Badge.createBadge(user, BadgeType.THE_DREAM_OF_DUCK);
        badgeRepository.save(badge);
        return true;
    }

    public int saveManualBadge(List<String> userEmails, BadgeType badgeType, boolean registerMessage) {
        validateEmailsSize(userEmails);

        List<User> users = findUsersExactlyIn(userEmails);
        int successCount = 0;
        for (User user : users) {
            if (badgeRepository.existsByUserAndBadgeType(user, badgeType)) {
                continue;
            }

            Badge badge = Badge.createBadge(user, badgeType);
            badgeRepository.save(badge);
            successCount++;
            if (registerMessage) {
                user.addNewBadgeNotification(badgeType);
            }
        }

        return successCount;
    }

    private void validateEmailsSize(List<String> userEmails) {
        if (userEmails.size() == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The number of emails is too large.");
        }
    }

    private List<User> findUsersExactlyIn(List<String> userEmails) {
        List<User> users = userRepository.findByEmailIn(userEmails);
        if (users.size() != userEmails.size()) {
            throw new UserNotFoundException();
        }

        return users;
    }

}
