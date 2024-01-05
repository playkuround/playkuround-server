package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

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
    private Integer ConsecutiveAttendanceDays;

    @Column(nullable = false)
    private LocalDateTime lastAttendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Embedded
    private HighestScore highestScore;

    @Builder
    public User(@NonNull String email, @NonNull String nickname, @NonNull Major major, @NonNull Role role) {
        this.email = email;
        this.nickname = nickname;
        this.major = major;
        this.ConsecutiveAttendanceDays = 0;
        this.lastAttendanceDate = LocalDateTime.now().minusDays(1);
        this.role = role;
    }

    public void updateAttendanceDate() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        if (lastAttendanceDate.equals(yesterday)) {
            this.ConsecutiveAttendanceDays++;
        }
        else {
            this.ConsecutiveAttendanceDays = 0;
        }
        this.lastAttendanceDate = LocalDateTime.now();
    }


}
