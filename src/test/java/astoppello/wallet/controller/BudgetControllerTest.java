package astoppello.wallet.controller;

import astoppello.wallet.dto.BudgetDto;
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

    private static final String BASE_PATH = "/api/v1/budgets";

    @MockitoBean
    BudgetService budgetService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private BudgetDto budgetDto;

    @BeforeEach
    void setup() {
        budgetDto = new BudgetDto("Monthly groceries", BudgetDto.PeriodEnum.MONTHLY,
                new BigDecimal("500.00"), Set.of(UUID.randomUUID()), Set.of(UUID.randomUUID()), Set.of(UUID.randomUUID()))
                .id(UUID.randomUUID())
                .closed(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now());
    }

    @Test
    void getAll() throws Exception {
        given(budgetService.getAll()).willReturn(List.of(budgetDto));

        mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(budgetDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(budgetDto.getName())))
                .andExpect(jsonPath("$[0].period", is(budgetDto.getPeriod().getValue())))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
        then(budgetService).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(budgetService.getByID(any())).willReturn(budgetDto);

        mockMvc.perform(get(BASE_PATH + "/" + budgetDto.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(budgetDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(budgetDto.getName())))
                .andExpect(jsonPath("$.period", is(budgetDto.getPeriod().getValue())));
        then(budgetService).should().getByID(any());
    }

    @Test
    void handlePost() throws Exception {
        given(budgetService.save(any())).willReturn(budgetDto);

        String body = objectMapper.writeValueAsString(new BudgetDto("Monthly groceries", BudgetDto.PeriodEnum.MONTHLY,
                budgetDto.getBudgetLimit(), budgetDto.getCategoryIds(), budgetDto.getAccountIds(), budgetDto.getLabelIds()));

        mockMvc.perform(post(BASE_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        then(budgetService).should().save(any());
    }

    @Test
    void handlePut() throws Exception {
        given(budgetService.update(any(), any())).willReturn(budgetDto);

        String body = objectMapper.writeValueAsString(new BudgetDto("Monthly groceries", BudgetDto.PeriodEnum.MONTHLY,
                budgetDto.getBudgetLimit(), budgetDto.getCategoryIds(), budgetDto.getAccountIds(), budgetDto.getLabelIds()));

        mockMvc.perform(put(BASE_PATH + "/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        then(budgetService).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
        then(budgetService).should().delete(any());
    }
}
