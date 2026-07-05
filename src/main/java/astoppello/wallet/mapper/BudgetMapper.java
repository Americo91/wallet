package astoppello.wallet.mapper;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Budget;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.Label;
import astoppello.wallet.dto.BudgetDto;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(uses = {DateMapper.class})
public interface BudgetMapper {

    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "mapCategories")
    @Mapping(target = "accountIds", source = "accounts", qualifiedByName = "mapAccounts")
    @Mapping(target = "labelIds", source = "labels", qualifiedByName = "mapLabels")
    @Mapping(target = "createdAt", source = "trackingDate.createdAt")
    @Mapping(target = "updatedAt", source = "trackingDate.updatedAt")
    BudgetDto toDto(Budget domain);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "trackingDate", ignore = true)
    Budget toDomain(BudgetDto dto);

    @Named("mapCategories")
    default Set<UUID> mapCategories(Set<Category> categories) {
        if (CollectionUtils.isEmpty(categories)) return null;
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapAccounts")
    default Set<UUID> mapAccounts(Set<Account> accounts) {
        if (CollectionUtils.isEmpty(accounts)) return null;
        return accounts.stream().map(Account::getId).collect(Collectors.toSet());
    }

    @Named("mapLabels")
    default Set<UUID> mapLabels(Set<Label> labels) {
        if (CollectionUtils.isEmpty(labels)) return null;
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
