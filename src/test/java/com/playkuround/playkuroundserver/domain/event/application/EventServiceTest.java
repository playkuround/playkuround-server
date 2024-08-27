package com.playkuround.playkuroundserver.domain.event.application;

import com.playkuround.playkuroundserver.domain.event.dao.EventRepository;
import com.playkuround.playkuroundserver.domain.event.domain.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("event 조회")
    void findEvents_1(boolean display) {
        // given
        List<Event> events = List.of(
                new Event("title1", "imageUrl1", "description1", "referenceUrl1", true),
                new Event("title2", "imageUrl2", "description2", "referenceUrl2", true)
        );
        when(eventRepository.findByDisplay(display))
                .thenReturn(events);

        // when
        List<Event> result = eventService.findEvents(display);

        // then
        assertThat(result).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(events);
    }

}