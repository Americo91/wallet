package astoppello.wallet.bootstrap;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.service.AccountService;
import astoppello.wallet.service.InstitutionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordLoaderTest {

    private static final int EXPECTED_INSTITUTION_COUNT = 10;
    private static final int EXPECTED_ACCOUNT_COUNT = 16;

    @Mock
    private InstitutionService institutionService;
    @Mock
    private AccountService accountService;
    @Mock
    private FileService fileService;

    @InjectMocks
    private RecordLoader recordLoader;

    private final Map<String, UUID> institutionIdByName = new HashMap<>();
    private final Map<String, UUID> accountIdByName = new HashMap<>();

    @Test
    void loadInstitutionAndAccount_createsEachInstitutionOnce() {
        stubInstitutionAndAccountSaves();

        recordLoader.loadInstitutionAndAccount();

        ArgumentCaptor<InstitutionDto> institutionCaptor = ArgumentCaptor.forClass(InstitutionDto.class);
        verify(institutionService, times(EXPECTED_INSTITUTION_COUNT)).save(institutionCaptor.capture());

        Map<String, String> colorByInstitutionName = new HashMap<>();
        institutionCaptor.getAllValues().forEach(dto -> colorByInstitutionName.put(dto.getName(), dto.getColor()));

        assertThat(colorByInstitutionName).containsOnlyKeys(
                "Revolut", "PayPal", "Boursorama", "Cash", "Mediolanum",
                "Joint Account", "Scalable Capital", "Natixis", "Degiro", "Coinbase");

        assertThat(colorByInstitutionName.get("Revolut")).isEqualTo("white");
        assertThat(colorByInstitutionName.get("PayPal")).isEqualTo("blue");
        assertThat(colorByInstitutionName.get("Mediolanum")).isEqualTo("blue");
        assertThat(colorByInstitutionName.get("Boursorama")).isEqualTo("#6a1b9a");
        assertThat(colorByInstitutionName.get("Joint Account")).isEqualTo("#6a1b9a");
        assertThat(colorByInstitutionName.get("Cash")).isNull();
        assertThat(colorByInstitutionName.get("Scalable Capital")).isNull();
        assertThat(colorByInstitutionName.get("Natixis")).isNull();
        assertThat(colorByInstitutionName.get("Degiro")).isNull();
        assertThat(colorByInstitutionName.get("Coinbase")).isNull();
    }

    @Test
    void loadInstitutionAndAccount_savesEachAccountUnderItsOwnInstitution() {
        stubInstitutionAndAccountSaves();

        recordLoader.loadInstitutionAndAccount();

        Map<String, UUID> actualInstitutionIdByAccountName = captureAccountsByInstitutionId();

        Map<String, String> expectedInstitutionByAccountName = new HashMap<>();
        expectedInstitutionByAccountName.put("Revolut", "Revolut");
        expectedInstitutionByAccountName.put("Revolut JPY", "Revolut");
        expectedInstitutionByAccountName.put("PayPal", "PayPal");
        expectedInstitutionByAccountName.put("Boursorama", "Boursorama");
        expectedInstitutionByAccountName.put("Livret A", "Boursorama");
        expectedInstitutionByAccountName.put("Borso+", "Boursorama");
        expectedInstitutionByAccountName.put("LDDS", "Boursorama");
        expectedInstitutionByAccountName.put("Cash", "Cash");
        expectedInstitutionByAccountName.put("Cash Matrimonio", "Cash");
        expectedInstitutionByAccountName.put("Cash Lek", "Cash");
        expectedInstitutionByAccountName.put("Mediolanum", "Mediolanum");
        expectedInstitutionByAccountName.put("Joint Account", "Joint Account");
        expectedInstitutionByAccountName.put("Scalable Capital", "Scalable Capital");
        expectedInstitutionByAccountName.put("Natixis", "Natixis");
        expectedInstitutionByAccountName.put("Degiro", "Degiro");
        expectedInstitutionByAccountName.put("Coinbase", "Coinbase");

        assertThat(actualInstitutionIdByAccountName).containsOnlyKeys(expectedInstitutionByAccountName.keySet());
        expectedInstitutionByAccountName.forEach((accountName, institutionName) ->
                assertThat(actualInstitutionIdByAccountName.get(accountName))
                        .as("institution for account %s", accountName)
                        .isEqualTo(institutionIdByName.get(institutionName)));
    }

    @Test
    void loadInstitutionAndAccount_loadsTransactionsForEachAccountFromItsOwnFile() {
        stubInstitutionAndAccountSaves();

        recordLoader.loadInstitutionAndAccount();

        ArgumentCaptor<UUID> accountIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> filePathCaptor = ArgumentCaptor.forClass(String.class);
        verify(fileService, times(EXPECTED_ACCOUNT_COUNT))
                .loadTransactions(accountIdCaptor.capture(), filePathCaptor.capture());

        Map<UUID, String> filePathByAccountId = IntStream.range(0, accountIdCaptor.getAllValues().size())
                .boxed()
                .collect(Collectors.toMap(accountIdCaptor.getAllValues()::get, filePathCaptor.getAllValues()::get));

        assertThat(filePathByAccountId.get(accountIdByName.get("Revolut"))).isEqualTo("/jsonLoad/Revolut.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Revolut JPY"))).isEqualTo("/jsonLoad/Revolut_jpy.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("PayPal"))).isEqualTo("/jsonLoad/Paypal.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Boursorama"))).isEqualTo("/jsonLoad/Boursorama.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Livret A"))).isEqualTo("/jsonLoad/Livret_A.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Borso+"))).isEqualTo("/jsonLoad/Borso+_.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("LDDS"))).isEqualTo("/jsonLoad/LDDS.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Cash"))).isEqualTo("/jsonLoad/Cash.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Cash Matrimonio"))).isEqualTo("/jsonLoad/Cash_Matrimonio.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Cash Lek"))).isEqualTo("/jsonLoad/Cash_Lek.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Mediolanum"))).isEqualTo("/jsonLoad/Banca_Mediolanum.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Joint Account"))).isEqualTo("/jsonLoad/Joint_account.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Scalable Capital"))).isEqualTo("/jsonLoad/Scalable_Capital.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Natixis"))).isEqualTo("/jsonLoad/Natixis.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Degiro"))).isEqualTo("/jsonLoad/Degiro.json");
        assertThat(filePathByAccountId.get(accountIdByName.get("Coinbase"))).isEqualTo("/jsonLoad/Coinbase.json");
    }

    @Test
    void loadInstitutionAndAccount_setsExpectedAccountAttributes() {
        stubInstitutionAndAccountSaves();

        recordLoader.loadInstitutionAndAccount();

        Map<String, AccountDto> accountsByName = captureAccountDtosByName();

        assertThat(accountsByName.get("Revolut JPY").getCurrency()).isEqualTo(AccountDto.CurrencyEnum.JPY);
        assertThat(accountsByName.get("Revolut JPY").getArchive()).isTrue();
        assertThat(accountsByName.get("Revolut JPY").getExcludeFromStats()).isTrue();

        assertThat(accountsByName.get("Coinbase").getArchive()).isTrue();
        assertThat(accountsByName.get("Natixis").getArchive()).isTrue();
        assertThat(accountsByName.get("Scalable Capital").getArchive()).isTrue();

        assertThat(accountsByName.get("PayPal").getBalance()).isEqualByComparingTo(new BigDecimal("58.67"));
        assertThat(accountsByName.get("Cash Matrimonio").getBalance()).isEqualByComparingTo(new BigDecimal("-643.00"));
        assertThat(accountsByName.get("Cash Lek").getCurrency()).isEqualTo(AccountDto.CurrencyEnum.ALL);

        List<String> savingsAccounts = List.of("Livret A", "Borso+", "LDDS");
        savingsAccounts.forEach(name ->
                assertThat(accountsByName.get(name).getAccountType()).isEqualTo(AccountDto.AccountTypeEnum.SAVINGS));

        List<String> investmentAccounts = List.of("Scalable Capital", "Natixis", "Degiro", "Coinbase");
        investmentAccounts.forEach(name ->
                assertThat(accountsByName.get(name).getAccountType()).isEqualTo(AccountDto.AccountTypeEnum.INVESTMENTS));
    }

    private void stubInstitutionAndAccountSaves() {
        when(institutionService.save(any())).thenAnswer(invocation -> {
            InstitutionDto dto = invocation.getArgument(0);
            UUID id = institutionIdByName.computeIfAbsent(dto.getName(), n -> UUID.randomUUID());
            return dto.id(id);
        });

        when(accountService.save(any(), any())).thenAnswer(invocation -> {
            AccountDto dto = invocation.getArgument(1);
            UUID id = accountIdByName.computeIfAbsent(dto.getName(), n -> UUID.randomUUID());
            return dto.id(id);
        });
    }

    private Map<String, UUID> captureAccountsByInstitutionId() {
        ArgumentCaptor<UUID> institutionIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<AccountDto> accountDtoCaptor = ArgumentCaptor.forClass(AccountDto.class);
        verify(accountService, times(EXPECTED_ACCOUNT_COUNT))
                .save(institutionIdCaptor.capture(), accountDtoCaptor.capture());

        return IntStream.range(0, accountDtoCaptor.getAllValues().size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> accountDtoCaptor.getAllValues().get(i).getName(),
                        i -> institutionIdCaptor.getAllValues().get(i)));
    }

    private Map<String, AccountDto> captureAccountDtosByName() {
        ArgumentCaptor<AccountDto> accountDtoCaptor = ArgumentCaptor.forClass(AccountDto.class);
        verify(accountService, times(EXPECTED_ACCOUNT_COUNT)).save(any(), accountDtoCaptor.capture());
        return accountDtoCaptor.getAllValues().stream()
                .collect(Collectors.toMap(AccountDto::getName, dto -> dto));
    }
}