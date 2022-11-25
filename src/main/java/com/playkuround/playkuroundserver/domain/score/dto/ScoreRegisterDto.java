package com.playkuround.playkuroundserver.domain.score.dto;

import com.playkuround.playkuroundserver.domain.score.domain.Score;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRegisterDto {

    @ValidEnum(enumClass = ScoreType.class, message = "잘못된 ScoreType 입니다.")
    private String scoreType;

    public Score toEntity(User user) {
        return Score.builder()
                .user(user)
                .scoreType(ScoreType.valueOf(scoreType))
                .build();
    }

}
