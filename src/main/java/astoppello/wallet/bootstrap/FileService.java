package astoppello.wallet.bootstrap;

import astoppello.wallet.bootstrap.walletexport.WalletExportDto;
import astoppello.wallet.bootstrap.walletexport.WalletExportJson;
import astoppello.wallet.bootstrap.walletexport.WalletExportMapper;
import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.service.CategoryService;
import astoppello.wallet.service.LabelService;
import astoppello.wallet.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileService {

    private final ObjectMapper mapper;
    private final CategoryService categoryService;
    private final LabelService labelService;
    private final TransactionService transactionService;
    private final WalletExportMapper walletExportMapper;

    @Transactional
    public void loadTransactions(UUID accountId, String filePath) {
        WalletExportDto export = walletExportMapper.toDto(loadFromFile(filePath));
        log.info("Loading {} transactions from {}", export.totalRecords(), filePath);

        Set<String> missingCategories = new HashSet<>();
        Set<String> missingLabels = new HashSet<>();

        Map<CategoryTuple, UUID> categoryCache = new HashMap<>();
        Map<String, UUID> labelCache = new HashMap<>();

        List<TransactionDto> convertedTransaction = new ArrayList<>();

        for (WalletExportDto.RecordDto record : export.records()) {
            Optional<UUID> categoryDtoOptional = resolveCategory(record, missingCategories, categoryCache);
            if (categoryDtoOptional.isEmpty()) {
                continue;
            }

            Set<UUID> labels = null;

            if (CollectionUtils.isNotEmpty(record.labels())) {
                labels = resolveLabels(record, missingLabels, labelCache);
                if (CollectionUtils.isEmpty(labels)) {
                    continue;
                }
            }

            TransactionDto dto = TransactionDto.builder()
                    .type(TransactionType.valueOf(record.type().toUpperCase()))
                    .amount(record.value().abs())
                    .date(LocalDate.parse(record.date().substring(0, 10)))
                    .category(categoryDtoOptional.get())
                    .description(record.note())
                    .payee(record.payee())
                    .labels(CollectionUtils.isEmpty(labels) ? null : labels)
                    .build();

            convertedTransaction.add(dto);
//            transactionService.save(accountId, dto);
        }
        log.info("Loaded {} transactions successfully", convertedTransaction.size());

        log.error("Not converted categories: {}", mapper.writeValueAsString(missingCategories));
        log.error("Not converted labels: {}", mapper.writeValueAsString(missingLabels));
    }

    private Set<UUID> resolveLabels(WalletExportDto.RecordDto record, Set<String> recordsNotConverted, Map<String, UUID> labelCache) {

        Set<UUID> resolvedLabels = new HashSet<>();

        for (String label : record.labels()) {
            if (labelCache.containsKey(label)) {
                resolvedLabels.add(labelCache.get(label));
                continue;
            }
            try {
                LabelDto byName = labelService.getByName(label);
                UUID id = byName.getId();
                labelCache.put(label, id);
                resolvedLabels.add(id);
            } catch (NotFoundException e) {
                recordsNotConverted.addAll(new HashSet<>(record.labels()));
                return Collections.emptySet();
            }
        }
        return resolvedLabels;
    }

    private Optional<UUID> resolveCategory(WalletExportDto.RecordDto record, Set<String> recordsNotConverted, Map<CategoryTuple, UUID> categoryCache) {
        CategoryType categoryType = CategoryType.valueOf(record.type().toUpperCase());
        CategoryTuple categoryTuple = new CategoryTuple(record.category(), categoryType);

        if (categoryCache.containsKey(categoryTuple)) {
            categoryCache.get(categoryTuple);
        }

        Optional<CategoryDto> categoryDtoOptional = categoryService.getByNameAndType(record.category(), categoryType);
        if (categoryDtoOptional.isEmpty()) {
            recordsNotConverted.add(record.category());
            return Optional.empty();
        }
        UUID id = categoryDtoOptional.get().getId();
        categoryCache.put(categoryTuple, id);

        return Optional.of(id);
    }


    private WalletExportJson loadFromFile(String filePath) {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + filePath);
            }
            return mapper.readValue(is, WalletExportJson.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load file: " + filePath, e);
        }
    }

    private record CategoryTuple(String name, CategoryType type) {
    }

}