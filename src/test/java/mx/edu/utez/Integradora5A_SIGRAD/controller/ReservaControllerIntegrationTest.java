package mx.edu.utez.Integradora5A_SIGRAD.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.edu.utez.Integradora5A_SIGRAD.dto.ReservaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.ReservaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
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
@DisplayName("Pruebas de Integración — ReservaController")
class ReservaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioPrueba;
    private Area areaPrueba;

    @BeforeEach
    void setUp() {
        reservaRepository.deleteAll();
        areaRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuarioPrueba = new Usuario();
        usuarioPrueba.setNombre("Tester Integración");
        usuarioPrueba.setEmailInstitucional("tester@utez.edu.mx");
        usuarioPrueba.setContrasena("test123");
        usuarioPrueba.setRol("ESTUDIANTE");
        usuarioPrueba.setEstado(true);
        usuarioPrueba.setValidado(true);
        usuarioPrueba = usuarioRepository.save(usuarioPrueba);

        areaPrueba = new Area();
        areaPrueba.setNombre("Cancha Integración");
        areaPrueba.setUbicacion("Edificio Test");
        areaPrueba.setHoraApertura("07:00");
        areaPrueba.setHoraCierre("21:00");
        areaPrueba.setEstado("disponible");
        areaPrueba = areaRepository.save(areaPrueba);
    }

    @Test
    @DisplayName("IT-RC-01: POST /api/reservas/crear → 200 OK con datos válidos")
    void crearReserva_retorna200() throws Exception {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdUsuario(usuarioPrueba.getId());
        dto.setIdArea(areaPrueba.getId());
        dto.setFecha(LocalDate.now().plusDays(1).toString());
        dto.setHoraInicio("10:00");
        dto.setHoraFin("12:00");
        dto.setDescripcion("Prueba integración");

        mockMvc.perform(post("/api/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Reserva creada")));
    }

    @Test
    @DisplayName("IT-RC-02: POST /api/reservas/crear → 500 si usuario no existe")
    void crearReserva_usuarioInexistente_retornaError() throws Exception {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdUsuario(99999L);
        dto.setIdArea(areaPrueba.getId());
        dto.setFecha(LocalDate.now().plusDays(1).toString());
        dto.setHoraInicio("10:00");
        dto.setHoraFin("12:00");

        mockMvc.perform(post("/api/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("IT-RC-03: PUT /api/reservas/cancelar/{id} → 200 OK")
    void cancelarReserva_retorna200() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuarioPrueba);
        reserva.setArea(areaPrueba);
        reserva.setFecha(LocalDate.now().plusDays(2).toString());
        reserva.setHoraInicio("10:00");
        reserva.setHoraFin("12:00");
        reserva.setEstado("CONFIRMADA");
        reserva = reservaRepository.save(reserva);

        mockMvc.perform(put("/api/reservas/cancelar/" + reserva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("IT-RC-04: PUT /api/reservas/cancelar/{id} → error si id no existe")
    void cancelarReserva_idInexistente_retornaError() throws Exception {
        mockMvc.perform(put("/api/reservas/cancelar/99999"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("IT-RC-05: GET /api/reservas/listar → 200 OK y retorna lista")
    void listarReservas_retorna200() throws Exception {
        mockMvc.perform(get("/api/reservas/listar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("IT-RC-06: GET /api/reservas/usuario/{id} → 200 OK")
    void listarReservasPorUsuario_retorna200() throws Exception {
        mockMvc.perform(get("/api/reservas/usuario/" + usuarioPrueba.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IT-RC-07: No se pueden crear dos reservas con horario traslapado en el mismo área")
    void crearReserva_conflictoRetornaError() throws Exception {
        Reserva primera = new Reserva();
        primera.setUsuario(usuarioPrueba);
        primera.setArea(areaPrueba);
        primera.setFecha(LocalDate.now().plusDays(1).toString());
        primera.setHoraInicio("09:00");
        primera.setHoraFin("11:00");
        primera.setEstado("CONFIRMADA");
        reservaRepository.save(primera);

        ReservaDTO dto = new ReservaDTO();
        dto.setIdUsuario(usuarioPrueba.getId());
        dto.setIdArea(areaPrueba.getId());
        dto.setFecha(LocalDate.now().plusDays(1).toString());
        dto.setHoraInicio("10:00");
        dto.setHoraFin("12:00");

        mockMvc.perform(post("/api/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }
}
