package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.badge.application.college_special_badge.CollegeSpecialBadgeFactory;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Notification;
import com.playkuround.playkuroundserver.domain.user.domain.NotificationEnum;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.stream.Stream;

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

    @Nested
    @DisplayName("뱃지 조회하기")
    class findBadge {

        @Test
        @DisplayName("뱃지 개수가 0개이면 빈리스트가 반환된다")
        void success_1() {
            // given
            User user = TestUtil.createUser();
            when(badgeRepository.findByUser(user)).thenReturn(new ArrayList<>());

            // when
            List<Badge> result = badgeService.findBadgeByEmail(user);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("뱃지 개수 3개 조회")
        void success_2() {
            // given
            User user = TestUtil.createUser();
            List<Badge> badges = List.of(
                    new Badge(user, BadgeType.ATTENDANCE_1),
                    new Badge(user, BadgeType.MONTHLY_RANKING_3),
                    new Badge(user, BadgeType.COLLEGE_OF_ENGINEERING_A)
            );
            when(badgeRepository.findByUser(user)).thenReturn(badges);

            // when
            List<Badge> result = badgeService.findBadgeByEmail(user);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting("badgeType")
                    .containsOnly(
                            BadgeType.ATTENDANCE_1,
                            BadgeType.MONTHLY_RANKING_3,
                            BadgeType.COLLEGE_OF_ENGINEERING_A
                    );
        }
    }

    @Nested
    @DisplayName("출석에 따른 뱃지 부여")
    class updateNewlyAttendanceBadges {

        static Stream<Arguments> generateAttendanceBadgeTestData() {
            return Stream.of(
                    Arguments.of(1,
                            List.of(BadgeType.ATTENDANCE_1.name())),
                    Arguments.of(5,
                            List.of(BadgeType.ATTENDANCE_1.name(),
                                    BadgeType.ATTENDANCE_5.name())),
                    Arguments.of(10,
                            List.of(BadgeType.ATTENDANCE_1.name(),
                                    BadgeType.ATTENDANCE_5.name(),
                                    BadgeType.ATTENDANCE_10.name())),
                    Arguments.of(30,
                            List.of(BadgeType.ATTENDANCE_1.name(),
                                    BadgeType.ATTENDANCE_5.name(),
                                    BadgeType.ATTENDANCE_10.name(),
                                    BadgeType.ATTENDANCE_30.name()))
            );
        }

        @ParameterizedTest
        @MethodSource("generateAttendanceBadgeTestData")
        @DisplayName("출석 횟수에 따른 뱃지 획득")
        void success_1(int attendanceDay, List<String> expected) {
            // given
            User user = TestUtil.createUser();
            for (int i = 0; i < attendanceDay; i++) {
                user.increaseAttendanceDay();
            }
            when(badgeRepository.findByUser(user)).thenReturn(new ArrayList<>());

            List<NewlyRegisteredBadge.BadgeInfo> result;
            try (MockedStatic<DateUtils> mockedStatic = Mockito.mockStatic(DateUtils.class)) {
                mockedStatic.when(DateUtils::isTodayDuckDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayArborDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayWhiteDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayChildrenDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayFoundationDay).thenReturn(false);

                // when
                NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAttendanceBadges(user);
                result = newlyRegisteredBadge.getNewlyBadges();
            }

            // then
            List<String> target = result.stream()
                    .map(NewlyRegisteredBadge.BadgeInfo::name)
                    .toList();
            assertThat(target).isEqualTo(expected);
        }

        @Test
        @DisplayName("기념일 출석 뱃지")
        void success_2() {
            // given
            User user = TestUtil.createUser();
            when(badgeRepository.findByUser(user)).thenReturn(new ArrayList<>());

            List<NewlyRegisteredBadge.BadgeInfo> result;
            try (MockedStatic<DateUtils> mockedStatic = Mockito.mockStatic(DateUtils.class)) {
                mockedStatic.when(DateUtils::isTodayDuckDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayArborDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayWhiteDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayChildrenDay).thenReturn(true);
                mockedStatic.when(DateUtils::isTodayFoundationDay).thenReturn(false);

                // when
                NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAttendanceBadges(user);
                result = newlyRegisteredBadge.getNewlyBadges();
            }

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo(BadgeType.ATTENDANCE_CHILDREN_DAY.name());
        }

        @Test
        @DisplayName("이미 가지고 있는 뱃지는 부여하지 않는다")
        void success_3() {
            // given
            User user = TestUtil.createUser();
            for (int i = 0; i < 30; i++) {
                user.increaseAttendanceDay();
            }
            when(badgeRepository.findByUser(user))
                    .thenReturn(List.of(
                            new Badge(user, BadgeType.ATTENDANCE_1),
                            new Badge(user, BadgeType.ATTENDANCE_10),
                            new Badge(user, BadgeType.ATTENDANCE_CHILDREN_DAY)
                    ));

            List<NewlyRegisteredBadge.BadgeInfo> result;
            try (MockedStatic<DateUtils> mockedStatic = Mockito.mockStatic(DateUtils.class)) {
                mockedStatic.when(DateUtils::isTodayDuckDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayWhiteDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayArborDay).thenReturn(false);
                mockedStatic.when(DateUtils::isTodayChildrenDay).thenReturn(true);
                mockedStatic.when(DateUtils::isTodayFoundationDay).thenReturn(false);

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
    }

    @Nested
    @DisplayName("탐험 대학에 따른 뱃지 부여")
    class updateNewlyAdventureBadges {

        static Stream<Arguments> generateAdventureBadgeTestData() {
            return Stream.of(
                    Arguments.of(LandmarkType.인문학관,
                            List.of(BadgeType.COLLEGE_OF_LIBERAL_ARTS.name())),
                    Arguments.of(LandmarkType.과학관,
                            List.of(BadgeType.COLLEGE_OF_SCIENCES.name())),
                    Arguments.of(LandmarkType.건축관,
                            List.of(BadgeType.COLLEGE_OF_ARCHITECTURE.name())),
                    Arguments.of(LandmarkType.공학관A,
                            List.of(BadgeType.COLLEGE_OF_ENGINEERING.name(),
                                    BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY.name())),
                    Arguments.of(LandmarkType.공학관B,
                            List.of(BadgeType.COLLEGE_OF_ENGINEERING.name(),
                                    BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY.name())),
                    Arguments.of(LandmarkType.공학관C,
                            List.of(BadgeType.COLLEGE_OF_ENGINEERING.name(),
                                    BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY.name())),
                    Arguments.of(LandmarkType.신공학관,
                            List.of(BadgeType.COLLEGE_OF_ENGINEERING.name())),
                    Arguments.of(LandmarkType.상허연구관,
                            List.of(BadgeType.COLLEGE_OF_SOCIAL_SCIENCES.name())),
                    Arguments.of(LandmarkType.경영관,
                            List.of(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION.name())),
                    Arguments.of(LandmarkType.부동산학관,
                            List.of(BadgeType.COLLEGE_OF_REAL_ESTATE.name())),
                    Arguments.of(LandmarkType.생명과학관,
                            List.of(BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY.name())),
                    Arguments.of(LandmarkType.동물생명과학관,
                            List.of(BadgeType.COLLEGE_OF_BIOLOGICAL_SCIENCES.name())),
                    Arguments.of(LandmarkType.수의학관,
                            List.of(BadgeType.COLLEGE_OF_VETERINARY_MEDICINE.name())),
                    Arguments.of(LandmarkType.예디대,
                            List.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN.name())),
                    Arguments.of(LandmarkType.공예관,
                            List.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN.name())),
                    Arguments.of(LandmarkType.교육과학관,
                            List.of(BadgeType.COLLEGE_OF_EDUCATION.name()))
            );
        }

        @ParameterizedTest
        @MethodSource("generateAdventureBadgeTestData")
        @DisplayName("대학별 뱃지")
        void success_1(LandmarkType landmarkType, List<String> badgeTypeList) throws Exception {
            // given
            User user = TestUtil.createUser();
            when(badgeRepository.findByUser(user))
                    .thenReturn(new ArrayList<>());
            when(collegeSpecialBadgeFactory.getBadgeType(any(User.class), any(Set.class), any(Landmark.class)))
                    .thenReturn(Optional.empty());

            // when
            Landmark landmark = createLandmark(landmarkType);
            NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAdventureBadges(user, landmark);
            List<NewlyRegisteredBadge.BadgeInfo> result = newlyRegisteredBadge.getNewlyBadges();

            // then
            List<String> target = result.stream()
                    .map(NewlyRegisteredBadge.BadgeInfo::name)
                    .toList();
            assertThat(target).isEqualTo(badgeTypeList);
        }

        @Test
        @DisplayName("단과대 특별 뱃지")
        void success_2() throws Exception {
            // given
            User user = TestUtil.createUser();
            when(badgeRepository.findByUser(user))
                    .thenReturn(new ArrayList<>());
            when(collegeSpecialBadgeFactory.getBadgeType(any(User.class), any(Set.class), any(Landmark.class)))
                    .thenReturn(Optional.of(BadgeType.COLLEGE_OF_ENGINEERING_A));

            // when
            Landmark landmark = createLandmark(LandmarkType.공학관A);
            NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAdventureBadges(user, landmark);
            List<NewlyRegisteredBadge.BadgeInfo> result = newlyRegisteredBadge.getNewlyBadges();

            // then
            List<String> target = result.stream()
                    .map(NewlyRegisteredBadge.BadgeInfo::name)
                    .toList();
            assertThat(target).containsOnly(
                    BadgeType.COLLEGE_OF_ENGINEERING.name(),
                    BadgeType.COLLEGE_OF_ENGINEERING_A.name(),
                    BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY.name()
            );
        }

        private Landmark createLandmark(LandmarkType landmarkType) throws Exception {
            Class<Landmark> landmarkClass = Landmark.class;
            Constructor<Landmark> constructor = landmarkClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            Field targetField = landmarkClass.getDeclaredField("name");
            targetField.setAccessible(true);

            Landmark landmark = constructor.newInstance();
            targetField.set(landmark, landmarkType);

            return landmark;
        }
    }

    @Nested
    @DisplayName("The Dream of Duck 뱃지 부여")
    class saveTheDreamOfDuckBadge {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("정상 저장되었다면 true, 이미 저장된 뱃지였으면 false를 반환한다.")
        void success_1(boolean hasBadge) {
            // given
            User user = TestUtil.createUser();
            when(badgeRepository.existsByUserAndBadgeType(user, BadgeType.THE_DREAM_OF_DUCK))
                    .thenReturn(hasBadge);

            // when
            boolean result = badgeService.saveTheDreamOfDuckBadge(user);

            // then
            assertThat(result).isEqualTo(!hasBadge);
        }
    }

    @Nested
    @DisplayName("뱃지 수동 등록")
    class saveManualBadge {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("정상 저장되었다면 true, 이미 저장된 뱃지였으면 false를 반환한다.")
        void success_1(boolean hasBadge) {
            // given
            User user = TestUtil.createUser();
            BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
            when(userRepository.findByEmail(user.getEmail()))
                    .thenReturn(Optional.of(user));
            when(badgeRepository.existsByUserAndBadgeType(user, badgeType))
                    .thenReturn(hasBadge);

            // when
            boolean result = badgeService.saveManualBadge(user.getEmail(), badgeType, false);

            // then
            assertThat(result).isEqualTo(!hasBadge);
            assertThat(user.getNotification()).isNull();
        }

        @Test
        @DisplayName("개인 메시지로 저장")
        void success_2() {
            // given
            User user = TestUtil.createUser();
            BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
            when(userRepository.findByEmail(user.getEmail()))
                    .thenReturn(Optional.of(user));
            when(badgeRepository.existsByUserAndBadgeType(user, badgeType))
                    .thenReturn(false);

            // when
            boolean result = badgeService.saveManualBadge(user.getEmail(), badgeType, true);

            // then
            assertThat(result).isTrue();
            assertThat(user.getNotification())
                    .containsOnly(new Notification(NotificationEnum.NEW_BADGE, badgeType.name()));
        }
    }

}