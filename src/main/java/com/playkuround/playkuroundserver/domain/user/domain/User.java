package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false, unique = true)
    @Length(min = 2, max = 8)
    @Pattern(regexp = "^[0-9a-zA-Z가-힣]*$")
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

    @Builder
    public User(@NonNull String email, @NonNull String nickname, @NonNull Major major, @NonNull Role role) {
        this.email = email;
        this.nickname = nickname;
        this.major = major;
        this.attendanceDays = 0;
        this.role = role;
    }

    public void increaseAttendanceDay() {
        attendanceDays++;
    }

    public void clearNotification() {
        this.notification = null;
    }

    public void addNotification(String name, String description) {
        if (notification == null) {
            notification = name + "#" + description;
        }
        else {
            notification += "@" + name + "#" + description;
        }
    }
}
