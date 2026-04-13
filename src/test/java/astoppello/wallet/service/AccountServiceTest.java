package astoppello.wallet.service;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Institution;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.AccountMapper;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.InstitutionRepository;
import astoppello.wallet.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountMapper mapper;

    @Mock
    private AccountRepository repository;

    @Mock
    private InstitutionRepository institutionRepository;

    @InjectMocks
    private AccountServiceImpl service;

    private static final String ACCOUNT_NAME = "Checking";
    private static final String INSTITUTION_NAME = "Bank";
    private AccountDto dto;

    @BeforeEach
    void setUp() {
        dto =  AccountDto.builder()
                .name(ACCOUNT_NAME)
                .accountType(AccountTypeEnum.LIQUIDITY)
                .balance(BigDecimal.ZERO)
                .currency(Currency.EUR)
                .institution(INSTITUTION_NAME)
                .build();
    }

    private Account buildDomain(UUID id) {
        Institution institution = Institution.builder().id(UUID.randomUUID()).name(INSTITUTION_NAME).build();
        return Account.builder()
                .id(id)
                .name(ACCOUNT_NAME)
                .accountType(AccountTypeEnum.LIQUIDITY)
                .balance(BigDecimal.ZERO)
                .currency(Currency.EUR)
                .institution(institution)
                .trackingDate(TrackingDate.now())
                .build();
    }

    @Test
    void save() {
        UUID institutionId = UUID.randomUUID();
        Institution institution = Institution.builder().id(institutionId).name(INSTITUTION_NAME).build();
        Account domain = buildDomain(null);
        Account saved = buildDomain(UUID.randomUUID());
        AccountDto savedDto = AccountDto.builder().id(saved.getId()).name(ACCOUNT_NAME).build();

        when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));
        when(mapper.toDomain(dto)).thenReturn(domain);
        when(repository.save(any())).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(savedDto);

        AccountDto result = service.save(institutionId, dto);

        assertThat(result.getId()).isEqualTo(saved.getId());
        verify(institutionRepository).findById(institutionId);
        verify(repository).save(any());
        verify(mapper).toDto(saved);
    }

    @Test
    void save_institutionNotFound() {
        UUID institutionId = UUID.randomUUID();
        when(institutionRepository.findById(institutionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(institutionId, dto))
                .isInstanceOf(NotFoundException.class);
        verify(institutionRepository).findById(institutionId);
    }

    @Test
    void getAll() {
        Account account = buildDomain(UUID.randomUUID());

        when(repository.findAll()).thenReturn(List.of(account));
        when(mapper.toDto(account)).thenReturn(dto);

        List<AccountDto> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo(ACCOUNT_NAME);
        verify(repository).findAll();
    }

    @Test
    void getByID() {
        UUID id = UUID.randomUUID();
        Account account = buildDomain(id);
        dto.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(account));
        when(mapper.toDto(account)).thenReturn(dto);

        AccountDto result = service.getByID(id);

        assertThat(result.getId()).isEqualTo(id);
        verify(repository).findById(id);
    }

    @Test
    void getByID_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByID(id))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void getByName() {
        Account account = buildDomain(UUID.randomUUID());

        when(repository.findByName(ACCOUNT_NAME)).thenReturn(Optional.of(account));
        when(mapper.toDto(account)).thenReturn(dto);

        AccountDto result = service.getByName(ACCOUNT_NAME);

        assertThat(result.getName()).isEqualTo(ACCOUNT_NAME);
        verify(repository).findByName(ACCOUNT_NAME);
    }

    @Test
    void getByName_notFound() {
        when(repository.findByName("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByName("missing"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_nameAndType() {
        UUID id = UUID.randomUUID();
        Account existing = buildDomain(id);
        AccountDto updateDto = AccountDto.builder().name("Savings").accountType(AccountTypeEnum.SAVINGS).build();
        AccountDto updatedDto = AccountDto.builder().id(id).name("Savings").accountType(AccountTypeEnum.SAVINGS).build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(updatedDto);

        AccountDto result = service.update(id, updateDto);

        assertThat(result.getName()).isEqualTo("Savings");
        assertThat(result.getAccountType()).isEqualTo(AccountTypeEnum.SAVINGS);
        verify(repository).findById(id);
        verify(repository).save(existing);
    }

    @Test
    void update_institution() {
        UUID id = UUID.randomUUID();
        Account existing = buildDomain(id);
        Institution newInstitution = Institution.builder().id(UUID.randomUUID()).name("New Bank").build();
        AccountDto updateDto = AccountDto.builder().institution("New Bank").build();
        AccountDto updatedDto = AccountDto.builder().id(id).institution("New Bank").build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(institutionRepository.findByName("New Bank")).thenReturn(Optional.of(newInstitution));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(updatedDto);

        AccountDto result = service.update(id, updateDto);

        assertThat(result.getInstitution()).isEqualTo("New Bank");
        verify(institutionRepository).findByName("New Bank");
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(repository).deleteById(id);
    }
}