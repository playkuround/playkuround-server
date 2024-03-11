package com.playkuround.playkuroundserver.domain.user.api.request;

import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.global.validation.ValidEnum;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Schema(externalDocs = @ExternalDocumentation(url = "https://www.notion.so/major-4dd11f19ece5409aacaa21a8ccc28dad?pvs=4"))
public class UserRegisterRequest {

    @NotBlank(message = "이메일은 필수값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(description = "건국대 이메일", example = "tester@konkuk.ac.kr", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "닉네임은 필수값입니다.")
    @Length(min = 2, max = 8, message = "닉네임은 2글자 이상 8글자 이하여야 합니다.")
    @Pattern(regexp = "^[0-9a-zA-Z가-힣]*$", message = "닉네임은 한글, 영어, 숫자만 허용됩니다.")
    @Schema(description = "사용할 닉네임(한글, 영어, 숫자만 허용)", example = "tester", minLength = 2, maxLength = 8)
    private String nickname;

    @ValidEnum(enumClass = Major.class, message = "잘못된 학과명입니다.")
    @Schema(description = "학과. 학과 리스트는 외부 문서 참고", example = "컴퓨터공학부", requiredMode = RequiredMode.REQUIRED)
    private String major;

    @NotBlank(message = "인증 토큰은 필수값입니다.")
    @Schema(description = "이메일 인증 완료 시 받았던 인증 토큰", example = "0a72d4d0-bc97-4776-811f-ade033cd0ba3")
    private String authVerifyToken;
}
