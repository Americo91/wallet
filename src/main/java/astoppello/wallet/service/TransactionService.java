package astoppello.wallet.service;

import astoppello.wallet.dto.TransactionDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Validated
public interface TransactionService {

    List<TransactionDto> getAll();

    TransactionDto getByID(UUID id);

    TransactionDto save(@NotNull UUID accountId, UUID categoryId, Set<UUID> labelIds, TransactionDto dto);

    TransactionDto update(UUID id, UUID accountId, UUID categoryId, Set<UUID> labelIds, TransactionDto dto);

    void delete(UUID id);
}