package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.domain.landmark.application.AdventureService;
import com.playkuround.playkuroundserver.domain.landmark.dto.RequestSaveAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.ResponseMostLandmarkUser;
import com.playkuround.playkuroundserver.global.resolver.UserEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/adventures")
public class AdventureApi {
    private final AdventureService adventureService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAdventure(@UserEmail String userEmail, @RequestBody @Valid RequestSaveAdventure dto) {
        adventureService.saveAdventure(userEmail, dto);
    }

    @GetMapping
    public List<ResponseFindAdventure> findAdventureByUserEmail(@UserEmail String userEmail) {
        return adventureService.findAdventureByUserEmail(userEmail);
    }

    @GetMapping("/{landmarkId}/most")
    public ResponseMostLandmarkUser findMemberMostAdventure(@PathVariable Long landmarkId) {
        return adventureService.findMemberMostLandmark(landmarkId);
    }
}