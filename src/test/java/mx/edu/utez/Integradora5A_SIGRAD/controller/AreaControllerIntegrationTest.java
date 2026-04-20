package mx.edu.utez.Integradora5A_SIGRAD.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.dto.BloqueoDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pruebas de Integración — AreaController")
class AreaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AreaRepository areaRepository;

    @BeforeEach
    void setUp() {
        areaRepository.deleteAll();
    }

    @Test
    @DisplayName("IT-AC-01: POST /api/areas/registrar → 200 OK")
    void registrarArea_retorna200() throws Exception {
        AreaDTO dto = new AreaDTO();
        dto.setNombre("Alberca Olímpica");
        dto.setUbicacion("Edificio Deportivo");
        dto.setHoraApertura("06:00");
        dto.setHoraCierre("22:00");

        mockMvc.perform(post("/api/areas/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Alberca Olímpica"));
    }

    @Test
    @DisplayName("IT-AC-02: POST /api/areas/registrar → error si nombre duplicado")
    void registrarArea_nombreDuplicado_retornaError() throws Exception {
        Area existente = new Area();
        existente.setNombre("Alberca Olímpica");
        existente.setUbicacion("Edificio X");
        existente.setHoraApertura("06:00");
        existente.setHoraCierre("22:00");
        areaRepository.save(existente);

        AreaDTO dto = new AreaDTO();
        dto.setNombre("Alberca Olímpica");
        dto.setUbicacion("Edificio Y");
        dto.setHoraApertura("06:00");
        dto.setHoraCierre("22:00");

        mockMvc.perform(post("/api/areas/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("IT-AC-03: GET /api/areas/listar → 200 OK y retorna lista")
    void listarAreas_retorna200() throws Exception {
        mockMvc.perform(get("/api/areas/listar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("IT-AC-04: PUT /api/areas/bloquear/{id} → 200 OK con fechas válidas")
    void bloquearArea_retorna200() throws Exception {
        Area area = new Area();
        area.setNombre("Cancha Test Bloqueo");
        area.setUbicacion("Edificio Z");
        area.setHoraApertura("07:00");
        area.setHoraCierre("20:00");
        area.setEstado("disponible");
        area = areaRepository.save(area);

        BloqueoDTO bloqueo = new BloqueoDTO();
        bloqueo.setMotivoBloqueo("Reparación de piso");
        bloqueo.setFechaInicioBloqueo(LocalDate.now().plusDays(1).toString());
        bloqueo.setFechaFinBloqueo(LocalDate.now().plusDays(7).toString());

        mockMvc.perform(put("/api/areas/bloquear/" + area.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bloqueo)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IT-AC-05: PUT /api/areas/desbloquear/{id} → 200 OK")
    void desbloquearArea_retorna200() throws Exception {
        Area area = new Area();
        area.setNombre("Cancha Bloqueada");
        area.setUbicacion("Edificio W");
        area.setHoraApertura("07:00");
        area.setHoraCierre("20:00");
        area.setEstado("bloqueada");
        area = areaRepository.save(area);

        mockMvc.perform(put("/api/areas/desbloquear/" + area.getId()))
                .andExpect(status().isOk());
    }
}
