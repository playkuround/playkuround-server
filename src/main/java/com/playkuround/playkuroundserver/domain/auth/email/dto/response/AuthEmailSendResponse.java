package com.playkuround.playkuroundserver.domain.auth.email.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthEmailSendResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Schema(description = "인증 만료 시각", example = "2023-11-01 20:03:10", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expireAt;

    @Schema(description = "금일 해당 주소로 전송된 메일 개수", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sendingCount;
}
