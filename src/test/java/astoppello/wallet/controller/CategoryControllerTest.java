package astoppello.wallet.controller;

import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockitoBean
    CategoryService categoryService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private CategoryDto categoryDto;

    @BeforeEach
    void setup() {
        categoryDto = CategoryDto.builder()
                .id(UUID.randomUUID())
                .name("Food")
                .type(CategoryType.EXPENSE)
                .trackingDate(TrackingDateDto.builder()
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build())
                .build();
    }

    @Test
    void getAll() throws Exception {
        given(categoryService.getAll()).willReturn(List.of(categoryDto));

        mockMvc.perform(get(CategoryController.CATEGORY_BASE_PATH + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(categoryDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(categoryDto.getName())))
                .andExpect(jsonPath("$[0].type", is(categoryDto.getType().toString())))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
        then(categoryService).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(categoryService.getByID(any())).willReturn(categoryDto);

        mockMvc.perform(get(CategoryController.CATEGORY_BASE_PATH + "/" + categoryDto.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(categoryDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
        then(categoryService).should().getByID(any());
    }

    @Test
    void getByName() throws Exception {
        given(categoryService.getByName(any())).willReturn(List.of(categoryDto));

        mockMvc.perform(get(CategoryController.CATEGORY_BASE_PATH + "?name=" + categoryDto.getName()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(categoryDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(categoryDto.getName())));
        then(categoryService).should().getByName(any());
    }

    @Test
    void handlePost() throws Exception {
        given(categoryService.save(any())).willReturn(categoryDto);

        String body = objectMapper.writeValueAsString(CategoryDto.builder()
                .name(categoryDto.getName())
                .type(CategoryType.EXPENSE)
                .build());

        mockMvc.perform(post(CategoryController.CATEGORY_BASE_PATH + "/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        then(categoryService).should().save(any());
    }

    @Test
    void handlePut() throws Exception {
        given(categoryService.update(any(), any())).willReturn(categoryDto);

        String body = objectMapper.writeValueAsString(CategoryDto.builder()
                .name(categoryDto.getName())
                .type(CategoryType.EXPENSE)
                .build());

        mockMvc.perform(put(CategoryController.CATEGORY_BASE_PATH + "/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        then(categoryService).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(CategoryController.CATEGORY_BASE_PATH + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
        then(categoryService).should().delete(any());
    }
}