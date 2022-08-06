package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.MostVisitedInfo;
import com.playkuround.playkuroundserver.domain.landmark.dto.RequestSaveAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.ResponseMostLandmarkUser;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        Landmark landmark = landmarkRepository.findById(dto.getLandmarkId())
                .orElseThrow(() -> new LandmarkNotFoundException(dto.getLandmarkId()));

        validateLocation(landmark, dto.getLatitude(), dto.getLongitude());

        adventureRepository.save(new Adventure(user, landmark));
    }

    private void validateLocation(Landmark landmark, Double latitude, Double longitude) {
        // TODO 랜드마크와 현재 위치에 대한 거리 검증 -> 검증 실패면 에러 발생
        // 검증 실패일 경우, 발생하는 오류 -> LocationValidateException
    }

    public List<ResponseFindAdventure> findAdventureByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        return adventureRepository.findAllByUser(user).stream()
                .map(adventure -> ResponseFindAdventure.builder()
                        .landmarkId(adventure.getLandmark().getId())
                        .visitedDateTime(adventure.getCreateAt())
                        .build())
                .collect(Collectors.toList());
    }

    public ResponseMostLandmarkUser findMemberMostLandmark(Long landmarkId) {
        /*
         * 해당 랜드마크에 가장 많이 방문한 회원
         * 횟수가 같다면 방문한지 오래된 회원 -> 정책 논의 필요
         */
        Landmark landmark = landmarkRepository.findById(landmarkId)
                .orElseThrow(() -> new LandmarkNotFoundException(landmarkId));

        // TODO 성능 최적화
        // count[유저 id] = {방문횟수, 최근 방문일}
        Map<Long, MostVisitedInfo> count = new HashMap<>();
        adventureRepository.findAllByLandmark(landmark)
                .forEach(adventure -> {
                    Long userId = adventure.getUser().getId();
                    if (count.containsKey(userId)) {
                        count.get(userId).updateData(adventure.getCreateAt());
                    } else {
                        count.put(userId, new MostVisitedInfo(userId, adventure.getCreateAt()));
                    }
                });


        MostVisitedInfo res = null;
        for (Long id : count.keySet()) {
            MostVisitedInfo value = count.get(id);
            if (res == null) res = value;
            if (res.isSatisfyUpdate(value)) res = value;
        }

        if (res == null) {
            return ResponseMostLandmarkUser.builder()
                    .count(0)
                    .message("해당 장소에 방문한 회원이 한 명도 없습니다.")
                    .build();
        } else {
            User user = userRepository.findById(res.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

            return ResponseMostLandmarkUser.builder()
                    .count(res.getCount())
                    .nickname(user.getNickname())
                    .userId(user.getId())
                    .build();
        }
    }

}
