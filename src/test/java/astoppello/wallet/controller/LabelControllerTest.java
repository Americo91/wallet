package astoppello.wallet.controller;

import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.service.LabelService;
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
@WebMvcTest(LabelController.class)
class LabelControllerTest {

    private static final String BASE_PATH = "/api/v1/labels";

    @MockitoBean
    LabelService labelService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private LabelDto labelDto;

    @BeforeEach
    void setup() {
        labelDto = new LabelDto("Food")
                .id(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now());
    }

    @Test
    void getAll() throws Exception {
        given(labelService.getAll()).willReturn(List.of(labelDto));

        mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(labelDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(labelDto.getName())))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
        then(labelService).should().getAll();
    }

    @Test
    void getById() throws Exception {
        given(labelService.getByID(any())).willReturn(labelDto);

        mockMvc.perform(get(BASE_PATH + "/" + labelDto.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id", is(labelDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(labelDto.getName())));
        then(labelService).should().getByID(any());
    }

    @Test
    void getByName() throws Exception {
        given(labelService.getByName(any())).willReturn(labelDto);

        mockMvc.perform(get(BASE_PATH + "?name=" + labelDto.getName()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(labelDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(labelDto.getName())));
        then(labelService).should().getByName(any());
    }

    @Test
    void handlePost() throws Exception {
        given(labelService.save(any())).willReturn(labelDto);

        String body = objectMapper.writeValueAsString(new LabelDto("Food"));

        mockMvc.perform(post(BASE_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        then(labelService).should().save(any());
    }

    @Test
    void handleUpdate() throws Exception {
        given(labelService.update(any(), any())).willReturn(labelDto);

        String body = objectMapper.writeValueAsString(new LabelDto("Food"));

        mockMvc.perform(put(BASE_PATH + "/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        then(labelService).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
        then(labelService).should().delete(any());
    }
}
