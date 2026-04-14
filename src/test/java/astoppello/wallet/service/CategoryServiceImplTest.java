package astoppello.wallet.service;

import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.CategoryMapper;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    private static final String CATEGORY_NAME = "CategoryName";
    @Mock
    private CategoryMapper mapper;
    @Mock
    private CategoryRepository repository;
    @InjectMocks
    private CategoryServiceImpl service;
    private CategoryDto dto;

    @BeforeEach
    void setUp() {
        dto = CategoryDto.builder()
                .name(CATEGORY_NAME)
                .type(CategoryType.EXPENSE)
                .build();
    }

    private Category buildCategory(UUID id) {
        return Category.builder()
                .id(id)
                .name(CATEGORY_NAME)
                .type(CategoryType.EXPENSE)
                .trackingDate(TrackingDate.now()).build();
    }

    @Test
    void getByID() {
        UUID id = UUID.randomUUID();
        Category category = buildCategory(id);
        dto.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(category));
        when(mapper.toDto(category)).thenReturn(dto);

        CategoryDto result = service.getByID(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(CATEGORY_NAME);
        assertThat(result.getType()).isEqualTo(CategoryType.EXPENSE);

        verify(repository).findById(id);
        verify(mapper).toDto(category);
    }

    @Test
    void getByID_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByID(id)).isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void getAll() {
        Category category = buildCategory(UUID.randomUUID());
        when(repository.findAll()).thenReturn(List.of(category));
        when(mapper.toDto(category)).thenReturn(dto);

        List<CategoryDto> result = service.getAll();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo(CATEGORY_NAME);
        verify(repository).findAll();
        verify(mapper).toDto(category);
    }

    @Test
    void getByName() {
        Category category = buildCategory(UUID.randomUUID());
        when(repository.findByName(CATEGORY_NAME)).thenReturn(List.of(category));
        when(mapper.toDto(category)).thenReturn(dto);

        List<CategoryDto> categoryDtos = service.getByName(CATEGORY_NAME);
        assertThat(categoryDtos).hasSize(1);
        assertThat(categoryDtos.getFirst().getName()).isEqualTo(CATEGORY_NAME);
        verify(repository).findByName(CATEGORY_NAME);
        verify(mapper).toDto(category);
    }

    @Test
    void save() {
        UUID id = UUID.randomUUID();
        Category category = buildCategory(null);
        Category saved = buildCategory(id);

        dto.setId(id);
        when(repository.save(category)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        CategoryDto save = service.save(dto);
        assertThat(save.getId()).isEqualTo(id);
        verify(repository).save(category);
        verify(mapper).toDto(saved);
    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        Category category = buildCategory(UUID.randomUUID());
        CategoryDto dtoUpdated = dto;
        dtoUpdated.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(category));
        when(repository.save(category)).thenReturn(category);
        when(mapper.toDto(category)).thenReturn(dtoUpdated);

        CategoryDto result = service.update(id, dto);
        assertThat(result.getName()).isEqualTo(CATEGORY_NAME);
        assertThat(result.getType()).isEqualTo(CategoryType.EXPENSE);
        verify(repository).findById(id);
        verify(repository).save(category);
        verify(mapper).toDto(category);
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        service.delete(id);
        verify(repository).deleteById(id);
    }
}