package com.playkuround.playkuroundserver.domain.adventure.application;

import com.playkuround.playkuroundserver.IntegrationServiceTest;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.exception.InvalidLandmarkLocationException;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@IntegrationServiceTest
class AdventureServiceTest {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdventureRepository adventureRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private LandmarkRepository landmarkRepository;

    @Autowired
    private AdventureService adventureService;

    @Value("${redis-key}")
    private String redisSetKey;

    @AfterEach
    void clean() {
        badgeRepository.deleteAllInBatch();
        adventureRepository.deleteAllInBatch();
        landmarkRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        redisTemplate.delete(redisSetKey);
    }

    @Test
    @DisplayName("탐험을 하게 되면 total score 증가, adventure 저장, 랜드마크별 최고기록과 유저별 게임 최고기록이 업데이트 된다.")
    void saveAdventure_1() {
        // given
        User user = TestUtil.createUser();

        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);

        Location location = new Location(landmark.getLatitude(), landmark.getLongitude());
        AdventureSaveDto adventureSaveDto = new AdventureSaveDto(user, landmark.getId(), location, 100, ScoreType.BOOK);

        // when
        adventureService.saveAdventure(adventureSaveDto);

        // then
        // Total Score 저장 및 최고 점수 갱신
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1)
                .extracting("highestScore.highestCardScore")
                .containsExactly(adventureSaveDto.score());

        // adventure 저장
        List<Adventure> adventures = adventureRepository.findAll();
        assertThat(adventures).hasSize(1)
                .extracting("score", "scoreType", "user.id", "landmark.id")
                .containsOnly(tuple(adventureSaveDto.score(), adventureSaveDto.scoreType(), users.get(0).getId(), landmark.getId()));

        // 랜드마크 최고 점수 갱신
        Optional<Landmark> optionalLandmark = landmarkRepository.findById(landmark.getId());
        assertThat(optionalLandmark).isPresent()
                .get()
                .extracting("highestScore", "firstUser.id")
                .contains(adventureSaveDto.score(), users.get(0).getId());
    }

    @Test
    @DisplayName("랜드마크가 존재하지 않으면 에러가 발생한다.")
    void saveAdventure_2() {
        // given
        User user = TestUtil.createUser();

        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);

        Location location = new Location(landmark.getLatitude(), landmark.getLongitude());
        AdventureSaveDto adventureSaveDto = new AdventureSaveDto(user, -1L, location, 100, ScoreType.BOOK);

        // expected
        assertThatThrownBy(() -> adventureService.saveAdventure(adventureSaveDto))
                .isInstanceOf(LandmarkNotFoundException.class)
                .hasMessage("-1의 랜드마크 조회에 실패하였습니다.");

        List<Adventure> adventures = adventureRepository.findAll();
        assertThat(adventures).isEmpty();
    }

    @Test
    @DisplayName("인식 거리 밖에 있으면 에러가 발생한다.")
    void saveAdventure_3() {
        // given
        User user = TestUtil.createUser();

        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);

        Location location = new Location(0, 0);
        AdventureSaveDto adventureSaveDto = new AdventureSaveDto(user, landmark.getId(), location, 100, ScoreType.BOOK);

        // when
        assertThatThrownBy(() -> adventureService.saveAdventure(adventureSaveDto))
                .isInstanceOf(InvalidLandmarkLocationException.class)
                .hasMessage("현재 위치와 랜드마크 위치가 너무 멉니다.");

        List<Adventure> adventures = adventureRepository.findAll();
        assertThat(adventures).isEmpty();
    }

    @Test
    @DisplayName("랜드마크 최고 기록자가 아니라면, 랜드마크 랭킹 1위는 업데이트 되지 않는다.")
    void saveAdventure_4() {
        // given
        User user = TestUtil.createUser();

        User otherUser = TestUtil.createUser("other@test.com", "other", Major.건축학부);
        userRepository.saveAll(List.of(user, otherUser));

        long highestScore = 1000;
        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmark.updateFirstUser(otherUser, highestScore);
        landmarkRepository.save(landmark);

        Location location = new Location(landmark.getLatitude(), landmark.getLongitude());
        AdventureSaveDto adventureSaveDto = new AdventureSaveDto(user, landmark.getId(), location, 100, ScoreType.BOOK);

        // when
        adventureService.saveAdventure(adventureSaveDto);

        // then
        Optional<Landmark> optionalLandmark = landmarkRepository.findById(landmark.getId());
        assertThat(optionalLandmark).isPresent()
                .get()
                .extracting("highestScore", "firstUser.id")
                .contains(highestScore, otherUser.getId());
    }

}