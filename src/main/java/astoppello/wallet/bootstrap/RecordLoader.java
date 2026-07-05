package astoppello.wallet.bootstrap;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.InstitutionDto;
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
    private final FileService fileService;

    @Override
    public void run(String... args) throws Exception {
        loadInstitutionAndAccount();
    }

    private void loadInstitutionAndAccount() {
        loadRevolut();
        loadPayPal();
        loadBousorama();
        loadCash();
        loadMediolanum();
        loadJointAccount();
        loadScalable();
        loadNatixis();
        loadDegiro();
        loadCoinbase();
    }

    private void loadDegiro() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Degiro"));
        AccountDto degiro = accountService.save(institutionDto.getId(), new AccountDto().accountType(AccountDto.AccountTypeEnum.INVESTMENTS).name("Degiro").balance(BigDecimal.ZERO).currency(AccountDto.CurrencyEnum.EUR));
        fileService.loadTransactions(degiro.getId(), "/jsonLoad/Degiro.json");
    }

    private void loadCoinbase() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Coinbase"));
        AccountDto coinbase = accountService.save(institutionDto.getId(), new AccountDto().accountType(AccountDto.AccountTypeEnum.INVESTMENTS).name("Coinbase").archive(true).balance(BigDecimal.ZERO).currency(AccountDto.CurrencyEnum.EUR));
        fileService.loadTransactions(coinbase.getId(), "/jsonLoad/Coinbase.json");
    }

    private void loadNatixis() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Natixis"));
        AccountDto natixis = accountService.save(institutionDto.getId(), new AccountDto().accountType(AccountDto.AccountTypeEnum.INVESTMENTS).name("Natixis").archive(true).balance(BigDecimal.ZERO).currency(AccountDto.CurrencyEnum.EUR));
        fileService.loadTransactions(natixis.getId(), "/jsonLoad/Natixis.json");
    }

    private void loadScalable() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Scalable Capital"));
        AccountDto scalable = accountService.save(institutionDto.getId(), new AccountDto().accountType(AccountDto.AccountTypeEnum.INVESTMENTS).name("Scalable Capital").archive(true).balance(BigDecimal.ZERO).currency(AccountDto.CurrencyEnum.EUR));
        fileService.loadTransactions(scalable.getId(), "/jsonLoad/Scalable_Capital.json");
    }

    private void loadRevolut() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Revolut").color("white"));
        AccountDto revolut = accountService.save(institutionDto.getId(), new AccountDto().name("Revolut").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(AccountDto.CurrencyEnum.EUR));
        AccountDto revolutJpy = accountService.save(institutionDto.getId(), new AccountDto().name("Revolut JPY").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(AccountDto.CurrencyEnum.JPY).archive(true).excludeFromStats(true));

        fileService.loadTransactions(revolut.getId(), "/jsonLoad/Revolut.json");
        fileService.loadTransactions(revolutJpy.getId(), "/jsonLoad/Revolut_JPY.json");
    }

    private void loadPayPal() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("PayPal").color("blue"));
        AccountDto paypal = accountService.save(institutionDto.getId(), new AccountDto().name("PayPal").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("58.67")).currency(AccountDto.CurrencyEnum.EUR));

        fileService.loadTransactions(paypal.getId(), "/jsonLoad/PayPal.json");
    }

    private void loadCash() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Cash"));
        AccountDto cash = accountService.save(institutionDto.getId(), new AccountDto().name("Cash").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(AccountDto.CurrencyEnum.EUR));
        fileService.loadTransactions(cash.getId(), "/jsonLoad/Cash.json");

        AccountDto cashMatrimonio = accountService.save(institutionDto.getId(), new AccountDto().name("Cash Matrimonio").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("-643.00")).currency(AccountDto.CurrencyEnum.EUR));
        fileService.loadTransactions(cashMatrimonio.getId(), "/jsonLoad/Cash_Matrimonio.json");

        AccountDto cashLek = accountService.save(institutionDto.getId(), new AccountDto().name("Cash Lek").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(AccountDto.CurrencyEnum.ALL));
        fileService.loadTransactions(cashLek.getId(), "/jsonLoad/Cash_Lek.json");
    }

    private void loadMediolanum() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Mediolanum").color("blue"));
        AccountDto mediolanum = accountService.save(institutionDto.getId(), new AccountDto().name("Mediolanum").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("6925.28")).currency(AccountDto.CurrencyEnum.EUR));

        fileService.loadTransactions(mediolanum.getId(), "/jsonLoad/Banca_Mediolanum.json");
    }

    private void loadBousorama() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Boursorama").color("#6a1b9a"));
        AccountDto boursorama = accountService.save(institutionDto.getId(), new AccountDto().name("Boursorama").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("13480.62")).currency(AccountDto.CurrencyEnum.EUR));
        fileService.loadTransactions(boursorama.getId(), "/jsonLoad/Boursorama.json");

        AccountDto livretA = accountService.save(institutionDto.getId(), new AccountDto().name("Livret A").currency(AccountDto.CurrencyEnum.EUR).balance(BigDecimal.ZERO).accountType(AccountDto.AccountTypeEnum.SAVINGS));
        fileService.loadTransactions(livretA.getId(), "/jsonLoad/Livret_A.json");

        AccountDto borsoPlus = accountService.save(institutionDto.getId(), new AccountDto().name("Borso+").currency(AccountDto.CurrencyEnum.EUR).balance(BigDecimal.ZERO).accountType(AccountDto.AccountTypeEnum.SAVINGS));
        fileService.loadTransactions(borsoPlus.getId(), "/jsonLoad/Borso+_.json");

        AccountDto ldds = accountService.save(institutionDto.getId(), new AccountDto().name("LDDS").currency(AccountDto.CurrencyEnum.EUR).balance(BigDecimal.ZERO).accountType(AccountDto.AccountTypeEnum.SAVINGS));
        fileService.loadTransactions(ldds.getId(), "/jsonLoad/LDDS.json" );
    }

    private void loadJointAccount() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto().name("Joint Account").color("#6a1b9a"));
        AccountDto joint = accountService.save(institutionDto.getId(), new AccountDto().name("Joint Account").accountType(AccountDto.AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("34.99")).currency(AccountDto.CurrencyEnum.EUR));
        fileService.loadTransactions(joint.getId(), "/jsonLoad/Joint_account.json");
    }
}
