package astoppello.wallet.repository;

import astoppello.wallet.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LabelRepository extends JpaRepository<Label, UUID> {
}
