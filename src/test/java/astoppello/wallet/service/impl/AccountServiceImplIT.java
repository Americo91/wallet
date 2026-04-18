package astoppello.wallet.service.impl;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.AccountMapperImpl;
import astoppello.wallet.mapper.DateMapper;
import astoppello.wallet.mapper.InstitutionMapperImpl;
import astoppello.wallet.mapper.TrackingMapperImpl;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import astoppello.wallet.service.AccountService;
import astoppello.wallet.service.InstitutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

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
        institutionDto = institutionService.save(InstitutionDto.builder()
                .name("Bank")
                .build());
    }

    private AccountDto buildDto(String name) {
        return AccountDto.builder()
                .name(name)
                .accountType(AccountTypeEnum.LIQUIDITY)
                .balance(BigDecimal.ZERO)
                .currency(Currency.EUR)
                .build();
    }

    @Test
    void save() {
        AccountDto saved = service.save(institutionDto.getId(), buildDto("Checking"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Checking");
        assertThat(saved.getAccountType()).isEqualTo(AccountTypeEnum.LIQUIDITY);
        assertThat(saved.getCurrency()).isEqualTo(Currency.EUR);
        assertThat(saved.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(saved.getTrackingDate()).isNotNull();
        assertThat(saved.getTrackingDate().getCreatedAt()).isNotNull();
        assertThat(saved.getTrackingDate().getUpdatedAt()).isNotNull();
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
                AccountDto.builder().name("Updated").accountType(AccountTypeEnum.SAVINGS).build());

        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getAccountType()).isEqualTo(AccountTypeEnum.SAVINGS);
    }

    @Test
    void update_institution() {
        InstitutionDto save = institutionService.save(InstitutionDto.builder()
                .name("Other Bank")
                .build());
        AccountDto saved = service.save(institutionDto.getId(), buildDto("Checking"));

        AccountDto updated = service.update(saved.getId(),
                AccountDto.builder().institution(save.getId()).build());

        assertThat(updated.getInstitution()).isEqualTo(save.getId());
    }

    @Test
    void delete() {
        AccountDto saved = service.save(institutionDto.getId(), buildDto("Checking"));

        service.delete(saved.getId());

        assertThat(service.getAll()).hasSize(0);
    }
}