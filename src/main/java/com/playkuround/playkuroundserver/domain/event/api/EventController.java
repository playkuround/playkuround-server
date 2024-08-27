package com.playkuround.playkuroundserver.domain.event.api;

import com.playkuround.playkuroundserver.domain.event.api.response.EventResponse;
import com.playkuround.playkuroundserver.domain.event.application.EventService;
import com.playkuround.playkuroundserver.domain.event.domain.Event;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<List<EventResponse>> findDisplayEvents() {
        List<Event> events = eventService.findEvents(true);
        return ApiUtils.success(events.stream()
                .map(EventResponse::from)
                .toList());
    }
}
