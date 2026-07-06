package astoppello.wallet.controller;

import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.dto.TransferDto;
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

    private static final String TRANSACTION_BASE_URL = "/api/v1/transactions";

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
        transactionDto = new TransactionDto()
                .id(UUID.randomUUID())
                .account(accountId)
                .type(TransactionDto.TypeEnum.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(LocalDate.now())
                .category(UUID.randomUUID())
                .description("anyText")
                .payee("any")
                .labels(Set.of(UUID.randomUUID()))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now());
    }

    @Test
    void getAll() throws Exception {
        given(service.getAll()).willReturn(List.of(transactionDto));

        mockMvc.perform(get(TRANSACTION_BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(transactionDto.getId().toString())))
                .andExpect(jsonPath("$[0].type", is(transactionDto.getType().getValue())))
                .andExpect(jsonPath("$[0].amount", is(transactionDto.getAmount().intValue())))
                .andExpect(jsonPath("$[0].account", is(transactionDto.getAccount().toString())))
                .andExpect(jsonPath("$[0].date", is(transactionDto.getDate().toString())))
                .andExpect(jsonPath("$[0].category", is(transactionDto.getCategory().toString())))
                .andExpect(jsonPath("$[0].description", is(transactionDto.getDescription())))
                .andExpect(jsonPath("$[0].payee", is(transactionDto.getPayee())))
                .andExpect(jsonPath("$[0].labels").isNotEmpty())
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
        then(service).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(service.getByID(any())).willReturn(transactionDto);

        mockMvc.perform(get(TRANSACTION_BASE_URL + "/" + transactionDto.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(transactionDto.getId().toString())))
                .andExpect(jsonPath("$.type", is(transactionDto.getType().getValue())));
        then(service).should().getByID(any());
    }

    @Test
    void getAllByAccount() throws Exception {
        given(service.getAllByAccount(any())).willReturn(List.of(transactionDto));

        mockMvc.perform(get("/api/v1/accounts/" + accountId + "/transactions")
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

        String body = mapper.writeValueAsString(new TransactionDto()
                .type(TransactionDto.TypeEnum.EXPENSE)
                .amount(BigDecimal.TEN)
                .category(UUID.randomUUID())
                .date(LocalDate.now()));

        mockMvc.perform(post("/api/v1/accounts/" + accountId + "/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        then(service).should().save(any(), any());
    }

    @Test
    void handlePost_nullAccountId() throws Exception {
        String body = mapper.writeValueAsString(new TransactionDto()
                .type(TransactionDto.TypeEnum.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(LocalDate.now()));

        mockMvc.perform(post("/api/v1/accounts/null/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
        then(service).shouldHaveNoInteractions();
    }

    @Test
    void handlePut() throws Exception {
        given(service.update(any(), any())).willReturn(transactionDto);

        String body = mapper.writeValueAsString(new TransactionDto()
                .type(TransactionDto.TypeEnum.EXPENSE)
                .amount(BigDecimal.TEN)
                .category(UUID.randomUUID())
                .date(LocalDate.now()));

        mockMvc.perform(put(TRANSACTION_BASE_URL + "/" + transactionDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        then(service).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(TRANSACTION_BASE_URL + "/" + transactionDto.getId()))
                .andExpect(status().isNoContent());
        then(service).should().delete(any());
    }

    @Test
    void handleTransfer() throws Exception {
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        TransactionDto expenseDto = new TransactionDto()
                .id(UUID.randomUUID()).type(TransactionDto.TypeEnum.EXPENSE).amount(BigDecimal.TEN);
        TransactionDto incomeDto = new TransactionDto()
                .id(UUID.randomUUID()).type(TransactionDto.TypeEnum.INCOME).amount(BigDecimal.TEN);

        given(service.transfer(any(), any(), any())).willReturn(List.of(expenseDto, incomeDto));

        String body = mapper.writeValueAsString(new TransferDto(BigDecimal.TEN)
                .date(LocalDate.now()));

        String url = "/api/v1/accounts/" + fromAccountId + "/transfer/" + toAccountId;

        mockMvc.perform(post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].type", is("EXPENSE")))
                .andExpect(jsonPath("$[1].type", is("INCOME")));
        then(service).should().transfer(any(), any(), any());
    }
}
