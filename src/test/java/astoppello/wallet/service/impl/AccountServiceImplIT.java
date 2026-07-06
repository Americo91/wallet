package astoppello.wallet.service.impl;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.service.AccountService;
import astoppello.wallet.service.InstitutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ComponentScan(basePackages = {"astoppello.wallet.service.impl", "astoppello.wallet.mapper"})
class AccountServiceImplIT {

    @Autowired
    private AccountService service;

    @Autowired
    private InstitutionService institutionService;

    private InstitutionDto institutionDto;

    @BeforeEach
    void setUp() {
        institutionDto = institutionService.save(new InstitutionDto("Bank"));
    }

    private AccountDto buildDto(String name) {
        return new AccountDto(name, AccountDto.AccountTypeEnum.LIQUIDITY, BigDecimal.ZERO, AccountDto.CurrencyEnum.EUR);
    }

    @Test
    void save() {
        AccountDto saved = service.save(institutionDto.getId(), buildDto("Checking"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Checking");
        assertThat(saved.getAccountType()).isEqualTo(AccountDto.AccountTypeEnum.LIQUIDITY);
        assertThat(saved.getCurrency()).isEqualTo(AccountDto.CurrencyEnum.EUR);
        assertThat(saved.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void save_institutionNotFound() {
        assertThatThrownBy(() -> service.save(UUID.randomUUID(), buildDto("Checking")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAll() {
        service.save(institutionDto.getId(), buildDto("Checking"));
        service.save(institutionDto.getId(), buildDto("Savings"));

        List<AccountDto> result = service.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(AccountDto::getName).containsExactlyInAnyOrder("Checking", "Savings");
    }

    @Test
    void getByID() {
        AccountDto saved = service.save(institutionDto.getId(), buildDto("Checking"));

        AccountDto found = service.getByID(saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo("Checking");
    }

    @Test
    void getByID_notFound() {
        assertThatThrownBy(() -> service.getByID(UUID.randomUUID()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByName() {
        service.save(institutionDto.getId(), buildDto("Checking"));

        AccountDto found = service.getByName("Checking");

        assertThat(found.getName()).isEqualTo("Checking");
    }

    @Test
    void getByName_notFound() {
        assertThatThrownBy(() -> service.getByName("missing"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_nameAndType() {
        AccountDto saved = service.save(institutionDto.getId(), buildDto("Checking"));

        AccountDto updated = service.update(saved.getId(),
                new AccountDto().name("Updated").accountType(AccountDto.AccountTypeEnum.SAVINGS));

        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getAccountType()).isEqualTo(AccountDto.AccountTypeEnum.SAVINGS);
    }

    @Test
    void update_institution() {
        InstitutionDto save = institutionService.save(new InstitutionDto("Other Bank"));
        AccountDto saved = service.save(institutionDto.getId(), buildDto("Checking"));

        AccountDto updated = service.update(saved.getId(),
                new AccountDto().institution(save.getId()));

        assertThat(updated.getInstitution()).isEqualTo(save.getId());
    }

    @Test
    void delete() {
        AccountDto saved = service.save(institutionDto.getId(), buildDto("Checking"));

        service.delete(saved.getId());

        assertThat(service.getAll()).hasSize(0);
    }
}
