package astoppello.wallet.service.impl;

import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.service.LabelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ComponentScan(basePackages = {"astoppello.wallet.service.impl", "astoppello.wallet.mapper"})
class LabelServiceImplIT {

    public static final String LABEL_NAME = "LabelName";
    @Autowired
    private LabelService service;

    private LabelDto labelDto;
    private LabelDto saved;

    @BeforeEach
    void setUp() {
        labelDto = new LabelDto(LABEL_NAME);
        saved = service.save(labelDto);
    }

    @Test
    void getAll() {
        service.save(new LabelDto("anyName"));

        List<LabelDto> all = service.getAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting(LabelDto::getName).containsExactly(LABEL_NAME, "anyName");
    }

    @Test
    void getByID() {
        LabelDto byID = service.getByID(saved.getId());
        assertThat(byID).isNotNull();
        assertThat(byID.getName()).isEqualTo(LABEL_NAME);
        assertThat(byID.getId()).isNotNull();
        assertThat(byID.getCreatedAt()).isNotNull();
        assertThat(byID.getUpdatedAt()).isNotNull();
    }

    @Test
    void getByName() {
        assertThat(service.getByName(LABEL_NAME)).isPresent();
    }

    @Test
    void save() {
        LabelDto saved1 = service.save(labelDto);
        assertThat(saved1.getId()).isNotNull();
    }

    @Test
    void update() {
        String name = "newName";
        LabelDto newName = service.update(saved.getId(), new LabelDto(name));
        assertThat(newName).isNotNull();
        assertThat(newName.getName()).isEqualTo(name);
        assertThat(newName.getUpdatedAt()).isNotEqualTo(saved.getUpdatedAt());
    }

    @Test
    void delete() {
        service.delete(saved.getId());
        assertThat(service.getAll()).hasSize(0);
    }
}
