package astoppello.wallet.controller;

import astoppello.wallet.dto.GoalDto;
import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(GoalController.class)
class GoalControllerTest {

    @MockitoBean
    GoalService goalService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private GoalDto goalDto;

    @BeforeEach
    void setup() {
        goalDto = GoalDto.builder()
                .id(UUID.randomUUID())
                .name("Emergency Fund")
                .amount(new BigDecimal("10000.00"))
                .trackingDate(TrackingDateDto.builder()
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build())
                .build();
    }

    @Test
    void getAll() throws Exception {
        given(goalService.getAll()).willReturn(List.of(goalDto));

        mockMvc.perform(get(GoalController.GOAL_BASE_PATH + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(goalDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(goalDto.getName())))
                .andExpect(jsonPath("$[0].amount", is(goalDto.getAmount().doubleValue())))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
        then(goalService).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(goalService.getByID(any())).willReturn(goalDto);

        mockMvc.perform(get(GoalController.GOAL_BASE_PATH + "/" + goalDto.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(goalDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(goalDto.getName())))
                .andExpect(jsonPath("$.amount", is(goalDto.getAmount().doubleValue())));
        then(goalService).should().getByID(any());
    }

    @Test
    void getByName() throws Exception {
        given(goalService.getByName(any())).willReturn(goalDto);

        mockMvc.perform(get(GoalController.GOAL_BASE_PATH + "?name=" + goalDto.getName()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(goalDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(goalDto.getName())));
        then(goalService).should().getByName(any());
    }

    @Test
    void handlePost() throws Exception {
        given(goalService.save(any())).willReturn(goalDto);

        String body = objectMapper.writeValueAsString(GoalDto.builder()
                .name(goalDto.getName())
                .amount(goalDto.getAmount())
                .build());

        mockMvc.perform(post(GoalController.GOAL_BASE_PATH + "/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        then(goalService).should().save(any());
    }

    @Test
    void handleUpdate() throws Exception {
        given(goalService.update(any(), any())).willReturn(goalDto);

        String body = objectMapper.writeValueAsString(GoalDto.builder()
                .name(goalDto.getName())
                .amount(goalDto.getAmount())
                .build());

        mockMvc.perform(put(GoalController.GOAL_BASE_PATH + "/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        then(goalService).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(GoalController.GOAL_BASE_PATH + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
        then(goalService).should().delete(any());
    }

    @Test
    void addSavedAmount() throws Exception {
        mockMvc.perform(post(GoalController.GOAL_BASE_PATH + "/" + UUID.randomUUID() + "/addSavedAmount")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(BigDecimal.TEN)))
                .andExpect(status().isOk());
        then(goalService).should().addSavedAmount(any(), eq(BigDecimal.TEN));
    }

    @Test
    void markAsReached() throws Exception {
        mockMvc.perform(post(GoalController.GOAL_BASE_PATH + "/" + UUID.randomUUID() + "/markAsSolved"))
                .andExpect(status().isNoContent());
        then(goalService).should().markAsReached(any());
    }
}