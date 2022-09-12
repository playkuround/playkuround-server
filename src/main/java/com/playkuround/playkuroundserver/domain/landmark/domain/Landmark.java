package com.playkuround.playkuroundserver.domain.landmark.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Landmark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private GameType gameType;

    @Builder
    public Landmark(String name, double latitude, double longitude, GameType gameType) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gameType = gameType;
    }
}
