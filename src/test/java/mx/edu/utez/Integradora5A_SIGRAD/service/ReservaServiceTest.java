package mx.edu.utez.Integradora5A_SIGRAD.service;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias — ReservaService")
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuarioMock;
    private Area areaMock;
    private ReservaDTO dtoValido;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombre("Juan Pérez");
        usuarioMock.setEmailInstitucional("juan@utez.edu.mx");

        areaMock = new Area();
        areaMock.setId(1L);
        areaMock.setNombre("Cancha de Fútbol");
        areaMock.setEstado("disponible");
        areaMock.setHoraApertura("07:00");
        areaMock.setHoraCierre("21:00");

        // Fecha futura para evitar que falle la validación de hora pasada
        String fechaFutura = LocalDate.now().plusDays(1).toString();

        dtoValido = new ReservaDTO();
        dtoValido.setIdUsuario(1L);
        dtoValido.setIdArea(1L);
        dtoValido.setFecha(fechaFutura);
        dtoValido.setHoraInicio("10:00");
        dtoValido.setHoraFin("12:00");
        dtoValido.setDescripcion("Entrenamiento");
    }

    // ─── CREAR RESERVA ────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-RS-01: Crear reserva exitosamente sin conflictos de horario")
    void crearReserva_exitoso() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));
        when(reservaRepository.findByAreaIdAndFechaAndEstadoNot(anyLong(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(99L);
        reservaGuardada.setEstado("CONFIRMADA");
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        Reserva resultado = reservaService.crearReserva(dtoValido);

        assertNotNull(resultado);
        assertEquals("CONFIRMADA", resultado.getEstado());
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("TC-RS-02: Crear reserva falla si el usuario no existe")
    void crearReserva_usuarioNoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dtoValido));
        assertTrue(ex.getMessage().contains("usuario no existe"));
    }

    @Test
    @DisplayName("TC-RS-03: Crear reserva falla si el área no existe")
    void crearReserva_areaNOExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(areaRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dtoValido));
        assertTrue(ex.getMessage().contains("zona deportiva no existe"));
    }

    @Test
    @DisplayName("TC-RS-04: Crear reserva falla si el área está bloqueada")
    void crearReserva_areaBloqueada() {
        areaMock.setEstado("bloqueada");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dtoValido));
        assertTrue(ex.getMessage().contains("bloqueada") || ex.getMessage().contains("mantenimiento"));
    }

    @Test
    @DisplayName("TC-RS-05: Crear reserva falla si horaInicio >= horaFin")
    void crearReserva_horaInicioMayorQueHoraFin() {
        dtoValido.setHoraInicio("14:00");
        dtoValido.setHoraFin("10:00");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dtoValido));
        assertTrue(ex.getMessage().contains("hora de inicio debe ser antes"));
    }

    @Test
    @DisplayName("TC-RS-06: Crear reserva falla si el horario está fuera del rango del área")
    void crearReserva_fueraDelHorarioDelArea() {
        dtoValido.setHoraInicio("05:00"); // antes de apertura 07:00
        dtoValido.setHoraFin("06:00");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dtoValido));
        assertTrue(ex.getMessage().contains("fuera del rango de servicio"));
    }

    @Test
    @DisplayName("TC-RS-07: Crear reserva falla si hay conflicto de horario con reserva existente")
    void crearReserva_conflictoDeHorario() {
        Reserva reservaExistente = new Reserva();
        reservaExistente.setHoraInicio("09:00");
        reservaExistente.setHoraFin("11:00");
        reservaExistente.setEstado("CONFIRMADA");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));
        when(reservaRepository.findByAreaIdAndFechaAndEstadoNot(anyLong(), anyString(), anyString()))
                .thenReturn(List.of(reservaExistente));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dtoValido));
        assertTrue(ex.getMessage().contains("Horario ocupado") || ex.getMessage().contains("Choca"));
    }

    @Test
    @DisplayName("TC-RS-08: Crear reserva falla si la fecha/hora ya pasó")
    void crearReserva_fechaPasada() {
        dtoValido.setFecha("2020-01-01");
        dtoValido.setHoraInicio("10:00");

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dtoValido));
        assertTrue(ex.getMessage().contains("ya pasó") || ex.getMessage().contains("pasado"));
    }

    @Test
    @DisplayName("TC-RS-09: Crear reserva — horarios exactamente adyacentes NO deben chocar")
    void crearReserva_horariosAdyacentesNoChocan() throws Exception {
        // Reserva existente: 08:00 - 10:00. Nueva: 10:00 - 12:00 → NO hay overlap
        Reserva reservaExistente = new Reserva();
        reservaExistente.setHoraInicio("08:00");
        reservaExistente.setHoraFin("10:00");
        reservaExistente.setEstado("CONFIRMADA");

        dtoValido.setHoraInicio("10:00");
        dtoValido.setHoraFin("12:00");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));
        when(reservaRepository.findByAreaIdAndFechaAndEstadoNot(anyLong(), anyString(), anyString()))
                .thenReturn(List.of(reservaExistente));

        Reserva guardada = new Reserva();
        guardada.setId(10L);
        guardada.setEstado("CONFIRMADA");
        when(reservaRepository.save(any())).thenReturn(guardada);

        Reserva resultado = reservaService.crearReserva(dtoValido);
        assertNotNull(resultado);
    }

    // ─── CANCELAR RESERVA ─────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-RS-10: Cancelar reserva exitosamente")
    void cancelarReserva_exitoso() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado("CONFIRMADA");
        reserva.setFecha(LocalDate.now().plusDays(1).toString());
        reserva.setHoraFin("23:00");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any())).thenReturn(reserva);

        Reserva resultado = reservaService.cancelarReserva(1L);
        assertEquals("CANCELADA", resultado.getEstado());
    }

    @Test
    @DisplayName("TC-RS-11: Cancelar reserva falla si ya está COMPLETADA")
    void cancelarReserva_yaCompletada() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado("COMPLETADA");
        reserva.setFecha(LocalDate.now().plusDays(1).toString());
        reserva.setHoraFin("23:00");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        Exception ex = assertThrows(Exception.class, () -> reservaService.cancelarReserva(1L));
        assertTrue(ex.getMessage().contains("finalizó") || ex.getMessage().contains("COMPLETADA"));
    }

    @Test
    @DisplayName("TC-RS-12: Cancelar reserva falla si ya está CANCELADA")
    void cancelarReserva_yaEstabaCanCelada() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado("CANCELADA");
        reserva.setFecha(LocalDate.now().plusDays(1).toString());
        reserva.setHoraFin("23:00");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        Exception ex = assertThrows(Exception.class, () -> reservaService.cancelarReserva(1L));
        assertTrue(ex.getMessage().contains("ya se encontraba cancelada"));
    }

    @Test
    @DisplayName("TC-RS-13: Cancelar reserva falla si el ID no existe")
    void cancelarReserva_idNoExiste() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.cancelarReserva(999L));
        assertTrue(ex.getMessage().contains("reserva no existe"));
    }

    // ─── ACTUALIZAR RESERVA ───────────────────────────────────────────────────

    @Test
    @DisplayName("TC-RS-14: Actualizar reserva exitosamente")
    void actualizarReserva_exitoso() throws Exception {
        Reserva existente = new Reserva();
        existente.setId(1L);
        existente.setEstado("CONFIRMADA");
        existente.setFecha(LocalDate.now().plusDays(2).toString());
        existente.setHoraFin("23:00");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));
        when(reservaRepository.findConflictingConfirmadasForUpdate(anyLong(), anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(reservaRepository.save(any())).thenReturn(existente);

        Reserva resultado = reservaService.actualizarReserva(1L, dtoValido);
        assertNotNull(resultado);
        verify(reservaRepository).save(any());
    }

    @Test
    @DisplayName("TC-RS-15: Actualizar reserva falla si la reserva no existe")
    void actualizarReserva_noExiste() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(999L, dtoValido));
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    @DisplayName("TC-RS-16: Actualizar reserva falla si está COMPLETADA")
    void actualizarReserva_yaCompletada() {
        Reserva existente = new Reserva();
        existente.setId(1L);
        existente.setEstado("COMPLETADA");
        existente.setFecha(LocalDate.now().plusDays(1).toString());
        existente.setHoraFin("23:00");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existente));

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(1L, dtoValido));
        assertTrue(ex.getMessage().contains("finalizó"));
    }

    @Test
    @DisplayName("TC-RS-17: Actualizar reserva falla si está CANCELADA")
    void actualizarReserva_yaCanCelada() {
        Reserva existente = new Reserva();
        existente.setId(1L);
        existente.setEstado("CANCELADA");
        existente.setFecha(LocalDate.now().plusDays(1).toString());
        existente.setHoraFin("23:00");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existente));

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(1L, dtoValido));
        assertTrue(ex.getMessage().contains("cancelada"));
    }
}
