package astoppello.wallet.repository;

import astoppello.wallet.domain.StandingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface StandingOrderRepository extends JpaRepository<StandingOrder, UUID> {

    List<StandingOrder> findByEnabledTrueAndNextOccurrenceBetween(Timestamp from, Timestamp to);

    List<StandingOrder> findByEnabledTrue();
}
