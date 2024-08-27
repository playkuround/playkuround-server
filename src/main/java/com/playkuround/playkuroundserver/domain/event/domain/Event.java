package com.playkuround.playkuroundserver.domain.event.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String imageUrl;
    private String description;
    private String referenceUrl;
    private boolean display;

    public Event(String title, String imageUrl, String description, String referenceUrl, boolean display) {
        Objects.requireNonNull(title, "title must be provided.");

        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.referenceUrl = referenceUrl;
        this.display = display;
    }
}
