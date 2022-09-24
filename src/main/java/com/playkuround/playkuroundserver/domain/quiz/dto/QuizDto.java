package com.playkuround.playkuroundserver.domain.quiz.dto;

import com.playkuround.playkuroundserver.domain.quiz.domain.Quiz;
import lombok.*;

import java.util.Arrays;
import java.util.List;

public class QuizDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private String question;
        private List<String> exampleList;
        private Integer answer;

        public static QuizDto.Response of(Quiz quiz) {
            return Response.builder()
                    .question(quiz.getQuestion())
                    .exampleList(Arrays.asList(
                            quiz.getExample1(),
                            quiz.getExample2(),
                            quiz.getExample3(),
                            quiz.getExample4()))
                    .answer(quiz.getAnswer())
                    .build();
        }
    }
}