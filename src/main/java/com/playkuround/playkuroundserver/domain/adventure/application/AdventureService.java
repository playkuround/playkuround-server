package com.playkuround.playkuroundserver.domain.adventure.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.dto.MostVisitedInfo;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseMostLandmarkUser;
import com.playkuround.playkuroundserver.domain.adventure.exception.InvalidLandmarkLocationException;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
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
@Transactional
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final LandmarkRepository landmarkRepository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final UserFindDao userFindDao;


    public AdventureSaveDto.Response saveAdventure(String userEmail, AdventureSaveDto.Request dto) {
        User user = userFindDao.findByEmail(userEmail);
        Landmark landmark = landmarkRepository.findById(dto.getLandmarkId())
                .orElseThrow(() -> new LandmarkNotFoundException(dto.getLandmarkId()));
        validateLocation(landmark, dto.getLatitude(), dto.getLongitude());
        adventureRepository.save(new Adventure(user, landmark));

        return findNewBadges(user, landmark);
    }

    private void validateLocation(Landmark landmark, double latitude, double longitude) {
        double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
        // 10미터 초과이면 에러
        if (distance > 10) throw new InvalidLandmarkLocationException();
    }

    private AdventureSaveDto.Response findNewBadges(User user, Landmark requestSaveLandmark) {
        List<Adventure> adventures = adventureRepository.findAllByUser(user);
        AdventureSaveDto.Response ret = new AdventureSaveDto.Response();

        if (badgeRepository.existsByUserAndBadgeType(user, BadgeType.CONQUEROR)) {
            return ret;
        }

        if (adventures.size() == 1) ret.addBadge(BadgeType.ADVENTURE_1);
        else if (adventures.size() == 5) ret.addBadge(BadgeType.ADVENTURE_5);
        else if (adventures.size() == 10) ret.addBadge(BadgeType.ADVENTURE_10);
        else if (adventures.size() == 30) ret.addBadge(BadgeType.ADVENTURE_30);
        else if (adventures.size() == 44) ret.addBadge(BadgeType.CONQUEROR);

        Long saveLandmarkId = requestSaveLandmark.getId();
        if (22 <= saveLandmarkId && saveLandmarkId <= 26) {
            boolean[] isExist = {false, false, false, false, false};
            for (Adventure adventure : adventures) {
                if (22 <= adventure.getId() && adventure.getId() <= 26)
                    isExist[(int) (adventure.getId() - 22)] = true;
            }
            if (isExist[0] && isExist[1] && isExist[2] && isExist[3] && isExist[4]) {
                ret.addBadge(BadgeType.ENGINEER);
            }
        }
        else if (saveLandmarkId == 8 || saveLandmarkId == 28) {
            boolean[] isExist = {false, false};
            for (Adventure adventure : adventures) {
                if (adventure.getId() == 8) isExist[0] = true;
                else if (adventure.getId() == 28) isExist[1] = true;
            }
            if (isExist[0] && isExist[1]) {
                ret.addBadge(BadgeType.ARTIST);
            }
        }
        else if (saveLandmarkId == 15) { // TODO 경제학관이 어디?
            boolean[] isExist = {false};
            for (Adventure adventure : adventures) {
                if (adventure.getId() == 15) isExist[0] = true;
            }
            if (isExist[0]) {
                ret.addBadge(BadgeType.CEO);
            }
        }
        else if (saveLandmarkId == 37 || saveLandmarkId == 38) {
            boolean[] isExist = {false, false};
            for (Adventure adventure : adventures) {
                if (adventure.getId() == 37) isExist[0] = true;
                if (adventure.getId() == 38) isExist[1] = true;
            }
            if (isExist[0] && isExist[1]) {
                ret.addBadge(BadgeType.NATIONAL_PLAYER);
            }
        }
        else if (39 <= saveLandmarkId && saveLandmarkId <= 44) {
            boolean[] isExist = {false, false, false, false, false, false};
            for (Adventure adventure : adventures) {
                if (39 <= adventure.getId() && adventure.getId() <= 44)
                    isExist[(int) (adventure.getId() - 39)] = true;
            }
            if (isExist[0] && isExist[1] && isExist[2] && isExist[3] && isExist[4] && isExist[5]) {
                ret.addBadge(BadgeType.NEIL_ARMSTRONG);
            }
        }
        return ret;
    }

    @Transactional(readOnly = true)
    public List<ResponseFindAdventure> findAdventureByUserEmail(String userEmail) {
        User user = userFindDao.findByEmail(userEmail);

        return adventureRepository.findAllByUser(user).stream()
                .map(ResponseFindAdventure::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
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
                    }
                    else {
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
