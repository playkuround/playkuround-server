package com.playkuround.playkuroundserver.domain.adventure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFindAdventure {

    private List<Long> landmarkIdList;

    public static ResponseFindAdventure of(List<Long> landmarkId) {
        return new ResponseFindAdventure(landmarkId);
    }
}
