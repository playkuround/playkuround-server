package com.playkuround.playkuroundserver.domain.landmark.domain;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Landmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LandmarkType name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private int recognitionRadius;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User firstUser;

    private long highestScore;

    public Landmark(LandmarkType landmarkType, double latitude, double longitude, int recognitionRadius) {
        this.name = landmarkType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recognitionRadius = recognitionRadius;
        this.firstUser = null;
        this.highestScore = 0;
    }

    public void updateFirstUser(User user, long score) {
        if (firstUser == null || this.highestScore < score) {
            this.firstUser = user;
            this.highestScore = score;
        }
    }

    public void deleteRank() {
        this.firstUser = null;
        this.highestScore = 0;
    }

    public boolean isInRecognitionRadius(Location location) {
        Location locationOfLandmark = new Location(latitude, longitude);
        double distance = LocationDistanceUtils.distance(locationOfLandmark, location);

        return distance <= recognitionRadius;
    }

    public boolean isFirstUser(Long userId) {
        if (this.firstUser == null) {
            return false;
        }
        return Objects.equals(this.firstUser.getId(), userId);
    }
}
