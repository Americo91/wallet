package astoppello.wallet.service;

import astoppello.wallet.dto.TransactionDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
public interface TransactionService {

    List<TransactionDto> getAll();

    TransactionDto getByID(UUID id);

    TransactionDto save(@NotNull UUID accountId, TransactionDto dto);

    TransactionDto update(UUID id, TransactionDto dto);

    void delete(UUID id);

    List<TransactionDto> getAllByAccount(@NotNull UUID accountId);
}