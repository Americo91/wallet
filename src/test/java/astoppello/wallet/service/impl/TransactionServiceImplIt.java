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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .build();
    }


    @Test
    void save() {
        TransactionDto saved = service.save(accountDto.getId(), categoryDto.getId(), Set.of(labelDto.getId()), transactionDto);

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
    void save_accountCategoryLabelNotFound() {
        assertThatThrownBy(() -> service.save(UUID.randomUUID(), categoryDto.getId(), null, transactionDto))
                .isInstanceOf(NotFoundException.class);

        assertThatThrownBy(() -> service.save(accountDto.getId(), UUID.randomUUID(), null, transactionDto))
                .isInstanceOf(NotFoundException.class);

        TransactionDto save = service.save(accountDto.getId(), categoryDto.getId(), Collections.singleton(UUID.randomUUID()), transactionDto);
        assertThat(save.getLabels()).isEmpty();
    }

    @Test
    void getAll() {
        TransactionDto saved = service.save(accountDto.getId(), categoryDto.getId(), Set.of(labelDto.getId()), transactionDto);
        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void getByID() {
        TransactionDto saved = service.save(accountDto.getId(), categoryDto.getId(), Set.of(labelDto.getId()), transactionDto);
        assertThat(service.getByID(saved.getId())).isEqualTo(saved);
    }

    @Test
    void getByID_notFound() {
        assertThatThrownBy(() -> service.getByID(UUID.randomUUID())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_amount() {
        TransactionDto saved = service.save(accountDto.getId(), categoryDto.getId(), Set.of(labelDto.getId()), transactionDto);
        TransactionDto newDto = TransactionDto.builder().amount(new BigDecimal("100.00")).build();

        TransactionDto update = service.update(saved.getId(), null, null, null, newDto);
        assertThat(update.getAmount()).isEqualTo(newDto.getAmount());
        assertThat(update.getTrackingDate()).isNotNull();
        assertThat(update.getTrackingDate().getUpdatedAt()).isAfter(update.getTrackingDate().getCreatedAt());
    }

    @Test
    void update_accountAndCategory() {
        TransactionDto saved = service.save(accountDto.getId(), categoryDto.getId(), Set.of(labelDto.getId()), transactionDto);
        CategoryDto newCategory = categoryService.save(CategoryDto.builder().name("newCategory").type(CategoryType.EXPENSE).build());
        AccountDto newAccount = accountService.save(institutionDto.getId(), AccountDto.builder().currency(Currency.EUR).accountType(AccountTypeEnum.LIQUIDITY).name("name").build());

        TransactionDto updated = service.update(saved.getId(), newAccount.getId(), newCategory.getId(), null, transactionDto);
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getCategory()).isEqualTo(newCategory.getId());
        assertThat(updated.getAccount()).isEqualTo(newAccount.getId());
    }

    @Test
    void delete() {
        TransactionDto save = service.save(accountDto.getId(), categoryDto.getId(), null, transactionDto);
        service.delete(save.getId());
        assertThat(service.getAll()).hasSize(0);
    }
}
