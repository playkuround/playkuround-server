package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.dto.UserAndScore;
import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.fakedoor.dao.FakeDoorRepository;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeleteService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final LandmarkRepository landmarkRepository;
    private final FakeDoorRepository fakeDoorRepository;
    private final AdventureRepository adventureRepository;
    private final AttendanceRepository attendanceRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final String redisSetKey = "ranking";
    private final RedisTemplate<String, String> redisTemplate;
    private final DateTimeService dateTimeService;

    @Transactional
    public void deleteUser(User user) {
        badgeRepository.deleteByUser(user);
        fakeDoorRepository.deleteByUser(user);
        attendanceRepository.deleteByUser(user);
        refreshTokenRepository.deleteByUserEmail(user.getEmail());
        updateLandmarkFirstUser(user);
        adventureRepository.deleteByUser(user);

        deleteTotalScoreRank(user);
        userRepository.delete(user);
    }

    private void deleteTotalScoreRank(User user) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.remove(redisSetKey, user.getEmail());
    }

    private void updateLandmarkFirstUser(User user) {
        LocalDate now = dateTimeService.getLocalDateNow();
        LocalDateTime monthStartDateTime = DateTimeUtils.getMonthStartDateTime(now);

        List<Landmark> landmarks = landmarkRepository.findAll();
        landmarks.stream()
                .filter(landmark -> landmark.isFirstUser(user.getId()))
                .forEach(landmark -> {
                    List<UserAndScore> rankDesc = adventureRepository.findRankDescBy(landmark.getId(), monthStartDateTime, 2);
                    landmark.deleteRank();

                    if (rankDesc.size() > 1) {
                        UserAndScore userAndScore = rankDesc.get(1);
                        landmark.updateFirstUser(userAndScore.user(), userAndScore.score());
                    }
                });
    }
}
