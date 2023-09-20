package com.playkuround.playkuroundserver.domain.auth.email.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class AuthEmailSendDto {

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        @NotBlank
        @Email
        private String target;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime expireAt;

        private Long sendingCount;

        public static Response of(LocalDateTime expireAt, Long sendingCount) {
            return Response.builder()
                    .expireAt(expireAt)
                    .sendingCount(sendingCount)
                    .build();
        }
    }

}
