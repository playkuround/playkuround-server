package com.playkuround.playkuroundserver.domain.adventure.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class MostVisitedInfo {
    private Integer count;
    private LocalDateTime recent;
    private final Long userId;

    public MostVisitedInfo(Long userId, LocalDateTime recent) {
        this.count = 1;
        this.recent = recent;
        this.userId = userId;
    }

    public void updateData(LocalDateTime updateDateTime) {
        increaseCount(); // 1. 방문 횟수 증가
        updateDateTime(updateDateTime); // 2. 최근 방문일 업데이트
    }

    public boolean isSatisfyUpdate(MostVisitedInfo value) {
        // 방문 횟수가 같다면, 방문한지 오래된 회원으로 업데이트 -> 정책에 맞게 따라가기
        if (this.count < value.getCount()) { // 방문 횟수가 더 많은 회원으로 업데이트
            return true;
        } else return Objects.equals(this.count, value.getCount()) && this.recent.isAfter(value.getRecent());
    }

    private void increaseCount() {
        this.count += 1;
    }

    private void updateDateTime(LocalDateTime updateDateTime) {
        if (this.recent.isBefore(updateDateTime)) {
            this.recent = updateDateTime;
        }
    }
}
