package astoppello.wallet.repository;

import astoppello.wallet.domain.Category;
import astoppello.wallet.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByName(String name);
    Optional<Category> findByNameAndType(String name, CategoryType type);
}
