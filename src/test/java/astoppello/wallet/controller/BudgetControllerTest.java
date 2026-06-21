package astoppello.wallet.controller;

import astoppello.wallet.dto.BudgetDto;
import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.service.BudgetService;
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
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BudgetController.class)
class BudgetControllerTest {

    @MockitoBean
    BudgetService budgetService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private BudgetDto budgetDto;

    @BeforeEach
    void setup() {
        budgetDto = BudgetDto.builder()
                .id(UUID.randomUUID())
                .name("Monthly groceries")
                .period(Frequency.MONTHLY)
                .budgetLimit(new BigDecimal("500.00"))
                .categoryIds(Set.of(UUID.randomUUID()))
                .accountIds(Set.of(UUID.randomUUID()))
                .labelIds(Set.of(UUID.randomUUID()))
                .closed(false)
                .trackingDate(TrackingDateDto.builder()
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build())
                .build();
    }

    @Test
    void getAll() throws Exception {
        given(budgetService.getAll()).willReturn(List.of(budgetDto));

        mockMvc.perform(get(BudgetController.BUDGET_BASE_PATH + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(budgetDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(budgetDto.getName())))
                .andExpect(jsonPath("$[0].period", is(budgetDto.getPeriod().name())))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
        then(budgetService).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(budgetService.getByID(any())).willReturn(budgetDto);

        mockMvc.perform(get(BudgetController.BUDGET_BASE_PATH + "/" + budgetDto.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(budgetDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(budgetDto.getName())))
                .andExpect(jsonPath("$.period", is(budgetDto.getPeriod().name())));
        then(budgetService).should().getByID(any());
    }

    @Test
    void handlePost() throws Exception {
        given(budgetService.save(any())).willReturn(budgetDto);

        String body = objectMapper.writeValueAsString(BudgetDto.builder()
                .name(budgetDto.getName())
                .period(budgetDto.getPeriod())
                .budgetLimit(budgetDto.getBudgetLimit())
                .categoryIds(budgetDto.getCategoryIds())
                .accountIds(budgetDto.getAccountIds())
                .labelIds(budgetDto.getLabelIds())
                .build());

        mockMvc.perform(post(BudgetController.BUDGET_BASE_PATH + "/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        then(budgetService).should().save(any());
    }

    @Test
    void handlePut() throws Exception {
        given(budgetService.update(any(), any())).willReturn(budgetDto);

        String body = objectMapper.writeValueAsString(BudgetDto.builder()
                .name(budgetDto.getName())
                .period(budgetDto.getPeriod())
                .budgetLimit(budgetDto.getBudgetLimit())
                .categoryIds(budgetDto.getCategoryIds())
                .accountIds(budgetDto.getAccountIds())
                .labelIds(budgetDto.getLabelIds())
                .build());

        mockMvc.perform(put(BudgetController.BUDGET_BASE_PATH + "/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        then(budgetService).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(BudgetController.BUDGET_BASE_PATH + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
        then(budgetService).should().delete(any());
    }
}