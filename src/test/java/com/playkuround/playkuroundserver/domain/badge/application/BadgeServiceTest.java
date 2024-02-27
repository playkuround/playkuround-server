package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.badge.application.college_special_badge.CollegeSpecialBadgeFactory;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.badge.dto.response.BadgeFindResponse;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @InjectMocks
    private BadgeService badgeService;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollegeSpecialBadgeFactory collegeSpecialBadgeFactory;

    @Test
    @DisplayName("뱃지 개수가 0개이면 빈리스트가 반환된다")
    void findBadgeByEmail_1() {
        // given
        User user = TestUtil.createUser();
        when(badgeRepository.findByUser(user))
                .thenReturn(new ArrayList<>());

        // when
        List<BadgeFindResponse> result = badgeService.findBadgeByEmail(user);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("뱃지 개수가 3개 조회")
    void findBadgeByEmail_2() {
        // given
        User user = TestUtil.createUser();
        List<Badge> badges = List.of(
                new Badge(user, BadgeType.COLLEGE_OF_ENGINEERING_A),
                new Badge(user, BadgeType.MONTHLY_RANKING_3),
                new Badge(user, BadgeType.ATTENDANCE_1)
        );
        when(badgeRepository.findByUser(user))
                .thenReturn(badges);

        // when
        List<BadgeFindResponse> result = badgeService.findBadgeByEmail(user);

        // then
        assertThat(result).hasSize(3);

        List<String> target = result.stream()
                .map(BadgeFindResponse::getName)
                .toList();
        assertThat(target).containsOnly(BadgeType.COLLEGE_OF_ENGINEERING_A.name(),
                BadgeType.MONTHLY_RANKING_3.name(),
                BadgeType.ATTENDANCE_1.name());
    }

    @Test
    @DisplayName("출석 횟수에 따른 뱃지 부여 : 1회 출석")
    void updateNewlyAttendanceBadges_1() {
        // given
        User user = TestUtil.createUser();
        user.increaseAttendanceDay();
        when(badgeRepository.findByUser(user)).thenReturn(new ArrayList<>());

        List<NewlyRegisteredBadge.BadgeInfo> result;
        try (MockedStatic<DateUtils> mockedStatic = Mockito.mockStatic(DateUtils.class)) {
            mockedStatic.when(DateUtils::isTodayFoundationDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayArborDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayChildrenDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayWhiteDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayDuckDay).thenReturn(false);

            // when
            NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAttendanceBadges(user);
            result = newlyRegisteredBadge.getNewlyBadges();
        }

        // then
        assertThat(result).hasSize(1);

        List<String> target = result.stream()
                .map(NewlyRegisteredBadge.BadgeInfo::name)
                .toList();
        assertThat(target).containsOnly(BadgeType.ATTENDANCE_1.name());
    }

    @Test
    @DisplayName("출석 횟수에 따른 뱃지 부여 : 30회 출석")
    void updateNewlyAttendanceBadges_2() {
        // given
        User user = TestUtil.createUser();
        for (int i = 0; i < 30; i++) {
            user.increaseAttendanceDay();
        }
        when(badgeRepository.findByUser(user)).thenReturn(new ArrayList<>());

        List<NewlyRegisteredBadge.BadgeInfo> result;
        try (MockedStatic<DateUtils> mockedStatic = Mockito.mockStatic(DateUtils.class)) {
            mockedStatic.when(DateUtils::isTodayFoundationDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayArborDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayChildrenDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayWhiteDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayDuckDay).thenReturn(false);

            // when
            NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAttendanceBadges(user);
            result = newlyRegisteredBadge.getNewlyBadges();
        }

        // then
        assertThat(result).hasSize(4);

        List<String> target = result.stream()
                .map(NewlyRegisteredBadge.BadgeInfo::name)
                .toList();
        assertThat(target).containsOnly(
                BadgeType.ATTENDANCE_1.name(),
                BadgeType.ATTENDANCE_5.name(),
                BadgeType.ATTENDANCE_10.name(),
                BadgeType.ATTENDANCE_30.name()
        );
    }

    @Test
    @DisplayName("기념일 출석 뱃지")
    void updateNewlyAttendanceBadges_3() {
        // given
        User user = TestUtil.createUser();
        user.increaseAttendanceDay();
        when(badgeRepository.findByUser(user)).thenReturn(new ArrayList<>());

        List<NewlyRegisteredBadge.BadgeInfo> result;
        try (MockedStatic<DateUtils> mockedStatic = Mockito.mockStatic(DateUtils.class)) {
            mockedStatic.when(DateUtils::isTodayFoundationDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayArborDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayChildrenDay).thenReturn(true);
            mockedStatic.when(DateUtils::isTodayWhiteDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayDuckDay).thenReturn(false);

            // when
            NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAttendanceBadges(user);
            result = newlyRegisteredBadge.getNewlyBadges();
        }

        // then
        assertThat(result).hasSize(2);

        List<String> target = result.stream()
                .map(NewlyRegisteredBadge.BadgeInfo::name)
                .toList();
        assertThat(target).containsOnly(
                BadgeType.ATTENDANCE_1.name(),
                BadgeType.ATTENDANCE_CHILDREN_DAY.name()
        );
    }

    @Test
    @DisplayName("출석 횟수에 따른 뱃지 부여 : 이미 가지고 있는 뱃지는 부여하지 않는다")
    void updateNewlyAttendanceBadges_4() {
        // given
        User user = TestUtil.createUser();
        for (int i = 0; i < 30; i++) {
            user.increaseAttendanceDay();
        }
        when(badgeRepository.findByUser(user)).thenReturn(List.of(
                new Badge(user, BadgeType.ATTENDANCE_1),
                new Badge(user, BadgeType.ATTENDANCE_10),
                new Badge(user, BadgeType.ATTENDANCE_CHILDREN_DAY)
        ));

        List<NewlyRegisteredBadge.BadgeInfo> result;
        try (MockedStatic<DateUtils> mockedStatic = Mockito.mockStatic(DateUtils.class)) {
            mockedStatic.when(DateUtils::isTodayFoundationDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayArborDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayChildrenDay).thenReturn(true);
            mockedStatic.when(DateUtils::isTodayWhiteDay).thenReturn(false);
            mockedStatic.when(DateUtils::isTodayDuckDay).thenReturn(false);

            // when
            NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAttendanceBadges(user);
            result = newlyRegisteredBadge.getNewlyBadges();
        }

        // then
        assertThat(result).hasSize(2);

        List<String> target = result.stream()
                .map(NewlyRegisteredBadge.BadgeInfo::name)
                .toList();
        assertThat(target).containsOnly(
                BadgeType.ATTENDANCE_5.name(),
                BadgeType.ATTENDANCE_30.name()
        );
    }

    @Test
    @DisplayName("탐험 대학에 따른 뱃지 부여 : 단과대 뱃지")
    void updateNewlyAdventureBadges_1() throws Exception {
        // given
        User user = TestUtil.createUser();
        when(badgeRepository.findByUser(user)).thenReturn(new ArrayList<>());
        when(collegeSpecialBadgeFactory.getBadgeType(any(User.class), any(Set.class), any(Landmark.class)))
                .thenReturn(Optional.empty());

        // when
        Landmark landmark = createLandmark(LandmarkType.공학관A);
        NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAdventureBadges(user, landmark);
        List<NewlyRegisteredBadge.BadgeInfo> result = newlyRegisteredBadge.getNewlyBadges();

        // then
        assertThat(result).hasSize(2);
        List<String> target = result.stream()
                .map(NewlyRegisteredBadge.BadgeInfo::name)
                .toList();
        assertThat(target).containsOnly(
                BadgeType.COLLEGE_OF_ENGINEERING.name(),
                BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY.name()
        );
    }

    @Test
    @DisplayName("탐험 대학에 따른 뱃지 부여 : 단과대 특별 뱃지")
    void updateNewlyAdventureBadges_2() throws Exception {
        // given
        User user = TestUtil.createUser();
        when(badgeRepository.findByUser(user)).thenReturn(new ArrayList<>());
        when(collegeSpecialBadgeFactory.getBadgeType(any(User.class), any(Set.class), any(Landmark.class)))
                .thenReturn(Optional.of(BadgeType.COLLEGE_OF_ENGINEERING_A));

        // when
        Landmark landmark = createLandmark(LandmarkType.공학관A);
        NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAdventureBadges(user, landmark);
        List<NewlyRegisteredBadge.BadgeInfo> result = newlyRegisteredBadge.getNewlyBadges();

        // then
        assertThat(result).hasSize(3);
        List<String> target = result.stream()
                .map(NewlyRegisteredBadge.BadgeInfo::name)
                .toList();
        assertThat(target).containsOnly(
                BadgeType.COLLEGE_OF_ENGINEERING.name(),
                BadgeType.COLLEGE_OF_ENGINEERING_A.name(),
                BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY.name()
        );
    }

    @Test
    @DisplayName("The Dream of Duck 뱃지 부여 : 정상 저장되었다면 true를 반환한다.")
    void saveTheDreamOfDuckBadge_1() {
        // given
        User user = TestUtil.createUser();
        when(badgeRepository.existsByUserAndBadgeType(user, BadgeType.THE_DREAM_OF_DUCK))
                .thenReturn(false);

        // when
        boolean result = badgeService.saveTheDreamOfDuckBadge(user);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("The Dream of Duck 뱃지 부여 : 이미 저장된 뱃지였으면 false를 반환한다.")
    void saveTheDreamOfDuckBadge_2() {
        // given
        User user = TestUtil.createUser();
        when(badgeRepository.existsByUserAndBadgeType(user, BadgeType.THE_DREAM_OF_DUCK))
                .thenReturn(true);

        // when
        boolean result = badgeService.saveTheDreamOfDuckBadge(user);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("뱃지 수동 등록 : 정상 저장되었다면 true를 반환한다.")
    void saveManualBadge_1() {
        // given
        User user = TestUtil.createUser();
        BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(badgeRepository.existsByUserAndBadgeType(user, badgeType)).thenReturn(false);

        // when
        boolean result = badgeService.saveManualBadge(user.getEmail(), badgeType, false);

        // then
        assertThat(result).isTrue();
        assertThat(user.getNotification()).isNull();
    }

    @Test
    @DisplayName("뱃지 수동 등록 : 개인 메시지로 저장")
    void saveManualBadge_2() {
        // given
        User user = TestUtil.createUser();
        BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(badgeRepository.existsByUserAndBadgeType(user, badgeType)).thenReturn(false);

        // when
        boolean result = badgeService.saveManualBadge(user.getEmail(), badgeType, true);

        // then
        assertThat(result).isTrue();
        assertThat(user.getNotification()).isEqualTo("new_badge#" + badgeType.name());
    }

    @Test
    @DisplayName("뱃지 수동 등록 : 이미 가지고 있는 뱃지면 false를 반환한다.")
    void saveManualBadge_3() {
        // given
        User user = TestUtil.createUser();
        BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(badgeRepository.existsByUserAndBadgeType(user, badgeType)).thenReturn(true);

        // when
        boolean result = badgeService.saveManualBadge(user.getEmail(), badgeType, true);

        // then
        assertThat(result).isFalse();
        assertThat(user.getNotification()).isNull();
    }

    private Landmark createLandmark(LandmarkType landmarkType) throws Exception {
        Class<Landmark> landmarkClass = Landmark.class;
        Constructor<Landmark> constructor = landmarkClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Landmark landmark = constructor.newInstance();
        Field targetField = landmarkClass.getDeclaredField("name");
        targetField.setAccessible(true);
        targetField.set(landmark, landmarkType);
        return landmark;
    }

}