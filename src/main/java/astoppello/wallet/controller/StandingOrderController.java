package astoppello.wallet.controller;

import astoppello.wallet.api.StandingOrdersApi;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.service.StandingOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class StandingOrderController implements StandingOrdersApi {

    private final StandingOrderService standingOrderService;

    @Override
    public ResponseEntity<StandingOrderDto> createStandingOrder(UUID accountId, StandingOrderDto standingOrderDto) {
        StandingOrderDto save = standingOrderService.save(accountId, standingOrderDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", String.format("/api/v1/standing-orders/%s", save.getId()));
        return new ResponseEntity<>(save, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteStandingOrder(UUID standingOrderId) {
        standingOrderService.delete(standingOrderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<StandingOrderDto> getStandingOrderById(UUID standingOrderId) {
        return new ResponseEntity<>(standingOrderService.getByID(standingOrderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<StandingOrderDto>> getUpcomingStandingOrders(Integer days) {
        return new ResponseEntity<>(standingOrderService.getUpcoming(days), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<StandingOrderDto>> listStandingOrders() {
        return new ResponseEntity<>(standingOrderService.getAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateStandingOrder(UUID standingOrderId, StandingOrderDto standingOrderDto) {
        standingOrderService.update(standingOrderId, standingOrderDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
