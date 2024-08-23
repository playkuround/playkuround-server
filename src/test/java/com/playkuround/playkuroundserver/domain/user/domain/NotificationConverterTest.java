package com.playkuround.playkuroundserver.domain.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationConverterTest {

    @Test
    @DisplayName("Set<Notification>이 null이면 null을 반환한다")
    void convertToDatabase_1() {
        // given
        NotificationConverter converter = new NotificationConverter();

        // when
        String result = converter.convertToDatabaseColumn(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Set<Notification>이 empty이면 null을 반환한다")
    void convertToDatabase_2() {
        // given
        NotificationConverter converter = new NotificationConverter();

        // when
        String result = converter.convertToDatabaseColumn(new HashSet<>());

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("메시지끼리는 '@'으로 구분되고, 하나의 메시지에서 notification enum과 detail message는 '#'으로 구분된다.")
    void convertToDatabase_3() {
        // given
        NotificationConverter converter = new NotificationConverter();

        String alarmMessage = "alarm notification";
        String systemCheckMessage = "system check notification";
        Set<Notification> notifications = Set.of(
                new Notification(NotificationEnum.ALARM, alarmMessage),
                new Notification(NotificationEnum.SYSTEM_CHECK, systemCheckMessage)
        );

        // when
        String result = converter.convertToDatabaseColumn(notifications);

        // then
        assertThat(result.split("@")).containsExactlyInAnyOrder(
                NotificationEnum.ALARM.getName() + "#" + alarmMessage,
                NotificationEnum.SYSTEM_CHECK.getName() + "#" + systemCheckMessage
        );
    }


    @Test
    @DisplayName("올바르게 구성된 String이라면 Set<Notification>으로 변환된다.")
    void convertToEntity_1() {
        // given
        NotificationConverter converter = new NotificationConverter();

        String alarmMessage = "alarm notification";
        String systemCheckMessage = "system check notification";
        String notifications = NotificationEnum.ALARM.getName() + "#" + alarmMessage + "@" +
                NotificationEnum.SYSTEM_CHECK.getName() + "#" + systemCheckMessage;

        // when
        Set<Notification> result = converter.convertToEntityAttribute(notifications);

        // then
        assertThat(result).containsExactlyInAnyOrder(
                new Notification(NotificationEnum.ALARM, alarmMessage),
                new Notification(NotificationEnum.SYSTEM_CHECK, systemCheckMessage)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "#",
            "@",
            "alarm",
            "#mesage",
            "alarm##notification",
            "NOT_FOUND#message",
            "NOT_FOUND#message@",
            "NOT_FOUND#message@alarm##message",
    })
    @DisplayName("올바르지 못한 구성으로만 이루어진 문자열은 빈 Set을 반환한다.")
    void convertToEntity_2(String notifications) {
        // given
        NotificationConverter converter = new NotificationConverter();

        // when
        Set<Notification> result = converter.convertToEntityAttribute(notifications);

        // when
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alarm#message@",
            "#mesage@alarm#message",
            "NOT_FOUND#message@alarm#message",
            "alarm#message@NOT_FOUND#message",
    })
    @DisplayName("'@'을 기준으로 정상적인 부분은 올바르게 변환된다.")
    void convertToEntity_3(String notifications) {
        // given
        NotificationConverter converter = new NotificationConverter();

        // when
        Set<Notification> result = converter.convertToEntityAttribute(notifications);

        // when
        assertThat(result).containsExactlyInAnyOrder(
                new Notification(NotificationEnum.ALARM, "message")
        );
    }
}