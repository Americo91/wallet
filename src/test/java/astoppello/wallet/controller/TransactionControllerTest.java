package astoppello.wallet.controller;

import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.service.TransactionService;
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
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @MockitoBean
    TransactionService service;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private TransactionDto transactionDto;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        transactionDto = TransactionDto.builder()
                .id(UUID.randomUUID())
                .account(accountId)
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(LocalDate.now())
                .category(UUID.randomUUID())
                .description("anyText")
                .merchant("any")
                .labels(Set.of(UUID.randomUUID()))
                .trackingDate(TrackingDateDto.builder()
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build())
                .build();
    }

    @Test
    void getAll() throws Exception {
        given(service.getAll()).willReturn(List.of(transactionDto));

        mockMvc.perform(get(TransactionController.TRANSACTION_BASE_URL + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(transactionDto.getId().toString())))
                .andExpect(jsonPath("$[0].type", is(transactionDto.getType().toString())))
                .andExpect(jsonPath("$[0].amount", is(transactionDto.getAmount().toString())))
                .andExpect(jsonPath("$[0].account", is(transactionDto.getAccount().toString())))
                .andExpect(jsonPath("$[0].date", is(transactionDto.getDate().toString())))
                .andExpect(jsonPath("$[0].category", is(transactionDto.getCategory().toString())))
                .andExpect(jsonPath("$[0].description", is(transactionDto.getDescription())))
                .andExpect(jsonPath("$[0].merchant", is(transactionDto.getMerchant())))
                .andExpect(jsonPath("$[0].labels").isNotEmpty())
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
        then(service).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(service.getByID(any())).willReturn(transactionDto);

        mockMvc.perform(get(TransactionController.TRANSACTION_BASE_URL + "/" + transactionDto.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(transactionDto.getId().toString())))
                .andExpect(jsonPath("$.type", is(transactionDto.getType().toString())));
        then(service).should().getByID(any());
    }

    @Test
    void getAllByAccount() throws Exception {
        given(service.getAllByAccount(any())).willReturn(List.of(transactionDto));

        mockMvc.perform(get(TransactionController.ACCOUNT_TRANSACTION_BASE_URL.replace("{accountId}", accountId.toString()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(transactionDto.getId().toString())))
                .andExpect(jsonPath("$[0].account", is(accountId.toString())));
        then(service).should().getAllByAccount(any());
    }

    @Test
    void handlePost() throws Exception {
        given(service.save(any(), any())).willReturn(transactionDto);

        String body = mapper.writeValueAsString(TransactionDto.builder()
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .category(UUID.randomUUID())
                .date(LocalDate.now())
                .build());

        mockMvc.perform(post(TransactionController.ACCOUNT_TRANSACTION_BASE_URL.replace("{accountId}", accountId.toString()) + "/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        then(service).should().save(any(), any());
    }

    @Test
    void handlePost_nullAccountId() throws Exception {
        String body = mapper.writeValueAsString(TransactionDto.builder()
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(LocalDate.now())
                .build());

        mockMvc.perform(post(TransactionController.ACCOUNT_TRANSACTION_BASE_URL.replace("{accountId}", "null") + "/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
        then(service).shouldHaveNoInteractions();
    }

    @Test
    void handlePut() throws Exception {
        given(service.update(any(), any())).willReturn(transactionDto);

        String body = mapper.writeValueAsString(TransactionDto.builder()
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .category(UUID.randomUUID())
                .date(LocalDate.now())
                .build());

        mockMvc.perform(put(TransactionController.TRANSACTION_BASE_URL + "/" + transactionDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        then(service).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(TransactionController.TRANSACTION_BASE_URL + "/" + transactionDto.getId()))
                .andExpect(status().isNoContent());
        then(service).should().delete(any());
    }
}