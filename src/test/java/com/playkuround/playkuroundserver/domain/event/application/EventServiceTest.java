package com.playkuround.playkuroundserver.domain.event.application;

import com.playkuround.playkuroundserver.domain.event.dao.EventRepository;
import com.playkuround.playkuroundserver.domain.event.domain.Event;
import com.playkuround.playkuroundserver.domain.event.dto.EventSaveDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("event 저장")
    void saveEvent_1() {
        // given
        EventSaveDto eventSaveDto = new EventSaveDto("title", "imageUrl", "description", "referenceUrl", true);

        // when
        eventService.saveEvent(eventSaveDto);

        // then
        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository, times(1)).save(eventArgumentCaptor.capture());
        assertThat(eventArgumentCaptor.getValue())
                .extracting("title", "imageUrl", "description", "referenceUrl", "display")
                .containsExactly(eventSaveDto.title(), eventSaveDto.imageUrl(), eventSaveDto.description(),
                        eventSaveDto.referenceUrl(), eventSaveDto.display());
    }

}