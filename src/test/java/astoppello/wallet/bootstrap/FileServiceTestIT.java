package astoppello.wallet.bootstrap;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@DataJpaTest
@ComponentScan(basePackages = {"astoppello.wallet.service.impl", "astoppello.wallet.bootstrap", "astoppello.wallet.mapper"})
@Import(JacksonAutoConfiguration.class)
public class FileServiceTestIT {

    @Autowired
    private FileService fileService;

    @Autowired
    private AccountService accountService;

    @Test
    void loadRevolut() {
        AccountDto revolut = accountService.getByName("Revolut");
        fileService.loadTransactions(revolut.getId(), "/jsonLoad/Revolut.json");
    }
}
