package com.playkuround.playkuroundserver.domain.adventure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ResponseMostVisitedUser {

    List<Top5User> top5Users;

    Integer myVisitedCount;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Top5User {
        private String nickname;
        private Integer count;
        private Long userId;
    }

    public void addUser(VisitedUserDto visitedUserDto) {
        this.top5Users.add(Top5User.builder()
                .nickname(visitedUserDto.getNickname())
                .count(visitedUserDto.getNumber())
                .userId(visitedUserDto.getUserId())
                .build());
    }

    public ResponseMostVisitedUser(Integer myVisitedCount) {
        this.top5Users = new ArrayList<>();
        this.myVisitedCount = myVisitedCount;
    }

    public static ResponseMostVisitedUser of(List<VisitedUserDto> visitedInfoes, Integer myVisitedCount) {
        ResponseMostVisitedUser ret = new ResponseMostVisitedUser(myVisitedCount);
        visitedInfoes.forEach(ret::addUser);
        return ret;
    }
}
