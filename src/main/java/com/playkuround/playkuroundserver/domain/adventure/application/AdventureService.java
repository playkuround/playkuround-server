package com.playkuround.playkuroundserver.domain.adventure.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.exception.InvalidLandmarkLocationException;
import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.score.application.TotalScoreService;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.HighestScore;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;
import com.playkuround.playkuroundserver.global.util.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdventureService {

    private final BadgeService badgeService;
    private final UserRepository userRepository;
    private final TotalScoreService totalScoreService;
    private final LandmarkRepository landmarkRepository;
    private final AdventureRepository adventureRepository;
    private final DateTimeService dateTimeService;

    @Transactional
    public List<BadgeType> saveAdventure(AdventureSaveDto adventureSaveDto) {
        Landmark landmark = landmarkRepository.findById(adventureSaveDto.landmarkId())
                .orElseThrow(() -> new LandmarkNotFoundException(adventureSaveDto.landmarkId()));
        validateLocation(landmark, adventureSaveDto.requestLocation());

        User user = adventureSaveDto.user();
        ScoreType scoreType = adventureSaveDto.scoreType();
        long score = adventureSaveDto.score();

        updateUserScore(user, scoreType, score);
        saveAdventure(user, landmark, scoreType, score);
        updateLandmarkHighestScore(user, landmark);

        return badgeService.updateNewlyAdventureBadges(user, landmark);
    }

    private void validateLocation(Landmark landmark, Location location) {
        if (!landmark.isInRecognitionRadius(location)) {
            throw new InvalidLandmarkLocationException();
        }
    }

    private void updateUserScore(User user, ScoreType scoreType, long score) {
        HighestScore highestScore = user.getHighestScore();
        highestScore.updateGameHighestScore(scoreType, score);
        userRepository.save(user);

        totalScoreService.incrementTotalScore(user, score);
    }

    private void saveAdventure(User user, Landmark landmark, ScoreType scoreType, long score) {
        Adventure adventure = new Adventure(user, landmark, scoreType, score);
        adventureRepository.save(adventure);
    }

    private void updateLandmarkHighestScore(User user, Landmark landmark) {
        LocalDate now = dateTimeService.getLocalDateNow();
        LocalDateTime monthStartDateTime = DateTimeUtils.getMonthStartDateTime(now);

        long sumScore = adventureRepository.sumScoreByUserAndLandmarkAfter(user, landmark, monthStartDateTime);
        landmark.updateFirstUser(user, sumScore);
    }
}
