package astoppello.wallet.bootstrap;

import astoppello.wallet.bootstrap.walletexport.WalletExportDto;
import astoppello.wallet.bootstrap.walletexport.WalletExportJson;
import astoppello.wallet.bootstrap.walletexport.WalletExportMapper;
import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.service.CategoryService;
import astoppello.wallet.service.LabelService;
import astoppello.wallet.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private ObjectMapper mapper;
    @Mock
    private CategoryService categoryService;
    @Mock
    private LabelService labelService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private WalletExportMapper walletExportMapper;

    @InjectMocks
    private FileService fileService;

    private UUID accountId;
    private UUID categoryId;
    private UUID labelId1;
    private UUID labelId2;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        labelId1 = UUID.randomUUID();
        labelId2 = UUID.randomUUID();
    }

    @Test
    void loadTransactions_expenseWithLabels() {
        WalletExportJson.RecordJson record = expenseRecordJson(
                new BigDecimal("-10.50"), "Restaurant",
                List.of(label("Guilty Free"), label("Lavoro")),
                "Lunch", "Pizza Place"
        );
        WalletExportJson export = exportJson(record);
        WalletExportDto exportDto = exportDto("Revolut", "2026-04-27", List.of(exportRecordDto(new BigDecimal("20.50"), "Restaurant", List.of("Guilty Free", "Work"), "expense", "Lunch", "Pizza Place")));

        when(mapper.readValue(any(InputStream.class), eq(WalletExportJson.class))).thenReturn(export);
        when(walletExportMapper.toDto(export)).thenReturn(exportDto);
        when(categoryService.getByNameAndType("Restaurant", CategoryType.EXPENSE))
                .thenReturn(Optional.of(new CategoryDto("Restaurant").id(categoryId)));
        when(labelService.getByName("Guilty Free"))
                .thenReturn(Optional.of(new LabelDto("Guilty Free").id(labelId1)));
        when(labelService.getByName("Work"))
                .thenReturn(Optional.of(new LabelDto("Work").id(labelId2)));

        fileService.loadTransactions(accountId, "/jsonLoad/test-export.json");

        ArgumentCaptor<TransactionDto> captor = ArgumentCaptor.forClass(TransactionDto.class);
        verify(transactionService).save(eq(accountId), captor.capture());

        TransactionDto saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(TransactionDto.TypeEnum.EXPENSE);
        assertThat(saved.getAmount()).isEqualByComparingTo(new BigDecimal("20.50"));
        assertThat(saved.getDate()).isEqualTo(LocalDate.of(2026, 4, 4));
        assertThat(saved.getCategory()).isEqualTo(categoryId);
        assertThat(saved.getDescription()).isEqualTo("Lunch");
        assertThat(saved.getPayee()).isEqualTo("Pizza Place");
        assertThat(saved.getLabels()).containsExactlyInAnyOrder(labelId1, labelId2);
    }


    @Test
    void loadTransactions_income_nullLabels() {
        WalletExportJson.RecordJson record = incomeRecord(new BigDecimal("180"), "Transfer");
        WalletExportJson export = exportJson(record);
        WalletExportDto exportDto = exportDto("Revolut", "2026-04-27", List.of(exportRecordDto(new BigDecimal("20.50"), "Transfer", null, "income", "Lunch", "Pizza Place")));


        when(mapper.readValue(any(InputStream.class), eq(WalletExportJson.class))).thenReturn(export);
        when(walletExportMapper.toDto(export)).thenReturn(exportDto);
        when(categoryService.getByNameAndType("Transfer", CategoryType.INCOME))
                .thenReturn(Optional.of(new CategoryDto("Transfer").id(categoryId)));

        fileService.loadTransactions(accountId, "/jsonLoad/test-export.json");

        ArgumentCaptor<TransactionDto> captor = ArgumentCaptor.forClass(TransactionDto.class);
        verify(transactionService).save(eq(accountId), captor.capture());

        TransactionDto saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(TransactionDto.TypeEnum.INCOME);
        assertThat(saved.getAmount()).isEqualByComparingTo(new BigDecimal("20.50"));
        assertThat(saved.getLabels()).isNull();
        verifyNoInteractions(labelService);
    }

    @Test
    void loadTransactions_categoryNotFound() {
        WalletExportJson.RecordJson record = incomeRecord(new BigDecimal("180"), "Transfer");
        WalletExportJson export = exportJson(record);
        WalletExportDto exportDto = exportDto("Revolut", "2026-04-27", List.of(exportRecordDto(new BigDecimal("20.50"), "Transfer", null, "income", "Lunch", "Pizza Place")));

        when(mapper.readValue(any(InputStream.class), eq(WalletExportJson.class))).thenReturn(export);
        when(walletExportMapper.toDto(export)).thenReturn(exportDto);
        when(categoryService.getByNameAndType("Transfer", CategoryType.INCOME))
                .thenReturn(Optional.empty());

        fileService.loadTransactions(accountId, "/jsonLoad/test-export.json");

        verifyNoInteractions(transactionService);
        verifyNoInteractions(labelService);
    }

    @Test
    void loadTransactions_resourceNotFound() {
        assertThatThrownBy(() -> fileService.loadTransactions(accountId, "/nonexistent.json"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Resource not found");

        verifyNoInteractions(transactionService, categoryService, labelService);
    }

    // --- helpers ---

    private WalletExportJson exportJson(WalletExportJson.RecordJson... records) {
        return new WalletExportJson ("Revolut", "2026-04-27", records.length, List.of(records));
    }

    private WalletExportJson.RecordJson expenseRecordJson(BigDecimal amount, String category,
                                                          List<WalletExportJson.LabelJson> labels,
                                                          String note, String payee) {
        return new WalletExportJson.RecordJson(
                "acc-1",
                new WalletExportJson.AmountJson("EUR", amount),
                new WalletExportJson.AmountJson("EUR", amount),
                new WalletExportJson.CategoryJson(null, 1001, "cat-1", category),
                "2026-04-24T10:43:51Z", "rec-1", labels, note, payee,
                "cash", "2026-04-24T10:43:35Z", "cleared", "expense",
                "2026-04-24T11:19:09Z"
        );
    }

    private WalletExportJson.RecordJson incomeRecord(BigDecimal amount, String category) {
        return new WalletExportJson.RecordJson(
                "acc-1",
                new WalletExportJson.AmountJson("EUR", amount),
                new WalletExportJson.AmountJson("EUR", amount),
                new WalletExportJson.CategoryJson(null, 20001, "cat-2", category),
                "2026-04-26T14:31:34Z", "rec-2", null, null, null,
                "cash", "2026-04-26T14:31:30Z", "cleared", "income",
                "2026-04-26T14:43:19Z"
        );
    }

    private WalletExportDto exportDto(String name, String date, List<WalletExportDto.RecordDto> recordDtos) {
        return new WalletExportDto(name, date, recordDtos.size(), recordDtos);
    }

    private WalletExportDto.RecordDto exportRecordDto(BigDecimal amount, String category, List<String> labels, String type, String note, String payee) {
        return new WalletExportDto.RecordDto(amount, category, note, payee, type, "2026-04-04", "2026-04-24T10:43:51Z", "2026-04-24T10:43:51Z", labels, "1");
    }

    private WalletExportJson.LabelJson label(String name) {
        return new WalletExportJson.LabelJson(false, "#ff6f00", "lbl-1", name);
    }
}
