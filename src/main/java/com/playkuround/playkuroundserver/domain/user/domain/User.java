package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String notification;

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
        this.notification = null;
    }

    private void addNotification(String name, String description) {
        if (notification == null) {
            notification = name + "#" + description;
        }
        else {
            notification += "@" + name + "#" + description;
        }
    }

    public void addNewBadgeNotification(String description) {
        addNotification("new_badge", description);
    }
}
