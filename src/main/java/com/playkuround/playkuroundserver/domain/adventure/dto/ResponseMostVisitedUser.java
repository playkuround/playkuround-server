package com.playkuround.playkuroundserver.domain.adventure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMostVisitedUser {

    List<Top5User> top5Users = new ArrayList<>();

    Me me;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Top5User {
        private String nickname;
        private Integer count;
        private Long userId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Me {
        private Integer count;
        private Integer ranking;
    }

    public void addUser(VisitedUserDto visitedUserDto) {
        this.top5Users.add(Top5User.builder()
                .nickname(visitedUserDto.getNickname())
                .count(visitedUserDto.getNumber())
                .userId(visitedUserDto.getUserId())
                .build());
    }

    public void setMe(Integer count, Integer ranking) {
        this.me = Me.builder()
                .count(count)
                .ranking(ranking)
                .build();
    }


    public static ResponseMostVisitedUser of(List<VisitedUserDto> visitedInfoList, Long userId) {
        ResponseMostVisitedUser ret = new ResponseMostVisitedUser();

        int maxUserNum = visitedInfoList.size();
        if (maxUserNum > 5) maxUserNum = 5;

        for (int ranking = 0; ranking < maxUserNum; ranking++) {
            ret.addUser(visitedInfoList.get(ranking));
        }
        for (int ranking = 0; ranking < visitedInfoList.size(); ranking++) {
            if (visitedInfoList.get(ranking).getUserId().equals(userId)) {
                ret.setMe(visitedInfoList.get(ranking).getNumber(), ranking + 1);
                break;
            }
        }
        return ret;
    }
}


