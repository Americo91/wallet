package astoppello.wallet.controller;

import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.service.StandingOrderService;
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
import java.time.LocalDate;
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
@WebMvcTest(StandingOrderController.class)
class StandingOrderControllerTest {

    @MockitoBean
    StandingOrderService service;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private StandingOrderDto standingOrderDto;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        standingOrderDto = StandingOrderDto.builder()
                .id(UUID.randomUUID())
                .name("Spotify")
                .account(accountId)
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.valueOf(3))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .nextOccurrence(LocalDate.now().plusDays(5))
                .category(UUID.randomUUID())
                .note("Spotify subscription")
                .enabled(true)
                .trackingDate(TrackingDateDto.builder()
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build())
                .build();
    }

    @Test
    void getAll() throws Exception {
        given(service.getAll()).willReturn(List.of(standingOrderDto));

        mockMvc.perform(get(StandingOrderController.BASE_URL + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(standingOrderDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(standingOrderDto.getName())))
                .andExpect(jsonPath("$[0].amount", is(standingOrderDto.getAmount().toString())))
                .andExpect(jsonPath("$[0].currency", is(standingOrderDto.getCurrency().toString())))
                .andExpect(jsonPath("$[0].frequency", is(standingOrderDto.getFrequency().toString())))
                .andExpect(jsonPath("$[0].type", is(standingOrderDto.getType().toString())))
                .andExpect(jsonPath("$[0].account", is(standingOrderDto.getAccount().toString())))
                .andExpect(jsonPath("$[0].category", is(standingOrderDto.getCategory().toString())))
                .andExpect(jsonPath("$[0].enabled", is(true)));
        then(service).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(service.getByID(any())).willReturn(standingOrderDto);

        mockMvc.perform(get(StandingOrderController.BASE_URL + "/" + standingOrderDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(standingOrderDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(standingOrderDto.getName())));
        then(service).should().getByID(any());
    }

    @Test
    void getUpcoming() throws Exception {
        given(service.getUpcoming(3)).willReturn(List.of(standingOrderDto));

        mockMvc.perform(get(StandingOrderController.UPCOMING_URL)
                        .param("days", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(standingOrderDto.getName())));
        then(service).should().getUpcoming(3);
    }

    @Test
    void getUpcoming_defaultDays() throws Exception {
        given(service.getUpcoming(3)).willReturn(List.of());

        mockMvc.perform(get(StandingOrderController.UPCOMING_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        then(service).should().getUpcoming(3);
    }

    @Test
    void handlePost() throws Exception {
        given(service.save(any(), any())).willReturn(standingOrderDto);

        String body = mapper.writeValueAsString(StandingOrderDto.builder()
                .name("Spotify")
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.valueOf(3))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .nextOccurrence(LocalDate.now().plusDays(5))
                .category(UUID.randomUUID())
                .enabled(true)
                .build());

        mockMvc.perform(post(StandingOrderController.ACCOUNT_BASE_URL.replace("{accountId}", accountId.toString()) + "/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        then(service).should().save(any(), any());
    }

    @Test
    void handlePut() throws Exception {
        given(service.update(any(), any())).willReturn(standingOrderDto);

        String body = mapper.writeValueAsString(StandingOrderDto.builder()
                .name("Spotify Premium")
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.valueOf(9.99))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .nextOccurrence(LocalDate.now().plusDays(5))
                .category(UUID.randomUUID())
                .enabled(true)
                .build());

        mockMvc.perform(put(StandingOrderController.BASE_URL + "/" + standingOrderDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        then(service).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(StandingOrderController.BASE_URL + "/" + standingOrderDto.getId()))
                .andExpect(status().isNoContent());
        then(service).should().delete(any());
    }
}
