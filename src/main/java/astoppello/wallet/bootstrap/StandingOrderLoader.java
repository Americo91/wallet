package astoppello.wallet.bootstrap;

import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class StandingOrderLoader {
  private final AccountService accountService;
  private final CategoryService categoryService;
  private final StandingOrderService standingOrderService;
  private final LabelService labelService;

  private UUID fixedCostLabel;
  private UUID guiltyFreeLabel;
  private UUID streamingServicesId;
  private UUID utilityId;
  private UUID internetId;

  @Transactional
  public void run() {
    fixedCostLabel = labelService.getByName("Fixed Cost").map(LabelDto::getId).orElse(null);
    guiltyFreeLabel = labelService.getByName("Guilty Free").map(LabelDto::getId).orElse(null);
    streamingServicesId =
        categoryService
            .getByNameAndType("Streaming Services", CategoryType.EXPENSE)
            .map(CategoryDto::getId)
            .orElse(null);
    utilityId =
        categoryService
            .getByNameAndType("Utilities", CategoryType.EXPENSE)
            .map(CategoryDto::getId)
            .orElse(null);
    internetId =
        categoryService
            .getByNameAndType("Internet & Phone", CategoryType.EXPENSE)
            .map(CategoryDto::getId)
            .orElse(null);

    loadJointAccount();
    loadRevolut();
    loadBoursorama();
    loadScalable();
  }

  private void loadScalable() {
    log.info("Load StandingOrder for Scalable");
    UUID scalable = accountService.getByName("Scalable Capital").getId();
    StandingOrderDto scalableIn =
        new StandingOrderDto()
            .name("Scalable In")
            .amount(new BigDecimal("550.00"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.INCOME)
            .nextOccurrence(LocalDate.of(2026, Month.SEPTEMBER, 7))
            .category(
                categoryService
                    .getByNameAndType("Transfer", CategoryType.INCOME)
                    .map(CategoryDto::getId)
                    .orElse(null))
            .description("Scalable In")
            .payee("Scalable Capital")
            .enabled(true)
            .startingDate(LocalDate.of(2024, Month.DECEMBER, 7));
    standingOrderService.save(scalable, scalableIn);
  }

  private void loadBoursorama() {
    log.info("Load StandingOrder for Boursorama");
    UUID boursorama = accountService.getByName("Boursorama").getId();

    StandingOrderDto red =
        new StandingOrderDto()
            .name("Red")
            .amount(new BigDecimal("14.98"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.AUGUST, 15))
            .category(internetId)
            .addLabelsItem(fixedCostLabel)
            .description(null)
            .payee("Red")
            .enabled(true)
            .startingDate(LocalDate.of(2024, Month.DECEMBER, 15));
    standingOrderService.save(boursorama, red);

    StandingOrderDto scalableOut =
        new StandingOrderDto()
            .name("Scalable Out")
            .amount(new BigDecimal("550.00"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.SEPTEMBER, 7))
            .category(
                categoryService
                    .getByNameAndType("Investments", CategoryType.EXPENSE)
                    .map(CategoryDto::getId)
                    .orElse(null))
            .addLabelsItem(fixedCostLabel)
            .description("Pac Scalable Capital")
            .payee("Scalable Capital")
            .enabled(true)
            .startingDate(LocalDate.of(2023, Month.AUGUST, 7));
    standingOrderService.save(boursorama, scalableOut);
  }

  private void loadJointAccount() {
    log.info("Load StandingOrder for Joint Account");
    UUID jointAccount = accountService.getByName("Joint Account").getId();

    StandingOrderDto luce =
        new StandingOrderDto()
            .name("Luce")
            .amount(new BigDecimal("58.20"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.AUGUST, 13))
            .category(utilityId)
            .addLabelsItem(fixedCostLabel)
            .description(null)
            .payee("EDF")
            .enabled(true)
            .startingDate(LocalDate.of(2024, Month.DECEMBER, 13));
    standingOrderService.save(jointAccount, luce);

    StandingOrderDto rent =
        new StandingOrderDto()
            .name("Rent")
            .amount(new BigDecimal("1310.92"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.SEPTEMBER, 3))
            .category(
                categoryService
                    .getByNameAndType("Rent", CategoryType.EXPENSE)
                    .map(CategoryDto::getId)
                    .orElse(null))
            .addLabelsItem(fixedCostLabel)
            .description("Rent")
            .payee(null)
            .enabled(true)
            .startingDate(LocalDate.of(2025, Month.JANUARY, 3));
    standingOrderService.save(jointAccount, rent);

    StandingOrderDto sosh =
        new StandingOrderDto()
            .name("Sosh")
            .amount(new BigDecimal("20.99"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.SEPTEMBER, 4))
            .category(internetId)
            .addLabelsItem(fixedCostLabel)
            .description(null)
            .payee("Sosh")
            .enabled(true)
            .startingDate(LocalDate.of(2025, Month.DECEMBER, 4));
    standingOrderService.save(jointAccount, sosh);

    StandingOrderDto luko =
        new StandingOrderDto()
            .name("Luko")
            .amount(new BigDecimal("198.96"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.YEARLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.SEPTEMBER, 9))
            .category(
                categoryService
                    .getByNameAndType("Home Insurance", CategoryType.EXPENSE)
                    .map(CategoryDto::getId)
                    .orElse(null))
            .addLabelsItem(fixedCostLabel)
            .description(null)
            .payee("Luko")
            .enabled(true)
            .startingDate(LocalDate.of(2025, Month.SEPTEMBER, 9));
    standingOrderService.save(jointAccount, luko);

    StandingOrderDto amazonPrimeFr =
        new StandingOrderDto()
            .name("Amazon Prime FR")
            .amount(new BigDecimal("6.99"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.YEARLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.SEPTEMBER, 9))
            .category(streamingServicesId)
            .addLabelsItem(guiltyFreeLabel)
            .description("Amazon Prime FR")
            .payee("Amazon")
            .enabled(true)
            .startingDate(LocalDate.of(2026, Month.JANUARY, 9));
    standingOrderService.save(jointAccount, amazonPrimeFr);
  }

  private void loadRevolut() {
    log.info("Load StandingOrder for Revolut");
    UUID revolutAccount = accountService.getByName("Revolut").getId();
    UUID guiltyFree = labelService.getByName("Guilty Free").map(LabelDto::getId).orElse(null);

    StandingOrderDto pokemon =
        new StandingOrderDto()
            .name("Pokemon")
            .amount(new BigDecimal("9.99"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.AUGUST, 13))
            .category(
                categoryService
                    .getByNameAndType("Gaming", CategoryType.EXPENSE)
                    .map(CategoryDto::getId)
                    .orElse(null))
            .addLabelsItem(guiltyFree)
            .description("Pokemon")
            .payee(null)
            .enabled(true)
            .startingDate(LocalDate.of(2025, Month.JANUARY, 13));
    standingOrderService.save(revolutAccount, pokemon);

    StandingOrderDto spotify =
        new StandingOrderDto()
            .name("Spotify")
            .amount(new BigDecimal("3.00"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.AUGUST, 15))
            .category(streamingServicesId)
            .addLabelsItem(guiltyFree)
            .description("Spotify")
            .payee(null)
            .enabled(true)
            .startingDate(LocalDate.of(2021, Month.JUNE, 15));
    standingOrderService.save(revolutAccount, spotify);

    StandingOrderDto radioRossonera =
        new StandingOrderDto()
            .name("Radio Rossonera")
            .amount(new BigDecimal("2.99"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.AUGUST, 24))
            .category(streamingServicesId)
            .addLabelsItem(guiltyFree)
            .description("Radio Rossonera")
            .payee(null)
            .enabled(true)
            .startingDate(LocalDate.of(2021, Month.JULY, 24));
    standingOrderService.save(revolutAccount, radioRossonera);

    StandingOrderDto claude =
        new StandingOrderDto()
            .name("Claude")
            .amount(new BigDecimal("21.60"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2026, Month.SEPTEMBER, 4))
            .category(
                categoryService
                    .getByNameAndType("Education", CategoryType.EXPENSE)
                    .map(CategoryDto::getId)
                    .orElse(null))
            .addLabelsItem(guiltyFree)
            .description("Claude")
            .payee("Anthropic")
            .enabled(true)
            .startingDate(LocalDate.of(2026, Month.JUNE, 4));
    standingOrderService.save(revolutAccount, claude);

    StandingOrderDto googleOne =
        new StandingOrderDto()
            .name("Google One")
            .amount(new BigDecimal("19.99"))
            .currency(StandingOrderDto.CurrencyEnum.EUR)
            .frequency(StandingOrderDto.FrequencyEnum.YEARLY)
            .type(StandingOrderDto.TypeEnum.EXPENSE)
            .nextOccurrence(LocalDate.of(2027, Month.MARCH, 11))
            .category(
                categoryService
                    .getByNameAndType("Software", CategoryType.EXPENSE)
                    .map(CategoryDto::getId)
                    .orElse(null))
            .addLabelsItem(guiltyFree)
            .description("Google One")
            .payee("Google")
            .enabled(true)
            .startingDate(LocalDate.of(2026, Month.MARCH, 11));
    standingOrderService.save(revolutAccount, googleOne);
  }
}
