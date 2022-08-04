package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.domain.landmark.application.AdventureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/adventures")
public class AdventureController {
    private final AdventureService adventureService;


    @PostMapping
    public void saveAdventure() {

    }

    @GetMapping
    public void getAdventure() {

    }

    @GetMapping("/{adventureId}/most")
    public void findMemberMostAdventure(@PathVariable Long adventureId) {

    }
}
