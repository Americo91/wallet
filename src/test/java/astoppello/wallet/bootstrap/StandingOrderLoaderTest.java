package astoppello.wallet.bootstrap;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.service.AccountService;
import astoppello.wallet.service.CategoryService;
import astoppello.wallet.service.LabelService;
import astoppello.wallet.service.StandingOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StandingOrderLoaderTest {

    private static final int EXPECTED_ORDER_COUNT = 13;

    @Mock
    private AccountService accountService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private StandingOrderService standingOrderService;
    @Mock
    private LabelService labelService;

    @InjectMocks
    private StandingOrderLoader standingOrderLoader;

    private final UUID jointAccountId = UUID.randomUUID();
    private final UUID revolutId = UUID.randomUUID();
    private final UUID boursoramaId = UUID.randomUUID();
    private final UUID scalableId = UUID.randomUUID();

    private final UUID fixedCostLabelId = UUID.randomUUID();
    private final UUID guiltyFreeLabelId = UUID.randomUUID();

    private final Map<String, UUID> categoryIdByKey = new HashMap<>();

    @BeforeEach
    void setUp() {
        mockAccount("Joint Account", jointAccountId);
        mockAccount("Revolut", revolutId);
        mockAccount("Boursorama", boursoramaId);
        mockAccount("Scalable Capital", scalableId);

        when(labelService.getByName("Fixed Cost")).thenReturn(Optional.of(new LabelDto("Fixed Cost").id(fixedCostLabelId)));
        when(labelService.getByName("Guilty Free")).thenReturn(Optional.of(new LabelDto("Guilty Free").id(guiltyFreeLabelId)));

        mockCategory("Streaming Services", CategoryType.EXPENSE);
        mockCategory("Utilities", CategoryType.EXPENSE);
        mockCategory("Internet & Phone", CategoryType.EXPENSE);
        mockCategory("Transfer", CategoryType.INCOME);
        mockCategory("Investments", CategoryType.EXPENSE);
        mockCategory("Rent", CategoryType.EXPENSE);
        mockCategory("Home Insurance", CategoryType.EXPENSE);
        mockCategory("Gaming", CategoryType.EXPENSE);
        mockCategory("Education", CategoryType.EXPENSE);
        mockCategory("Software", CategoryType.EXPENSE);
    }

    @Test
    void run_savesEachStandingOrderUnderItsOwnAccount() {
        standingOrderLoader.run();

        Map<String, StandingOrderDto> dtoByName = captureAllByName();

        Map<String, UUID> expectedAccountByOrderName = new HashMap<>();
        expectedAccountByOrderName.put("Luce", jointAccountId);
        expectedAccountByOrderName.put("Rent", jointAccountId);
        expectedAccountByOrderName.put("Sosh", jointAccountId);
        expectedAccountByOrderName.put("Luko", jointAccountId);
        expectedAccountByOrderName.put("Amazon Prime FR", jointAccountId);
        expectedAccountByOrderName.put("Pokemon", revolutId);
        expectedAccountByOrderName.put("Spotify", revolutId);
        expectedAccountByOrderName.put("Radio Rossonera", revolutId);
        expectedAccountByOrderName.put("Claude", revolutId);
        expectedAccountByOrderName.put("Google One", revolutId);
        expectedAccountByOrderName.put("Red", boursoramaId);
        expectedAccountByOrderName.put("Scalable Out", boursoramaId);
        expectedAccountByOrderName.put("Scalable In", scalableId);

        assertThat(dtoByName).containsOnlyKeys(expectedAccountByOrderName.keySet());

        ArgumentCaptor<UUID> accountIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<StandingOrderDto> dtoCaptor = ArgumentCaptor.forClass(StandingOrderDto.class);
        verify(standingOrderService, times(expectedAccountByOrderName.size()))
                .save(accountIdCaptor.capture(), dtoCaptor.capture());

        Map<String, UUID> actualAccountByOrderName = IntStream.range(0, dtoCaptor.getAllValues().size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> dtoCaptor.getAllValues().get(i).getName(),
                        i -> accountIdCaptor.getAllValues().get(i)));

        assertThat(actualAccountByOrderName).isEqualTo(expectedAccountByOrderName);
    }

    @Test
    void run_resolvesCategoriesAndLabelsForEachStandingOrder() {
        standingOrderLoader.run();

        Map<String, StandingOrderDto> dtoByName = captureAllByName();

        assertThat(dtoByName.get("Luce").getCategory()).isEqualTo(categoryIdByKey.get("Utilities|EXPENSE"));
        assertThat(dtoByName.get("Rent").getCategory()).isEqualTo(categoryIdByKey.get("Rent|EXPENSE"));
        assertThat(dtoByName.get("Sosh").getCategory()).isEqualTo(categoryIdByKey.get("Internet & Phone|EXPENSE"));
        assertThat(dtoByName.get("Luko").getCategory()).isEqualTo(categoryIdByKey.get("Home Insurance|EXPENSE"));
        assertThat(dtoByName.get("Amazon Prime FR").getCategory()).isEqualTo(categoryIdByKey.get("Streaming Services|EXPENSE"));
        assertThat(dtoByName.get("Red").getCategory()).isEqualTo(categoryIdByKey.get("Internet & Phone|EXPENSE"));
        assertThat(dtoByName.get("Scalable Out").getCategory()).isEqualTo(categoryIdByKey.get("Investments|EXPENSE"));
        assertThat(dtoByName.get("Scalable In").getCategory()).isEqualTo(categoryIdByKey.get("Transfer|INCOME"));
        assertThat(dtoByName.get("Pokemon").getCategory()).isEqualTo(categoryIdByKey.get("Gaming|EXPENSE"));
        assertThat(dtoByName.get("Spotify").getCategory()).isEqualTo(categoryIdByKey.get("Streaming Services|EXPENSE"));
        assertThat(dtoByName.get("Claude").getCategory()).isEqualTo(categoryIdByKey.get("Education|EXPENSE"));
        assertThat(dtoByName.get("Google One").getCategory()).isEqualTo(categoryIdByKey.get("Software|EXPENSE"));

        List<String> fixedCostOrders = List.of("Luce", "Rent", "Sosh", "Luko", "Red", "Scalable Out");
        List<String> guiltyFreeOrders = List.of("Amazon Prime FR", "Pokemon", "Spotify", "Radio Rossonera", "Claude", "Google One");

        fixedCostOrders.forEach(name ->
                assertThat(dtoByName.get(name).getLabels()).containsExactly(fixedCostLabelId));
        guiltyFreeOrders.forEach(name ->
                assertThat(dtoByName.get(name).getLabels()).containsExactly(guiltyFreeLabelId));

        assertThat(dtoByName.get("Scalable In").getLabels()).isNullOrEmpty();
    }

    @Test
    void run_leavesCategoryNullWhenNotFound() {
        when(categoryService.getByNameAndType("Rent", CategoryType.EXPENSE)).thenReturn(Optional.empty());

        standingOrderLoader.run();

        Map<String, StandingOrderDto> dtoByName = captureAllByName();

        assertThat(dtoByName.get("Rent").getCategory()).isNull();
    }

    private Map<String, StandingOrderDto> captureAllByName() {
        ArgumentCaptor<StandingOrderDto> dtoCaptor = ArgumentCaptor.forClass(StandingOrderDto.class);
        verify(standingOrderService, times(EXPECTED_ORDER_COUNT)).save(any(), dtoCaptor.capture());
        return dtoCaptor.getAllValues().stream()
                .collect(Collectors.toMap(StandingOrderDto::getName, d -> d, (a, b) -> a));
    }

    private void mockAccount(String name, UUID id) {
        when(accountService.getByName(name)).thenReturn(new AccountDto().id(id));
    }

    private void mockCategory(String name, CategoryType type) {
        UUID id = UUID.randomUUID();
        categoryIdByKey.put(name + "|" + type, id);
        when(categoryService.getByNameAndType(name, type)).thenReturn(Optional.of(new CategoryDto(name).id(id)));
    }
}