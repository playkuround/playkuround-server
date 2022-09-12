package com.playkuround.playkuroundserver.domain.attendance.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Latitude
    @Column(nullable = false)
    private Double latitude;

    @Longitude
    @Column(nullable = false)
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Attendance(Double latitude, Double longitude, User user) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }

    public static Attendance createAttendance(Double latitude, Double longitude, User user) {
        return Attendance.builder()
                .latitude(latitude)
                .longitude(longitude)
                .user(user)
                .build();
    }

}
