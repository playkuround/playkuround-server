package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.badge.application.college_special_badge.CollegeSpecialBadge;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Notification;
import com.playkuround.playkuroundserver.domain.user.domain.NotificationEnum;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @InjectMocks
    private BadgeService badgeService;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollegeSpecialBadge collegeSpecialBadge;

    @Mock
    private DateTimeService dateTimeService;

    @Nested
    @DisplayName("배지 조회하기")
    class findBadge {

        @Test
        @DisplayName("배지 개수가 0개이면 빈리스트가 반환된다")
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
        @DisplayName("배지 개수 3개 조회")
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
    @DisplayName("출석에 따른 배지 부여")
    class updateNewlyAttendanceBadges {

        static Stream<Arguments> generateAttendanceBadgeTestData() {
            return Stream.of(
                    Arguments.of(1,
                            List.of(BadgeType.ATTENDANCE_1)),
                    Arguments.of(5,
                            List.of(BadgeType.ATTENDANCE_1,
                                    BadgeType.ATTENDANCE_5)),
                    Arguments.of(10,
                            List.of(BadgeType.ATTENDANCE_1,
                                    BadgeType.ATTENDANCE_5,
                                    BadgeType.ATTENDANCE_10)),
                    Arguments.of(30,
                            List.of(BadgeType.ATTENDANCE_1,
                                    BadgeType.ATTENDANCE_5,
                                    BadgeType.ATTENDANCE_10,
                                    BadgeType.ATTENDANCE_30))
            );
        }

        @ParameterizedTest
        @MethodSource("generateAttendanceBadgeTestData")
        @DisplayName("출석 횟수에 따른 배지 획득")
        void success_1(int attendanceDay, List<BadgeType> expected) {
            // given
            User user = TestUtil.createUser();
            for (int i = 0; i < attendanceDay; i++) {
                user.increaseAttendanceDay();
            }
            when(badgeRepository.findByUser(user))
                    .thenReturn(new ArrayList<>());
            when(dateTimeService.getLocalDateNow())
                    .thenReturn(LocalDate.of(2024, 7, 1));

            List<BadgeType> result;
            try (MockedStatic<DateTimeUtils> mockedStatic = Mockito.mockStatic(DateTimeUtils.class)) {
                mockedStatic.when(() -> DateTimeUtils.isDuckDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isArborDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isWhiteDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isChildrenDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isFoundationDay(any())).thenReturn(false);

                // when
                result = badgeService.updateNewlyAttendanceBadges(user);
            }

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("기념일 출석 배지")
        void success_2() {
            // given
            User user = TestUtil.createUser();
            when(badgeRepository.findByUser(user))
                    .thenReturn(new ArrayList<>());
            when(dateTimeService.getLocalDateNow())
                    .thenReturn(LocalDate.of(2024, 7, 1));

            List<BadgeType> result;
            try (MockedStatic<DateTimeUtils> mockedStatic = Mockito.mockStatic(DateTimeUtils.class)) {
                mockedStatic.when(() -> DateTimeUtils.isDuckDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isArborDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isWhiteDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isChildrenDay(any())).thenReturn(true);
                mockedStatic.when(() -> DateTimeUtils.isFoundationDay(any())).thenReturn(false);

                // when
                result = badgeService.updateNewlyAttendanceBadges(user);
            }

            // then
            assertThat(result).hasSize(1)
                    .containsExactly(BadgeType.ATTENDANCE_CHILDREN_DAY);
        }

        @Test
        @DisplayName("이미 가지고 있는 배지는 부여하지 않는다")
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
            when(dateTimeService.getLocalDateNow())
                    .thenReturn(LocalDate.of(2024, 7, 1));

            List<BadgeType> result;
            try (MockedStatic<DateTimeUtils> mockedStatic = Mockito.mockStatic(DateTimeUtils.class)) {
                mockedStatic.when(() -> DateTimeUtils.isDuckDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isArborDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isWhiteDay(any())).thenReturn(false);
                mockedStatic.when(() -> DateTimeUtils.isChildrenDay(any())).thenReturn(true);
                mockedStatic.when(() -> DateTimeUtils.isFoundationDay(any())).thenReturn(false);

                // when
                result = badgeService.updateNewlyAttendanceBadges(user);
            }

            // then
            assertThat(result).hasSize(2)
                    .containsExactlyInAnyOrder(BadgeType.ATTENDANCE_5, BadgeType.ATTENDANCE_30);
        }
    }

    @Nested
    @DisplayName("탐험 대학에 따른 배지 부여")
    class updateNewlyAdventureBadges {

        static Stream<Arguments> generateAdventureBadgeTestData() {
            return Stream.of(
                    Arguments.of(LandmarkType.인문학관,
                            List.of(BadgeType.COLLEGE_OF_LIBERAL_ARTS)),
                    Arguments.of(LandmarkType.과학관,
                            List.of(BadgeType.COLLEGE_OF_SCIENCES)),
                    Arguments.of(LandmarkType.건축관,
                            List.of(BadgeType.COLLEGE_OF_ARCHITECTURE)),
                    Arguments.of(LandmarkType.공학관A,
                            List.of(BadgeType.COLLEGE_OF_ENGINEERING,
                                    BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY)),
                    Arguments.of(LandmarkType.공학관B,
                            List.of(BadgeType.COLLEGE_OF_ENGINEERING,
                                    BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY)),
                    Arguments.of(LandmarkType.공학관C,
                            List.of(BadgeType.COLLEGE_OF_ENGINEERING,
                                    BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY)),
                    Arguments.of(LandmarkType.신공학관,
                            List.of(BadgeType.COLLEGE_OF_ENGINEERING)),
                    Arguments.of(LandmarkType.상허연구관,
                            List.of(BadgeType.COLLEGE_OF_SOCIAL_SCIENCES)),
                    Arguments.of(LandmarkType.경영관,
                            List.of(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION)),
                    Arguments.of(LandmarkType.부동산학관,
                            List.of(BadgeType.COLLEGE_OF_REAL_ESTATE)),
                    Arguments.of(LandmarkType.생명과학관,
                            List.of(BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY)),
                    Arguments.of(LandmarkType.동물생명과학관,
                            List.of(BadgeType.COLLEGE_OF_BIOLOGICAL_SCIENCES)),
                    Arguments.of(LandmarkType.수의학관,
                            List.of(BadgeType.COLLEGE_OF_VETERINARY_MEDICINE)),
                    Arguments.of(LandmarkType.예디대,
                            List.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN)),
                    Arguments.of(LandmarkType.공예관,
                            List.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN)),
                    Arguments.of(LandmarkType.교육과학관,
                            List.of(BadgeType.COLLEGE_OF_EDUCATION))
            );
        }

        @ParameterizedTest
        @MethodSource("generateAdventureBadgeTestData")
        @DisplayName("대학별 배지")
        void success_1(LandmarkType landmarkType, List<BadgeType> badgeTypeList) throws Exception {
            // given
            User user = TestUtil.createUser();
            when(badgeRepository.findByUser(user))
                    .thenReturn(new ArrayList<>());
            when(collegeSpecialBadge.getBadgeTypes(any(User.class), any(Landmark.class)))
                    .thenReturn(List.of());

            Landmark landmark = createLandmark(landmarkType);

            // when
            List<BadgeType> newlyRegisteredBadge = badgeService.updateNewlyAdventureBadges(user, landmark);

            // then
            assertThat(newlyRegisteredBadge).containsExactlyInAnyOrderElementsOf(badgeTypeList);
        }

        @Test
        @DisplayName("단과대 특별 배지")
        void success_2() throws Exception {
            // given
            User user = TestUtil.createUser();
            when(badgeRepository.findByUser(user))
                    .thenReturn(new ArrayList<>());
            when(collegeSpecialBadge.getBadgeTypes(any(User.class), any(Landmark.class)))
                    .thenReturn(List.of(BadgeType.COLLEGE_OF_ENGINEERING_A));

            Landmark landmark = createLandmark(LandmarkType.공학관A);

            // when
            List<BadgeType> result = badgeService.updateNewlyAdventureBadges(user, landmark);

            // then
            assertThat(result).containsExactlyInAnyOrder(
                    BadgeType.COLLEGE_OF_ENGINEERING,
                    BadgeType.COLLEGE_OF_ENGINEERING_A,
                    BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY
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
    @DisplayName("The Dream of Duck 배지 부여")
    class saveTheDreamOfDuckBadge {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("정상 저장되었다면 true, 이미 저장된 배지였으면 false를 반환한다.")
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
    @DisplayName("배지 수동 등록")
    class saveManualBadge {

        @Test
        @DisplayName("새롭게 정상 저장된 배지 개수를 반환한다.")
        void success_1() {
            // given
            List<User> users = List.of(
                    TestUtil.createUser("user1@konkuk.ac.kr", "user1", Major.경영학과),
                    TestUtil.createUser("user2@konkuk.ac.kr", "user2", Major.컴퓨터공학부),
                    TestUtil.createUser("user3@konkuk.ac.kr", "user3", Major.국제무역학과)
            );
            List<String> emails = users.stream()
                    .map(User::getEmail)
                    .toList();
            when(userRepository.findByEmailIn(emails))
                    .thenReturn(users);

            BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
            when(badgeRepository.existsByUserAndBadgeType(users.get(0), badgeType))
                    .thenReturn(true);
            when(badgeRepository.existsByUserAndBadgeType(users.get(1), badgeType))
                    .thenReturn(false);
            when(badgeRepository.existsByUserAndBadgeType(users.get(2), badgeType))
                    .thenReturn(false);

            // when
            int result = badgeService.saveManualBadge(emails, badgeType, false);

            // then
            assertThat(result).isEqualTo(2);
            verify(badgeRepository, times(2)).save(any(Badge.class));
        }

        @Test
        @DisplayName("registerMessage 값을 true로 설정하면 user.notification에 원소가 추가된다.")
        void success_2() {
            // given
            List<User> users = List.of(
                    TestUtil.createUser("user1@konkuk.ac.kr", "user1", Major.경영학과),
                    TestUtil.createUser("user2@konkuk.ac.kr", "user2", Major.컴퓨터공학부),
                    TestUtil.createUser("user3@konkuk.ac.kr", "user3", Major.국제무역학과)
            );
            List<String> emails = users.stream()
                    .map(User::getEmail)
                    .toList();
            when(userRepository.findByEmailIn(emails))
                    .thenReturn(users);

            BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
            when(badgeRepository.existsByUserAndBadgeType(users.get(0), badgeType))
                    .thenReturn(true);
            when(badgeRepository.existsByUserAndBadgeType(users.get(1), badgeType))
                    .thenReturn(false);
            when(badgeRepository.existsByUserAndBadgeType(users.get(2), badgeType))
                    .thenReturn(false);

            // when
            int result = badgeService.saveManualBadge(emails, badgeType, true);

            // then
            assertThat(result).isEqualTo(2);
            verify(badgeRepository, times(2)).save(any(Badge.class));

            assertThat(users.get(0).getNotification()).isEmpty();
            assertThat(users.get(1).getNotification())
                    .containsOnly(new Notification(NotificationEnum.NEW_BADGE, badgeType.name()));
            assertThat(users.get(2).getNotification())
                    .containsOnly(new Notification(NotificationEnum.NEW_BADGE, badgeType.name()));
        }

        @Test
        @DisplayName("존재하지 않는 이메일이 하나라도 존재하면 예외를 던진다.")
        void fail_1() {
            // given
            List<User> users = List.of(
                    TestUtil.createUser("user1@konkuk.ac.kr", "user1", Major.경영학과),
                    TestUtil.createUser("user2@konkuk.ac.kr", "user2", Major.컴퓨터공학부),
                    TestUtil.createUser("user3@konkuk.ac.kr", "user3", Major.국제무역학과)
            );
            List<String> emails = users.subList(1, users.size()).stream()
                    .map(User::getEmail)
                    .toList();
            when(userRepository.findByEmailIn(emails))
                    .thenReturn(users);

            BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;

            // expected
            assertThatThrownBy(() -> badgeService.saveManualBadge(emails, badgeType, false))
                    .isInstanceOf(UserNotFoundException.class);
            verify(badgeRepository, times(0)).save(any(Badge.class));
        }
    }

}