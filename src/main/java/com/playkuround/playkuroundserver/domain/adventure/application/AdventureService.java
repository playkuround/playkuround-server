package com.playkuround.playkuroundserver.domain.adventure.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.MostVisitedInfo;
import com.playkuround.playkuroundserver.domain.adventure.dto.RequestSaveAdventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseMostLandmarkUser;
import com.playkuround.playkuroundserver.domain.adventure.exception.InvalidLandmarkLocationException;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
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
    private final UserFindDao userFindDao;

    @Transactional
    public void saveAdventure(String userEmail, RequestSaveAdventure dto) {
        User user = userFindDao.findByEmail(userEmail);

        Landmark landmark = landmarkRepository.findById(dto.getLandmarkId())
                .orElseThrow(() -> new LandmarkNotFoundException(dto.getLandmarkId()));

        validateLocation(landmark, dto.getLatitude(), dto.getLongitude());

        adventureRepository.save(new Adventure(user, landmark));
    }

    private void validateLocation(Landmark landmark, double latitude, double longitude) {
        double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
        // 10미터 초과이면 에러
        if (distance > 10) throw new InvalidLandmarkLocationException();
    }

    public List<ResponseFindAdventure> findAdventureByUserEmail(String userEmail) {
        User user = userFindDao.findByEmail(userEmail);

        return adventureRepository.findAllByUser(user).stream()
                .map(ResponseFindAdventure::of)
                .collect(Collectors.toList());
    }

    public ResponseMostLandmarkUser findMemberMostLandmark(Long landmarkId) {
        /*
         * 해당 랜드마크에 가장 많이 방문한 회원
         * 횟수가 같다면 방문한지 오래된 회원 -> 정책 논의 필요
         */
        Landmark landmark = landmarkRepository.findById(landmarkId)
                .orElseThrow(() -> new LandmarkNotFoundException(landmarkId));

        // TODO 성능 최적화 -> JPQL 사용 (@Query)
        // count[유저 id] = {방문횟수, 최근 방문일}
        Map<Long, MostVisitedInfo> count = new HashMap<>();
        adventureRepository.findAllByLandmark(landmark)
                .forEach(adventure -> {
                    Long userId = adventure.getUser().getId();
                    if (count.containsKey(userId)) {
                        count.get(userId).updateData(adventure.getCreatedAt());
                    } else {
                        count.put(userId, new MostVisitedInfo(userId, adventure.getCreatedAt()));
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
                    .message("해당 장소에 방문한 회원이 없습니다.")
                    .build();
        }
        else {
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
