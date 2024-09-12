package com.playkuround.playkuroundserver.domain.user.api.request;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ManualBadgeSaveRequest {

    @Schema(description = "배지를 부여할 유저 이메일 리스트", example = "[user2@konkuk.ac.kr, user2@konkuk.ac.kr]", requiredMode = RequiredMode.REQUIRED)
    private List<@NotBlank(message = "이메일은 필수값입니다.") @Email(message = "올바른 이메일 형식이 아닙니다.") String> userEmail;

    @ValidEnum(enumClass = BadgeType.class, message = "잘못된 배지타입입니다.")
    @Schema(description = "배지타입. 배지타입명은 외부 문서 참고", example = "ATTENDANCE_FOUNDATION_DAY", requiredMode = RequiredMode.REQUIRED)
    private String badge;

    @Schema(description = "개인 메시지로 추가 여부", example = "true", defaultValue = "false")
    private boolean registerMessage;

}
