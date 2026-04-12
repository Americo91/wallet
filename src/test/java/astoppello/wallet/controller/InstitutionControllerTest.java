package astoppello.wallet.controller;

import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.service.InstitutionService;
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
@WebMvcTest(InstitutionController.class)
class InstitutionControllerTest {

    @MockitoBean
    InstitutionService institutionService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private InstitutionDto institutionDto;

    @BeforeEach
    void setUp() {
        institutionDto = InstitutionDto.builder()
                .id(UUID.randomUUID())
                .name("name")
                .color("color")
                .trackingDate(TrackingDateDto.builder()
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build())
                .build();
    }

    @Test
    void getById() throws Exception {
        given(institutionService.getByID(any(UUID.class))).willReturn(institutionDto);

        mockMvc.perform(get(InstitutionController.API_V_1_INSTITUTIONS + "/" + institutionDto.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(institutionDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(institutionDto.getName())))
                .andExpect(jsonPath("$.color", is(institutionDto.getColor())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void getAll() throws Exception {
        given(institutionService.getAll()).willReturn(List.of(institutionDto));

        mockMvc.perform(get(InstitutionController.API_V_1_INSTITUTIONS + "/").accept(MediaType.APPLICATION_JSON.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$[0].id", is(institutionDto.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(institutionDto.getName())))
                .andExpect(jsonPath("$[0].color", is(institutionDto.getColor())))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
    }

    @Test
    void handlePost() throws Exception {
        given(institutionService.save(any())).willReturn(institutionDto);
        String s = objectMapper.writeValueAsString(InstitutionDto.builder().name(institutionDto.getName()).color(institutionDto.getColor()).build());

        mockMvc.perform(post(InstitutionController.API_V_1_INSTITUTIONS + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(s))
                .andExpect(status().isCreated());
    }

    @Test
    void handlePut() throws Exception {
        given(institutionService.getByID(any())).willReturn(institutionDto);

        String s = objectMapper.writeValueAsString(InstitutionDto.builder().name(institutionDto.getName()).color(institutionDto.getColor()).build());
        mockMvc.perform(put(InstitutionController.API_V_1_INSTITUTIONS + "/" + UUID.randomUUID())
                        .content(s)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        then(institutionService).should().update(any(), any());
    }

    @Test
    void handleDelete() throws Exception {
        given(institutionService.getByID(any())).willReturn(institutionDto);
        mockMvc.perform(delete(InstitutionController.API_V_1_INSTITUTIONS + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
        then(institutionService).should().delete(any());

    }
}