package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.RequestSaveAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.ResponseMostLandmarkUser;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.domain.dao.UserRepository;
import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final LandmarkRepository landmarkRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveAdventure(String userEmail, RequestSaveAdventure dto) {
        log.info("userEmail={}", userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Landmark landmark = landmarkRepository.findById(dto.getLandmarkId())
                .orElseThrow(() -> new EntityNotFoundException("랜드마크를 찾을 수 없습니다."));

        validateLocation(dto.getLatitude(), dto.getLongitude(), landmark);

        adventureRepository.save(new Adventure(user, landmark));
    }

    private void validateLocation(Double latitude, Double longitude, Landmark landmark) {
        // TODO 랜드마크와 현재 위치에 대한 거리 검증 -> 검증 실패면 에러 발생
    }

    public List<ResponseFindAdventure> findAdventureByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return adventureRepository.findAllByUser(user).stream()
                .map(adventure -> ResponseFindAdventure.builder()
                        .landmarkId(adventure.getLandmark().getId())
                        .createdDateTime(adventure.getCreateAt())
                        .build())
                .collect(Collectors.toList());

    }

    public ResponseMostLandmarkUser findMemberMostLandmark(Long landmarkId) {
        /**
         * 해당 랜드마크에 가장 많이 방문한 회원
         * 횟수가 같다면 방문한지 오래된 회원
         */
        Landmark landmark = landmarkRepository.findById(landmarkId)
                .orElseThrow(() -> new EntityNotFoundException("랜드마크를 찾을 수 없습니다."));

        Map<Long, UserByMost> fre = new HashMap<>();

        adventureRepository.findAllByLandmark(landmarkId)
                .forEach(adventure -> {
                    Long userId = adventure.getUser().getId();
                    if (fre.containsKey(userId)) {
                        UserByMost userByMost = fre.get(userId);
                        LocalDateTime recent = userByMost.recent;
                        if (recent.isBefore(adventure.getCreateAt())) recent = adventure.getCreateAt();

                        fre.put(userId, new UserByMost(userByMost.count + 1, recent));
                    } else fre.put(userId, new UserByMost(1, adventure.getCreateAt()));
                });

        Long userId = 0L;
        int maxCount = 0;
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        Set<Long> keySet = fre.keySet();
        for (Long id : keySet) {
            UserByMost userByMost = fre.get(id);
            if (maxCount < userByMost.count) {
                userId = id;
                maxCount = userByMost.count;
                dateTime = userByMost.recent;
            } else if (maxCount == userByMost.count && dateTime.isBefore(userByMost.recent)) {
                userId = id;
                dateTime = userByMost.recent;
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return ResponseMostLandmarkUser.builder()
                .count(maxCount)
                .nickname(user.getNickname())
                .build();
    }

    private static class UserByMost {
        public Integer count;
        public LocalDateTime recent;

        public UserByMost(Integer count, LocalDateTime recent) {
            this.count = count;
            this.recent = recent;
        }
    }
}
