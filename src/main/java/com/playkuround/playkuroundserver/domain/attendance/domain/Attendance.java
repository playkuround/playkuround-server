package com.playkuround.playkuroundserver.domain.attendance.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Latitude
    @Column(nullable = false)
    private double latitude;

    @Longitude
    @Column(nullable = false)
    private double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private Attendance(double latitude, double longitude, User user) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }

    public static Attendance createAttendance(User user, Location location) {
        return new Attendance(location.latitude(), location.longitude(), user);
    }
}
