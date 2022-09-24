package com.playkuround.playkuroundserver.domain.quiz.domain;

import com.playkuround.playkuroundserver.domain.user.domain.Major;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long landmarkId;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String example1;

    @Column(nullable = false)
    private String example2;

    @Column(nullable = false)
    private String example3;

    @Column(nullable = false)
    private String example4;

    @Column(nullable = false)
    private Integer answer;

    @Builder
    public Quiz(Long landmarkId, String question, String example1, String example2, String example3, String example4, Integer answer) {
        this.landmarkId = landmarkId;
        this.question = question;
        this.example1 = example1;
        this.example2 = example2;
        this.example3 = example3;
        this.example4 = example4;
        this.answer = answer;
    }
}
