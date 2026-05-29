package astoppello.wallet.controller;

import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.service.StandingOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class StandingOrderController {

    public static final String BASE_URL = "/api/v1/standing-orders";
    public static final String ID_PATH = "/api/v1/standing-orders/{standingOrderId}";
    public static final String ACCOUNT_BASE_URL = "/api/v1/accounts/{accountId}/standing-orders";
    public static final String UPCOMING_URL = "/api/v1/standing-orders/upcoming";

    private final StandingOrderService standingOrderService;

    @GetMapping(BASE_URL + "/")
    public ResponseEntity<List<StandingOrderDto>> getAll() {
        return new ResponseEntity<>(standingOrderService.getAll(), HttpStatus.OK);
    }

    @GetMapping(ID_PATH)
    public ResponseEntity<StandingOrderDto> getById(@PathVariable("standingOrderId") UUID id) {
        return new ResponseEntity<>(standingOrderService.getByID(id), HttpStatus.OK);
    }

    @GetMapping(UPCOMING_URL)
    public ResponseEntity<List<StandingOrderDto>> getUpcoming(
            @RequestParam(value = "days", defaultValue = "3") int days) {
        return new ResponseEntity<>(standingOrderService.getUpcoming(days), HttpStatus.OK);
    }

    @PostMapping(ACCOUNT_BASE_URL + "/")
    public ResponseEntity<StandingOrderDto> handlePost(
            @PathVariable("accountId") @NotNull UUID accountId,
            @RequestBody @Valid StandingOrderDto dto) {
        return new ResponseEntity<>(standingOrderService.save(accountId, dto), HttpStatus.CREATED);
    }

    @PutMapping(ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePut(
            @PathVariable("standingOrderId") UUID id,
            @RequestBody @Valid StandingOrderDto dto) {
        standingOrderService.update(id, dto);
    }

    @DeleteMapping(ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("standingOrderId") UUID id) {
        standingOrderService.delete(id);
    }
}
