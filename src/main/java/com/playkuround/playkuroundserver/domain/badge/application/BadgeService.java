package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.badge.dto.request.BadgeFindRequest;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public List<BadgeFindRequest> findBadgeByEmail(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(BadgeFindRequest::from)
                .toList();
    }

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

    private Set<BadgeType> getUserBadgeSet(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(Badge::getBadgeType)
                .collect(Collectors.toSet());
    }

    private boolean isEligibleForAttendanceBadge(User user, int requiredAttendanceDays) {
        return user.getAttendanceDays() == requiredAttendanceDays;
    }


}
