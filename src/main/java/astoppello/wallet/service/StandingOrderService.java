package astoppello.wallet.service;

import astoppello.wallet.dto.StandingOrderDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
public interface StandingOrderService {

    List<StandingOrderDto> getAll();

    StandingOrderDto getByID(UUID id);

    StandingOrderDto save(@NotNull UUID accountId, StandingOrderDto dto);

    StandingOrderDto update(UUID id, StandingOrderDto dto);

    void delete(UUID id);

    List<StandingOrderDto> getUpcoming(int days);
}
