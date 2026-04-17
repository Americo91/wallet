package astoppello.wallet.controller;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import astoppello.wallet.service.AccountService;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockitoBean
    AccountService accountService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private AccountDto accountDto;

    @BeforeEach
    void setup() {
        accountDto = AccountDto.builder()
                .id(UUID.randomUUID())
                .name("name")
                .currency(Currency.EUR)
                .balance(new BigDecimal("200.00"))
                .accountType(AccountTypeEnum.LIQUIDITY)
                .institution(UUID.randomUUID())
                .trackingDate(TrackingDateDto.builder()
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build())
                .build();
    }


    @Test
    void getAll() throws Exception {
        given(accountService.getAll()).willReturn(List.of(accountDto));

        mockMvc.perform(get(AccountController.ACCOUNT_BASE_URL + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(accountDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(accountDto.getName())))
                .andExpect(jsonPath("$[0].currency", is(accountDto.getCurrency().toString())))
                .andExpect(jsonPath("$[0].balance", is(accountDto.getBalance().toString())))
                .andExpect(jsonPath("$[0].accountType", is(accountDto.getAccountType().toString())))
                .andExpect(jsonPath("$[0].institution", is(accountDto.getInstitution().toString())))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
        then(accountService).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(accountService.getByID(any())).willReturn(accountDto);

        mockMvc.perform(get(AccountController.ACCOUNT_BASE_URL + "/" + accountDto.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(accountDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(accountDto.getName())));
        then(accountService).should().getByID(any());
    }

    @Test
    void getByName() throws Exception {
        given(accountService.getByName(any())).willReturn(accountDto);

        mockMvc.perform(get(AccountController.ACCOUNT_BASE_URL + "?name=" + accountDto.getName()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(accountDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(accountDto.getName())));
        then(accountService).should().getByName(any());
    }

    @Test
    void handlePost() throws Exception {
        given(accountService.save(any(), any())).willReturn(accountDto);
        String s = objectMapper.writeValueAsString(AccountDto.builder()
                .name(accountDto.getName())
                .currency(Currency.EUR)
                .balance(BigDecimal.ZERO)
                .accountType(AccountTypeEnum.LIQUIDITY)
                .build());

        mockMvc.perform(post("/api/v1/institutions/" + UUID.randomUUID() + "/accounts/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(s))
                .andExpect(status().isCreated());
        then(accountService).should().save(any(), any());

    }

    @Test
    void handlePut() throws Exception {
        given(accountService.update(any(), any())).willReturn(accountDto);

        String s = objectMapper.writeValueAsString(AccountDto.builder()
                .name(accountDto.getName())
                .currency(Currency.EUR)
                .balance(BigDecimal.ZERO)
                .accountType(AccountTypeEnum.LIQUIDITY)
                .build());
        mockMvc.perform(put(AccountController.ACCOUNT_BASE_URL + "/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(s))
                .andExpect(status().isNoContent());
        then(accountService).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(AccountController.ACCOUNT_BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
        then(accountService).should().delete(any());
    }
}