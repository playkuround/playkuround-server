package com.playkuround.playkuroundserver.domain.attendance.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime attendanceDateTime;

    private Attendance(User user, double latitude, double longitude, LocalDateTime attendanceDateTime) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.attendanceDateTime = attendanceDateTime;
    }

    public static Attendance of(User user, Location location, LocalDateTime attendanceDateTime) {
        return new Attendance(user, location.latitude(), location.longitude(), attendanceDateTime);
    }
}
