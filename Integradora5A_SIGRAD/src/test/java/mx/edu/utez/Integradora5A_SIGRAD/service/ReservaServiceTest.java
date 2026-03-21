package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.ReservaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.ReservaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    private static final String FECHA = "2026-03-19";
    private static final String H_APERTURA = "14:00";
    private static final String H_CIERRE = "20:00";
    private static final long ID_USUARIO = 1L;
    private static final long ID_AREA = 1L;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crearReserva_unhappyPath_usuarioNoExiste_lanzaException() {
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:30", "15:30");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dto));
        assertEquals("Error: El usuario no existe.", ex.getMessage());
        verify(areaRepository, never()).findById(any());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_unhappyPath_areaNoExiste_lanzaException() {
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:30", "15:30");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dto));
        assertEquals("Error: La zona deportiva no existe.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_unhappyPath_areaNoDisponible_areaBloqueada_lanzaException() {
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:30", "15:30");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, "bloqueada", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dto));
        assertEquals("Lo sentimos, esta área se encuentra bloqueada o en mantenimiento.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_happyPath_areaEstadoNull_cubreRama_disponiblePorDefecto_yGuarda() throws Exception {
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:30", "15:30");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, null, H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));
        when(reservaRepository.findByAreaIdAndFechaAndEstadoNot(ID_AREA, FECHA, "CANCELADA")).thenReturn(List.of());
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = reservaService.crearReserva(dto);

        assertEquals(ID_AREA, saved.getArea().getId());
        assertEquals(FECHA, saved.getFecha());
        assertEquals("14:30", saved.getHoraInicio());
        assertEquals("15:30", saved.getHoraFin());
        assertEquals("CONFIRMADA", saved.getEstado());
        assertNotNull(saved.getUsuario());
    }

    @Test
    void crearReserva_unhappyPath_superposicion_true_lanzaExceptionHorarioOcupado() {
        // Existing: 15:00 - 16:00. New: 15:30 - 16:30 => overlap true
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "15:30", "16:30");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Reserva existente = new Reserva();
        existente.setHoraInicio("15:00");
        existente.setHoraFin("16:00");
        when(reservaRepository.findByAreaIdAndFechaAndEstadoNot(ID_AREA, FECHA, "CANCELADA"))
                .thenReturn(List.of(existente));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dto));
        assertEquals("¡Horario ocupado! Choca con otra reserva de 15:00 a 16:00.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_happyPath_superposicion_primerTerminoFalso_noThrow_yGuarda() throws Exception {
        // Existing: 15:00 - 16:00. New: 16:00 - 17:00 => inicioNuevo == finExistente => first condition false
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "16:00", "17:00");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Reserva existente = new Reserva();
        existente.setHoraInicio("15:00");
        existente.setHoraFin("16:00");
        when(reservaRepository.findByAreaIdAndFechaAndEstadoNot(ID_AREA, FECHA, "CANCELADA"))
                .thenReturn(List.of(existente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = reservaService.crearReserva(dto);
        assertEquals("16:00", saved.getHoraInicio());
        assertEquals("17:00", saved.getHoraFin());
        assertEquals("CONFIRMADA", saved.getEstado());
    }

    @Test
    void crearReserva_happyPath_superposicion_segundoTerminoFalso_noThrow_yGuarda() throws Exception {
        // Existing: 15:00 - 16:00. New: 14:00 - 15:00 => finNuevo == inicioExistente => second condition false
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:00", "15:00");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Reserva existente = new Reserva();
        existente.setHoraInicio("15:00");
        existente.setHoraFin("16:00");
        when(reservaRepository.findByAreaIdAndFechaAndEstadoNot(ID_AREA, FECHA, "CANCELADA"))
                .thenReturn(List.of(existente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = reservaService.crearReserva(dto);
        assertEquals("14:00", saved.getHoraInicio());
        assertEquals("15:00", saved.getHoraFin());
        assertEquals("CONFIRMADA", saved.getEstado());
    }

    @Test
    void crearReserva_unhappyPath_inicioDespuesFin_lanzaException() {
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "16:00", "15:00");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dto));
        assertEquals("La hora de inicio debe ser antes que la hora de fin.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_unhappyPath_inicioIgualFin_lanzaException() {
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "15:00", "15:00");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dto));
        assertEquals("La hora de inicio debe ser antes que la hora de fin.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_unhappyPath_inicioAntesApertura_lanzaException() {
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "13:00", "14:30");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dto));
        assertEquals(
                "El horario está fuera del rango de servicio de esta zona (14:00 a 20:00).",
                ex.getMessage()
        );
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_unhappyPath_finDespuesCierre_lanzaException() {
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "15:00", "21:00");

        when(usuarioRepository.findById(dto.getIdUsuario())).thenReturn(Optional.of(usuario(ID_USUARIO)));
        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.crearReserva(dto));
        assertEquals(
                "El horario está fuera del rango de servicio de esta zona (14:00 a 20:00).",
                ex.getMessage()
        );
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    // =========================
    // CANCELAR RESERVA
    // =========================

    @Test
    void cancelarReserva_unhappyPath_idNoExiste_lanzaException() {
        long id = 10L;
        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.cancelarReserva(id));
        assertEquals("Error: La reserva no existe.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_unhappyPath_yaCancelada_lanzaException() {
        long id = 10L;
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setEstado("cancelada"); // equalsIgnoreCase => true

        when(reservaRepository.findById(id)).thenReturn(Optional.of(reserva));

        Exception ex = assertThrows(Exception.class, () -> reservaService.cancelarReserva(id));
        assertEquals("La reserva ya se encontraba cancelada.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_happyPath_actualizaEstadoAConfirmaCancelada() throws Exception {
        long id = 10L;
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setEstado("CONFIRMADA");
        reserva.setHoraInicio("14:00");
        reserva.setHoraFin("15:00");

        when(reservaRepository.findById(id)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = reservaService.cancelarReserva(id);
        assertEquals("CANCELADA", saved.getEstado());
        verify(reservaRepository).save(any(Reserva.class));
    }

    // =========================
    // ACTUALIZAR RESERVA
    // =========================

    @Test
    void actualizarReserva_unhappyPath_reservaNoExiste_lanzaException() {
        long id = 10L;
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:30", "15:30");
        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(id, dto));
        assertEquals("Error: La reserva que intentas editar no existe.", ex.getMessage());
        verify(areaRepository, never()).findById(any());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void actualizarReserva_unhappyPath_areaNoExiste_lanzaException() {
        long id = 10L;
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:30", "15:30");

        Reserva existente = new Reserva();
        existente.setId(id);
        existente.setHoraInicio("14:00");
        existente.setHoraFin("15:00");
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(id, dto));
        assertEquals("Error: La zona deportiva no existe.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void actualizarReserva_unhappyPath_areaNoDisponible_areaBloqueada_lanzaException() {
        long id = 10L;
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:30", "15:30");

        Reserva existente = new Reserva();
        existente.setId(id);
        existente.setHoraInicio("14:00");
        existente.setHoraFin("15:00");
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, "bloqueada", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(id, dto));
        assertEquals("Lo sentimos, esta área se encuentra bloqueada.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void actualizarReserva_happyPath_areaEstadoNull_ignoreIdPropio_yGuarda() throws Exception {
        long id = 10L;
        // DTO se traslapa con la reserva "existente", pero el método de repository ignora el ID,
        // así que findByAreaIdAndFechaAndEstadoNotAndIdNot retorna lista vacía.
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "15:30", "16:30");

        Reserva existente = new Reserva();
        existente.setId(id);
        existente.setHoraInicio("15:00");
        existente.setHoraFin("16:00");
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, null, H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        when(reservaRepository.findByAreaIdAndFechaAndEstadoNotAndIdNot(ID_AREA, FECHA, "CANCELADA", id))
                .thenReturn(List.of());
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = reservaService.actualizarReserva(id, dto);

        assertEquals(FECHA, saved.getFecha());
        assertEquals("15:30", saved.getHoraInicio());
        assertEquals("16:30", saved.getHoraFin());
        verify(reservaRepository).findByAreaIdAndFechaAndEstadoNotAndIdNot(ID_AREA, FECHA, "CANCELADA", id);
    }

    @Test
    void actualizarReserva_happyPath_areaDisponible_superposicion_primerTerminoFalso_noThrow() throws Exception {
        long id = 10L;
        // New: 16:00 - 17:00 => inicioNuevo == finExistente => primer término falso
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "16:00", "17:00");

        Reserva existente = new Reserva();
        existente.setId(id);
        existente.setHoraInicio("15:00");
        existente.setHoraFin("16:00");
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Reserva otraReserva = new Reserva();
        otraReserva.setHoraInicio("15:00");
        otraReserva.setHoraFin("16:00");

        when(reservaRepository.findByAreaIdAndFechaAndEstadoNotAndIdNot(ID_AREA, FECHA, "CANCELADA", id))
                .thenReturn(List.of(otraReserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = reservaService.actualizarReserva(id, dto);
        assertEquals("16:00", saved.getHoraInicio());
        assertEquals("17:00", saved.getHoraFin());
    }

    @Test
    void actualizarReserva_happyPath_areaDisponible_superposicion_segundoTerminoFalso_noThrow() throws Exception {
        long id = 10L;
        // New: 14:00 - 15:00 => finNuevo == inicioExistente => segundo término falso
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "14:00", "15:00");

        Reserva existente = new Reserva();
        existente.setId(id);
        existente.setHoraInicio("10:00");
        existente.setHoraFin("11:00");
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Reserva otraReserva = new Reserva();
        otraReserva.setHoraInicio("15:00");
        otraReserva.setHoraFin("16:00");

        when(reservaRepository.findByAreaIdAndFechaAndEstadoNotAndIdNot(ID_AREA, FECHA, "CANCELADA", id))
                .thenReturn(List.of(otraReserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = reservaService.actualizarReserva(id, dto);
        assertEquals("14:00", saved.getHoraInicio());
        assertEquals("15:00", saved.getHoraFin());
    }

    @Test
    void actualizarReserva_unhappyPath_inicioDespuesFin_lanzaException() {
        long id = 10L;
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "16:00", "15:00");

        Reserva existente = new Reserva();
        existente.setId(id);
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(id, dto));
        assertEquals("La hora de inicio debe ser antes que la hora de fin.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void actualizarReserva_unhappyPath_inicioIgualFin_lanzaException() {
        long id = 10L;
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "15:00", "15:00");

        Reserva existente = new Reserva();
        existente.setId(id);
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(id, dto));
        assertEquals("La hora de inicio debe ser antes que la hora de fin.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void actualizarReserva_unhappyPath_inicioAntesApertura_lanzaException() {
        long id = 10L;
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "13:00", "14:30");

        Reserva existente = new Reserva();
        existente.setId(id);
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(id, dto));
        assertEquals("El horario está fuera del rango de servicio (14:00 a 20:00).", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void actualizarReserva_unhappyPath_finDespuesCierre_lanzaException() {
        long id = 10L;
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "15:00", "21:00");

        Reserva existente = new Reserva();
        existente.setId(id);
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(id, dto));
        assertEquals("El horario está fuera del rango de servicio (14:00 a 20:00).", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void actualizarReserva_unhappyPath_superposicion_true_lanzaExceptionHorarioOcupado() {
        long id = 10L;
        ReservaDTO dto = baseDto(ID_USUARIO, ID_AREA, FECHA, "15:30", "16:30");

        Reserva existente = new Reserva();
        existente.setId(id);
        when(reservaRepository.findById(id)).thenReturn(Optional.of(existente));

        Area area = area(ID_AREA, "disponible", H_APERTURA, H_CIERRE);
        when(areaRepository.findById(dto.getIdArea())).thenReturn(Optional.of(area));

        Reserva otraReserva = new Reserva();
        otraReserva.setHoraInicio("15:00");
        otraReserva.setHoraFin("16:00");
        when(reservaRepository.findByAreaIdAndFechaAndEstadoNotAndIdNot(ID_AREA, FECHA, "CANCELADA", id))
                .thenReturn(List.of(otraReserva));

        Exception ex = assertThrows(Exception.class, () -> reservaService.actualizarReserva(id, dto));
        assertEquals("¡Horario ocupado! Choca con otra reserva de 15:00 a 16:00.", ex.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    // =========================
    // Helpers
    // =========================

    private ReservaDTO baseDto(Long idUsuario, Long idArea, String fecha, String horaInicio, String horaFin) {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdUsuario(idUsuario);
        dto.setIdArea(idArea);
        dto.setFecha(fecha);
        dto.setHoraInicio(horaInicio);
        dto.setHoraFin(horaFin);
        dto.setDescripcion("desc");
        return dto;
    }

    private Usuario usuario(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Usuario " + id);
        u.setRol("ADMIN");
        u.setEstado(true);
        return u;
    }

    private Area area(Long id, String estado, String apertura, String cierre) {
        Area a = new Area();
        a.setId(id);
        a.setEstado(estado);
        a.setHoraApertura(apertura);
        a.setHoraCierre(cierre);
        return a;
    }

    @SuppressWarnings("unused")
    private Reserva reservaConHoras(String horaInicio, String horaFin) {
        Reserva r = new Reserva();
        r.setHoraInicio(horaInicio);
        r.setHoraFin(horaFin);
        return r;
    }
}

