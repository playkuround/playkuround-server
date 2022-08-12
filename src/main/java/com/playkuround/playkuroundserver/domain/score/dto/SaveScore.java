package com.playkuround.playkuroundserver.domain.score.dto;

import com.playkuround.playkuroundserver.domain.score.domain.Score;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.score.exception.ScoreTypeNotMatchException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Getter
@Setter
public class SaveScore {

    @NotBlank
    private String scoreType;

    private ScoreType getScoreType() {
        if (Objects.equals(scoreType, ScoreType.ADVENTURE.name())) return ScoreType.ADVENTURE;
        else if (Objects.equals(scoreType, ScoreType.ATTENDANCE.name())) return ScoreType.ATTENDANCE;
        else if (Objects.equals(scoreType, ScoreType.EXTRA_ADVENTURE.name())) return ScoreType.EXTRA_ADVENTURE;
        else throw new ScoreTypeNotMatchException();
    }

    public Score toEntity(User user) {
        return Score.builder()
                .user(user)
                .scoreType(getScoreType())
                .build();
    }
}
