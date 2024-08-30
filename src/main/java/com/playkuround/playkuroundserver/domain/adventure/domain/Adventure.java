package com.playkuround.playkuroundserver.domain.adventure.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Adventure extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landmark_id")
    private Landmark landmark;

    @Column(nullable = false)
    private Long score;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ScoreType scoreType;

    public Adventure(User user, Landmark landmark, ScoreType scoreType, Long score) {
        this.user = user;
        this.landmark = landmark;
        this.scoreType = scoreType;
        this.score = score;
    }
}
