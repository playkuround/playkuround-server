package com.playkuround.playkuroundserver.domain.event.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.domain.event.api.request.EventSaveRequest;
import com.playkuround.playkuroundserver.domain.event.dao.EventRepository;
import com.playkuround.playkuroundserver.domain.event.domain.Event;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clear() {
        eventRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("display가 true인 event만 조회된다.")
    void findEvents() throws Exception {
        // given
        List<Event> events = List.of(
                new Event("title1", "imageUrl1", "description1", "referenceUrl1", true),
                new Event("title2", "imageUrl2", "description2", "referenceUrl2", false),
                new Event("title3", "imageUrl2", "description2", "referenceUrl2", true)
        );
        eventRepository.saveAll(events);

        // expected
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.size()").value(2))
                .andExpect(jsonPath("$.response.[*].title").value(
                        containsInAnyOrder(events.get(0).getTitle(), events.get(2).getTitle())))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser(role = Role.ROLE_ADMIN)
    @DisplayName("event 저장 성공")
    void saveEvents() throws Exception {
        // given
        EventSaveRequest eventSaveRequest = new EventSaveRequest("title", "imageUrl", "description", "referenceUrl", true);
        String request = objectMapper.writeValueAsString(eventSaveRequest);

        // expected
        mockMvc.perform(post("/api/admin/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andDo(print());

        List<Event> result = eventRepository.findAll();
        assertThat(result).hasSize(1)
                .extracting("title", "imageUrl", "description", "referenceUrl", "display")
                .containsExactly(
                        Tuple.tuple(eventSaveRequest.getTitle(), eventSaveRequest.getImageUrl(), eventSaveRequest.getDescription(),
                                eventSaveRequest.getReferenceUrl(), eventSaveRequest.isDisplay())
                );
    }
}