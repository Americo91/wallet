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
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Degiro").build());
        AccountDto degiro = accountService.save(institutionDto.getId(), AccountDto.builder().accountType(AccountTypeEnum.INVESTMENTS).name("Degiro").balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        fileService.loadTransactions(degiro.getId(), "/jsonLoad/Degiro.json");
    }

    private void loadCoinbase() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Coinbase").build());
        AccountDto coinbase = accountService.save(institutionDto.getId(), AccountDto.builder().accountType(AccountTypeEnum.INVESTMENTS).name("Coinbase").archive(true).balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        fileService.loadTransactions(coinbase.getId(), "/jsonLoad/Coinbase.json");
    }

    private void loadNatixis() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Natixis").build());
        AccountDto natixis = accountService.save(institutionDto.getId(), AccountDto.builder().accountType(AccountTypeEnum.INVESTMENTS).name("Natixis").archive(true).balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        fileService.loadTransactions(natixis.getId(), "/jsonLoad/Natixis.json");
    }

    private void loadScalable() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Scalable Capital").build());
        AccountDto scalable = accountService.save(institutionDto.getId(), AccountDto.builder().accountType(AccountTypeEnum.INVESTMENTS).name("Scalable Capital").archive(true).balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        fileService.loadTransactions(scalable.getId(), "/jsonLoad/Scalable_Capital.json");
    }

    private void loadRevolut() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Revolut").color("white").build());
        AccountDto revolut = accountService.save(institutionDto.getId(), AccountDto.builder().name("Revolut").accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        AccountDto revolutJpy = accountService.save(institutionDto.getId(), AccountDto.builder().name("Revolut JPY").accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(Currency.JPY).archive(true).excludeFromStats(true).build());

        fileService.loadTransactions(revolut.getId(), "/jsonLoad/Revolut.json");
        fileService.loadTransactions(revolutJpy.getId(), "/jsonLoad/Revolut_JPY.json");
    }

    private void loadPayPal() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("PayPal").color("blue").build());
        AccountDto paypal = accountService.save(institutionDto.getId(), AccountDto.builder().name("PayPal").accountType(AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("58.67")).currency(Currency.EUR).build());

        fileService.loadTransactions(paypal.getId(), "/jsonLoad/PayPal.json");
    }

    private void loadCash() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Cash").build());
        AccountDto cash = accountService.save(institutionDto.getId(), AccountDto.builder().name("Cash").accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        fileService.loadTransactions(cash.getId(), "/jsonLoad/Cash.json");

        AccountDto cashMatrimonio = accountService.save(institutionDto.getId(), AccountDto.builder().name("Cash Matrimonio").accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        fileService.loadTransactions(cashMatrimonio.getId(), "/jsonLoad/Cash_Matrimonio.json");

        AccountDto cashLek = accountService.save(institutionDto.getId(), AccountDto.builder().name("Cash Lek").accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(Currency.ALL).build());
        fileService.loadTransactions(cashLek.getId(), "/jsonLoad/Cash_Lek.json");
    }

    private void loadMediolanum() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Mediolanum").color("blue").build());
        AccountDto mediolanum = accountService.save(institutionDto.getId(), AccountDto.builder().name("Mediolanum").accountType(AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("6925.28")).currency(Currency.EUR).build());

        fileService.loadTransactions(mediolanum.getId(), "/jsonLoad/Banca_Mediolanum.json");
    }

    private void loadBousorama() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Boursorama").color("#6a1b9a").build());
        AccountDto boursorama = accountService.save(institutionDto.getId(), AccountDto.builder().name("Boursorama").accountType(AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("13480.62")).currency(Currency.EUR).build());
        fileService.loadTransactions(boursorama.getId(), "/jsonLoad/Boursorama.json");

        AccountDto livretA = accountService.save(institutionDto.getId(), AccountDto.builder().name("Livret A").currency(Currency.EUR).balance(BigDecimal.ZERO).accountType(AccountTypeEnum.SAVINGS).build());
        fileService.loadTransactions(livretA.getId(), "/jsonLoad/Livret_A.json");

        AccountDto borsoPlus = accountService.save(institutionDto.getId(), AccountDto.builder().name("Borso+").currency(Currency.EUR).balance(BigDecimal.ZERO).accountType(AccountTypeEnum.SAVINGS).build());
        fileService.loadTransactions(borsoPlus.getId(), "/jsonLoad/Borso+_.json");

        AccountDto ldds = accountService.save(institutionDto.getId(), AccountDto.builder().name("LDDS").currency(Currency.EUR).balance(BigDecimal.ZERO).accountType(AccountTypeEnum.SAVINGS).build());
        fileService.loadTransactions(ldds.getId(), "/jsonLoad/LDDS.json" );
    }

    private void loadJointAccount() {
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Joint Account").color("#6a1b9a").build());
        AccountDto joint = accountService.save(institutionDto.getId(), AccountDto.builder().name("Joint Account").accountType(AccountTypeEnum.LIQUIDITY).balance(new BigDecimal("34.99")).currency(Currency.EUR).build());
        fileService.loadTransactions(joint.getId(), "/jsonLoad/Joint_account.json");
    }
}
