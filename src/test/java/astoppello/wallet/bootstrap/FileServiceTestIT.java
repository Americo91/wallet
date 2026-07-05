package astoppello.wallet.bootstrap;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.AccountDto.AccountTypeEnum;
import astoppello.wallet.dto.AccountDto.CurrencyEnum;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.service.AccountService;
import astoppello.wallet.service.InstitutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

@DataJpaTest
@ComponentScan(basePackages = {"astoppello.wallet.service.impl", "astoppello.wallet.bootstrap", "astoppello.wallet.mapper"})
@Import(JacksonAutoConfiguration.class)
public class FileServiceTestIT {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private FileService fileService;

    @Autowired
    private AccountService accountService;

    @Test
    void loadRevolut() {
        InstitutionDto institutionDto = institutionService.save(new InstitutionDto("Revolut").color("white"));
        AccountDto revolut = accountService.save(institutionDto.getId(), new AccountDto("Revolut", AccountTypeEnum.LIQUIDITY, BigDecimal.ZERO, CurrencyEnum.EUR));
        fileService.loadTransactions(revolut.getId(), "/jsonLoad/Revolut.json");
    }
}
