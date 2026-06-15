package astoppello.wallet.bootstrap;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import astoppello.wallet.service.AccountService;
import astoppello.wallet.service.InstitutionService;
import org.hibernate.integrator.spi.IntegratorService;
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
        InstitutionDto institutionDto = institutionService.save(InstitutionDto.builder().name("Revolut").color("white").build());
        AccountDto revolut = accountService.save(institutionDto.getId(), AccountDto.builder().name("Revolut").accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.ZERO).currency(Currency.EUR).build());
        fileService.loadTransactions(revolut.getId(), "/jsonLoad/Revolut.json");
    }
}
