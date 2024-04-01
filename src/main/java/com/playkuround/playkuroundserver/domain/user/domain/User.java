package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Major major;

    @Column(nullable = false)
    private int attendanceDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Embedded
    private HighestScore highestScore;

    @Convert(converter = NotificationConverter.class)
    private Set<Notification> notification;

    private User(String email, String nickname, Major major, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.major = major;
        this.role = role;
    }

    public static User create(String email, String nickname, Major major, Role role) {
        return new User(email, nickname, major, role);
    }

    public HighestScore getHighestScore() {
        if (highestScore == null) {
            highestScore = new HighestScore();
        }
        return highestScore;
    }

    public void increaseAttendanceDay() {
        attendanceDays++;
    }

    public void clearNotification() {
        if (this.notification != null) {
            this.notification.clear();
        }
    }

    public void addNewBadgeNotification(BadgeType badgeType) {
        if (this.notification == null) {
            this.notification = new HashSet<>();
        }
        this.notification.add(new Notification(NotificationEnum.NEW_BADGE, badgeType.name()));
    }

    public void updateHighestRank(long rank, long score) {
        HighestScore highestScore = getHighestScore();
        highestScore.updateHighestTotalLank(rank, score);
    }
}
