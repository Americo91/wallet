package astoppello.wallet.service.impl;

import astoppello.wallet.dto.*;
import astoppello.wallet.exception.NotFoundException;
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
    private TransferDto transferDto;

    @BeforeEach
    void setUp() {
        institutionDto = institutionService.save(new InstitutionDto("Bank"));
        accountDto = accountService.save(institutionDto.getId(), new AccountDto("account", AccountDto.AccountTypeEnum.LIQUIDITY, BigDecimal.TEN, AccountDto.CurrencyEnum.EUR));
        categoryDto = categoryService.save(new CategoryDto("Expenses").type(CategoryDto.TypeEnum.EXPENSE));
        labelDto = labelService.save(new LabelDto("label"));
        transactionDto = new TransactionDto()
                .type(TransactionDto.TypeEnum.EXPENSE)
                .description("any")
                .payee("any")
                .amount(BigDecimal.TEN)
                .category(categoryDto.getId())
                .labels(Set.of(labelDto.getId()));
        transferDto = new TransferDto(BigDecimal.TEN);
    }


    @Test
    void save() {
        TransactionDto saved = service.save(accountDto.getId(), transactionDto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAccount()).isEqualTo(accountDto.getId());
        assertThat(saved.getCategory()).isEqualTo(categoryDto.getId());
        assertThat(saved.getLabels()).isNotNull().contains(labelDto.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getType()).isEqualTo(TransactionDto.TypeEnum.EXPENSE);
        assertThat(saved.getDescription()).isEqualTo("any");
        assertThat(saved.getPayee()).isEqualTo("any");
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
        TransactionDto newDto = new TransactionDto().amount(new BigDecimal("100.00"));

        TransactionDto update = service.update(saved.getId(), newDto);
        assertThat(update.getAmount()).isEqualTo(newDto.getAmount());
        assertThat(update.getUpdatedAt()).isAfter(update.getCreatedAt());
    }

    @Test
    void update_accountAndCategory() {
        TransactionDto saved = service.save(accountDto.getId(), transactionDto);
        CategoryDto newCategory = categoryService.save(new CategoryDto().name("newCategory").type(CategoryDto.TypeEnum.EXPENSE));
        AccountDto newAccount = accountService.save(institutionDto.getId(), new AccountDto().currency(AccountDto.CurrencyEnum.EUR).balance(BigDecimal.ZERO).accountType(AccountDto.AccountTypeEnum.LIQUIDITY).name("name"));

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

    @Test
    void transfer() {
        AccountDto toAccount = accountService.save(institutionDto.getId(), new AccountDto("savings", AccountDto.AccountTypeEnum.LIQUIDITY, BigDecimal.ZERO, AccountDto.CurrencyEnum.EUR ));
        CategoryDto transferCategory = categoryService.save(new CategoryDto("Transfer"));

        TransferDto transferDto = new TransferDto()
                .amount(BigDecimal.valueOf(50))
                .description("Monthly savings")
                .payee("self")
                .labels(Set.of(labelDto.getId()));

        List<TransactionDto> result = service.transfer(accountDto.getId(), toAccount.getId(), transferDto);

        assertThat(result).hasSize(2);

        TransactionDto expense = result.get(0);
        assertThat(expense.getId()).isNotNull();
        assertThat(expense.getAccount()).isEqualTo(accountDto.getId());
        assertThat(expense.getType()).isEqualTo(TransactionDto.TypeEnum.EXPENSE);
        assertThat(expense.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(expense.getCategory()).isEqualTo(transferCategory.getId());
        assertThat(expense.getDescription()).isEqualTo("Monthly savings");
        assertThat(expense.getPayee()).isEqualTo("self");
        assertThat(expense.getLabels()).contains(labelDto.getId());
        assertThat(expense.getDate()).isNotNull();
        assertThat(expense.getCreatedAt()).isNotNull();
        assertThat(expense.getUpdatedAt()).isNotNull();

        TransactionDto income = result.get(1);
        assertThat(income.getId()).isNotNull();
        assertThat(income.getAccount()).isEqualTo(toAccount.getId());
        assertThat(income.getType()).isEqualTo(TransactionDto.TypeEnum.INCOME);
        assertThat(income.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(income.getCategory()).isEqualTo(transferCategory.getId());

        // Verify balances updated
        AccountDto updatedFrom = accountService.getByID(accountDto.getId());
        AccountDto updatedTo = accountService.getByID(toAccount.getId());
        assertThat(updatedFrom.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(-40));
        assertThat(updatedTo.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

    @Test
    void transfer_fromAccountNotFound() {
        AccountDto toAccount = accountService.save(institutionDto.getId(), new AccountDto("savings", AccountDto.AccountTypeEnum.LIQUIDITY, BigDecimal.ZERO, AccountDto.CurrencyEnum.EUR ));

        assertThatThrownBy(() -> service.transfer(UUID.randomUUID(), toAccount.getId(), transferDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void transfer_toAccountNotFound() {

        assertThatThrownBy(() -> service.transfer(accountDto.getId(), UUID.randomUUID(), transferDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void transfer_categoryNotFound() {
        AccountDto toAccount = accountService.save(institutionDto.getId(), new AccountDto("savings", AccountDto.AccountTypeEnum.LIQUIDITY, BigDecimal.ZERO, AccountDto.CurrencyEnum.EUR ));

        assertThatThrownBy(() -> service.transfer(accountDto.getId(), toAccount.getId(), transferDto))
                .isInstanceOf(NotFoundException.class);
    }

}
