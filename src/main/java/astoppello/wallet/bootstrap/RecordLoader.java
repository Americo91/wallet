package astoppello.wallet.bootstrap;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import astoppello.wallet.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class RecordLoader implements CommandLineRunner {

    private final InstitutionService institutionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final LabelService labelService;
    private final TransactionService transactionService;
    private final FileService fileService;

    @Override
    public void run(String... args) throws Exception {
        loadInstitutionAndAccount();
    }

    private void loadInstitutionAndAccount() {
        loadRevolut();
//        loadPayPal();
//        loadBousorama();
//        loadCash();
//        loadMediolanum();
    }

    private void loadRevolut() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Revolut").color("white").build());
        AccountDto revolut = accountService.save(institutionDto.getId(), AccountDto.builder().name("Revolut").accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        AccountDto revolutJpy = accountService.save(institutionDto.getId(), AccountDto.builder().name("Revolut JPY").accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(Currency.JPY).build());

        fileService.loadTransactions(revolut.getId(), "/jsonLoad/Revolut.json");
    }
}
