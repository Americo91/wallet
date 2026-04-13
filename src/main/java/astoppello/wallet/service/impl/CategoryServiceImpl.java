package astoppello.wallet.service.impl;

import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.CategoryMapper;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.service.CategoryService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper mapper;
    private final CategoryRepository repository;

    @Override
    public CategoryDto getByID(UUID id) {
        return mapper.toDto(getById(id));
    }

    private Category getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Category.class, id));
    }

    @Override
    public List<CategoryDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public List<CategoryDto> getByName(String name) {
        return repository.findByName(name).stream().map(mapper::toDto).toList();
    }

    @Override
    public CategoryDto save(CategoryDto dto) {
        Category domain = mapper.toDomain(dto);
        if (dto.getParentId() != null) {
            Category parent = getById(dto.getParentId());
            domain.setParent(parent);
            domain.setType(null);
        }
        domain.setTrackingDate(TrackingDate.now());
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public CategoryDto update(UUID id, CategoryDto dto) {
        Category byId = getById(id);
        if (StringUtils.isNotEmpty(dto.getName())) {
            byId.setName(dto.getName());
        }
        byId.getTrackingDate().touch();
        return mapper.toDto(repository.save(byId));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
