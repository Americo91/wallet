package astoppello.wallet.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeneralLoader implements CommandLineRunner {

    private final RecordLoader recordLoader;
    private final StandingOrderLoader standingOrderLoader;

    @Override
    public void run(String... args) throws Exception {
        recordLoader.loadInstitutionAndAccount();
        standingOrderLoader.run();
    }
}
