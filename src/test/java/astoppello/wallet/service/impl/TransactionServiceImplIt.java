package astoppello.wallet.service.impl;

import astoppello.wallet.dto.*;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@ComponentScan(basePackages = {"astoppello.wallet.service.impl", "astoppello.wallet.mapper"})
public class TransactionServiceImplIt {

    @Autowired
    private TransactionService service;
    @Autowired
    private AccountService accountService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private InstitutionService institutionService;
    private InstitutionDto institutionDto;
    private AccountDto accountDto;
    private CategoryDto categoryDto;
    private LabelDto labelDto;
    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        institutionDto = institutionService.save(InstitutionDto.builder()
                .name("Bank")
                .build());
        accountDto = accountService.save(institutionDto.getId(), AccountDto.builder().name("account").balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        categoryDto = categoryService.save(CategoryDto.builder().type(CategoryType.EXPENSE).name("Expenses").build());
        labelDto = labelService.save(LabelDto.builder().name("label").build());
        transactionDto = TransactionDto.builder()
                .type(TransactionType.EXPENSE)
                .description("any")
                .merchant("any")
                .amount(BigDecimal.TEN)
                .category(categoryDto.getId())
                .labels(Set.of(labelDto.getId()))
                .build();
    }


    @Test
    void save() {
        TransactionDto saved = service.save(accountDto.getId(), transactionDto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAccount()).isEqualTo(accountDto.getId());
        assertThat(saved.getCategory()).isEqualTo(categoryDto.getId());
        assertThat(saved.getLabels()).isNotNull().contains(labelDto.getId());
        assertThat(saved.getTrackingDate()).isNotNull();
        assertThat(saved.getTrackingDate().getCreatedAt()).isNotNull();
        assertThat(saved.getTrackingDate().getUpdatedAt()).isNotNull();
        assertThat(saved.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(saved.getDescription()).isEqualTo("any");
        assertThat(saved.getMerchant()).isEqualTo("any");
        assertThat(saved.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(saved.getDate()).isNotNull();
    }

    @Test
    void save_accountNotFound() {
        assertThatThrownBy(() -> service.save(UUID.randomUUID(), transactionDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void save_categoryNotFound() {
        transactionDto.setCategory(UUID.randomUUID());
        assertThatThrownBy(() -> service.save(accountDto.getId(), transactionDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void save_labelNotFound(){
        transactionDto.setLabels(Set.of(UUID.randomUUID()));
        try {
            TransactionDto save = service.save(accountDto.getId(), transactionDto);
            assertThat(save.getLabels()).isEmpty();
        } catch (RuntimeException e) {
            fail();
        }
    }

    @Test
    void getAll() {
        service.save(accountDto.getId(), transactionDto);
        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void getByID() {
        TransactionDto saved = service.save(accountDto.getId(), transactionDto);
        assertThat(service.getByID(saved.getId())).isEqualTo(saved);
    }

    @Test
    void getByID_notFound() {
        assertThatThrownBy(() -> service.getByID(UUID.randomUUID())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_amount() {
        TransactionDto saved = service.save(accountDto.getId(), transactionDto);
        TransactionDto newDto = TransactionDto.builder().amount(new BigDecimal("100.00")).build();

        TransactionDto update = service.update(saved.getId(), newDto);
        assertThat(update.getAmount()).isEqualTo(newDto.getAmount());
        assertThat(update.getTrackingDate()).isNotNull();
        assertThat(update.getTrackingDate().getUpdatedAt()).isAfter(update.getTrackingDate().getCreatedAt());
    }

    @Test
    void update_accountAndCategory() {
        TransactionDto saved = service.save(accountDto.getId(), transactionDto);
        CategoryDto newCategory = categoryService.save(CategoryDto.builder().name("newCategory").type(CategoryType.EXPENSE).build());
        AccountDto newAccount = accountService.save(institutionDto.getId(), AccountDto.builder().currency(Currency.EUR).accountType(AccountTypeEnum.LIQUIDITY).name("name").build());

        transactionDto.setAccount(newAccount.getId());
        transactionDto.setCategory(newCategory.getId());
        TransactionDto updated = service.update(saved.getId(), transactionDto);
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getCategory()).isEqualTo(newCategory.getId());
        assertThat(updated.getAccount()).isEqualTo(newAccount.getId());
    }

    @Test
    void delete() {
        TransactionDto save = service.save(accountDto.getId(), transactionDto);
        service.delete(save.getId());
        assertThat(service.getAll()).hasSize(0);
    }

    @Test
    void getAllByAccount() {
        assertThat(service.getAll()).hasSize(0);
        TransactionDto saved = service.save(accountDto.getId(), transactionDto);
        List<TransactionDto> allByAccount = service.getAllByAccount(accountDto.getId());
        assertThat(allByAccount).hasSize(1);
        assertThat(allByAccount.getFirst().getId()).isEqualTo(saved.getId());
    }

    @Test
    void getAllByAccount_notFound() {
        assertThatThrownBy(() -> service.getAllByAccount(UUID.randomUUID())).isInstanceOf(NotFoundException.class);
    }

}
