package artistas.controllers;

import org.example.Application;
import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class ArtistaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistaService artistaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreated() throws Exception {
        ArtistaRequestDto dto = ArtistaRequestDto.builder().nombre("Queen").build();
        Artista saved = Artista.builder().id(1L).nombre("Queen").build();

        when(artistaService.save(any(ArtistaRequestDto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Queen"));
    }
}