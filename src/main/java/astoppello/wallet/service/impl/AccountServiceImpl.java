package astoppello.wallet.service.impl;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Institution;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.AccountMapper;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.InstitutionRepository;
import astoppello.wallet.service.AccountService;
import io.micrometer.common.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountMapper mapper;
    private final AccountRepository repository;
    private final InstitutionRepository institutionRepository;

    @Override
    public AccountDto save(UUID institutionId, AccountDto dto) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new NotFoundException(Institution.class, institutionId));

        Account domain = mapper.toDomain(dto);
        domain.setInstitution(institution);
        domain.setTrackingDate(TrackingDate.now());
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public AccountDto update(UUID id, AccountDto dto) {
        Account byId = getById(id);

        if (StringUtils.isNotEmpty(dto.getName())) {
            byId.setName(dto.getName());
        }
        if (dto.getAccountType() != null) {
            byId.setAccountType(dto.getAccountType());
        }
        if (dto.getInstitution() != null) {
            institutionRepository.findById(dto.getInstitution()).ifPresent(byId::setInstitution);
        }
        byId.getTrackingDate().touch();
        return mapper.toDto(repository.save(byId));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<AccountDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public AccountDto getByID(UUID id) {
        return mapper.toDto(getById(id));
    }

    private @NonNull Account getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Account.class, id));
    }

    @Override
    public AccountDto getByName(String name) {
        Account byName = repository.findByName(name).orElseThrow(() -> new NotFoundException(Account.class, name));
        return mapper.toDto(byName);
    }
}
