package com.playkuround.playkuroundserver.domain.landmark.domain;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void updateFirstUser(User user, long score) {
        if (score == 0) return;

        if (firstUser == null || this.highestScore < score) {
            this.firstUser = user;
            this.highestScore = score;
        }
    }
}
