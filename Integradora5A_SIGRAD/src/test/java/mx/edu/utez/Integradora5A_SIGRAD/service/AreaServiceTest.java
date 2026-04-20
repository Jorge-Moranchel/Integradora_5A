package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.dto.BloqueoDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private AreaService areaService;

    @Test
    void registrarArea_happyPath_nombreDisponible_horaValida_guardaAreaDisponible() throws Exception {
        AreaDTO dto = new AreaDTO();
        dto.setNombre("Zona1");
        dto.setUbicacion("Ubicacion1");
        dto.setTipo("tipo1");
        dto.setHoraApertura("08:00");
        dto.setHoraCierre("17:00");
        dto.setImagen("img.png");

        when(areaRepository.existsByNombre(dto.getNombre())).thenReturn(false);
        when(areaRepository.save(any(Area.class))).thenAnswer(inv -> inv.getArgument(0));

        Area saved = areaService.registrarArea(dto);

        assertNotNull(saved);
        assertEquals("Zona1", saved.getNombre());
        assertEquals("Ubicacion1", saved.getUbicacion());
        assertEquals("tipo1",saved.getTipo());
        assertEquals("08:00", saved.getHoraApertura());
        assertEquals("17:00", saved.getHoraCierre());
        assertEquals("img.png", saved.getImagen());
        assertEquals("disponible", saved.getEstado());
        verify(areaRepository).save(any(Area.class));
    }

    @Test
    void registrarArea_unhappyPath_nombreYaExiste_lanzaException() {
        AreaDTO dto = new AreaDTO();
        dto.setNombre("Zona1");

        when(areaRepository.existsByNombre(dto.getNombre())).thenReturn(true);

        Exception ex = assertThrows(Exception.class, () -> areaService.registrarArea(dto));
        assertEquals("Error: El nombre de la zona ya está registrado", ex.getMessage());
        verify(areaRepository, never()).save(any(Area.class));
    }

    @Test
    void registrarArea_unhappyPath_horaAperturaMayorIgualCierre_lanzaException() {
        AreaDTO dto = new AreaDTO();
        dto.setNombre("Zona1");
        dto.setUbicacion("Ubicacion1");
        dto.setTipo("tipo1");
        dto.setHoraApertura("17:00");
        dto.setHoraCierre("17:00");
        dto.setImagen("img.png");

        when(areaRepository.existsByNombre(dto.getNombre())).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> areaService.registrarArea(dto));
        assertEquals("Error: La hora de apertura no puede ser mayor o igual a la hora de cierre", ex.getMessage());
        verify(areaRepository, never()).save(any(Area.class));
    }

    @Test
    void actualizarArea_unhappyPath_idNoExiste_lanzaException() {
        long id = 1L;
        AreaDTO dto = new AreaDTO();
        dto.setNombre("Zona1");
        dto.setUbicacion("U1");
        dto.setTipo("t1");
        dto.setHoraApertura("08:00");
        dto.setHoraCierre("17:00");

        when(areaRepository.findById(id)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> areaService.actualizarArea(id, dto));
        assertEquals("Error: Área no encontrada", ex.getMessage());
    }

    @Test
    void actualizarArea_unhappyPath_nombreOcupado_lanzaException() {
        long id = 1L;
        Area areaActual = new Area();
        areaActual.setId(id);
        areaActual.setNombre("ZonaActual");
        areaActual.setUbicacion("U1");
        areaActual.setTipo("t1");
        areaActual.setHoraApertura("07:00");
        areaActual.setHoraCierre("16:00");
        areaActual.setImagen("old.png");

        AreaDTO dto = new AreaDTO();
        dto.setNombre("ZonaNuevo");
        dto.setUbicacion("U2");
        dto.setTipo("t2");
        dto.setHoraApertura("08:00");
        dto.setHoraCierre("17:00");
        dto.setImagen("new.png");

        when(areaRepository.findById(id)).thenReturn(Optional.of(areaActual));
        when(areaRepository.existsByNombre(dto.getNombre())).thenReturn(true);

        Exception ex = assertThrows(Exception.class, () -> areaService.actualizarArea(id, dto));
        assertEquals("Error: El nombre de la zona ya está ocupado", ex.getMessage());
        verify(areaRepository, never()).save(any(Area.class));
    }

    @Test
    void actualizarArea_unhappyPath_horaAperturaMayorIgualCierre_lanzaException() {
        long id = 1L;
        Area areaActual = new Area();
        areaActual.setId(id);
        areaActual.setNombre("ZonaActual");

        AreaDTO dto = new AreaDTO();
        dto.setNombre("ZonaActual");
        dto.setUbicacion("U2");
        dto.setTipo("t2");
        dto.setHoraApertura("17:00");
        dto.setHoraCierre("17:00");
        dto.setImagen("img.png");

        when(areaRepository.findById(id)).thenReturn(Optional.of(areaActual));
        when(areaRepository.existsByNombre(anyString())).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> areaService.actualizarArea(id, dto));
        assertEquals("Error: La hora de apertura no puede ser mayor o igual a la hora de cierre", ex.getMessage());
        verify(areaRepository, never()).save(any(Area.class));
    }

    @Test
    void actualizarArea_happyPath_nombreSinDuplicado_yImagenNull_noActualizaImagen() throws Exception {
        long id = 1L;
        Area areaActual = new Area();
        areaActual.setId(id);
        areaActual.setNombre("ZonaActual");
        areaActual.setUbicacion("U1");
        areaActual.setTipo("t1");
        areaActual.setHoraApertura("07:00");
        areaActual.setHoraCierre("16:00");
        areaActual.setImagen("old.png");

        AreaDTO dto = new AreaDTO();
        dto.setNombre("ZonaNuevo"); // distinto => valida duplicado
        dto.setUbicacion("U2");
        dto.setTipo("t2");
        dto.setHoraApertura("08:00");
        dto.setHoraCierre("17:00");
        dto.setImagen(null); // cubre &&: dto.getImagen() != null es false

        when(areaRepository.findById(id)).thenReturn(Optional.of(areaActual));
        when(areaRepository.existsByNombre(dto.getNombre())).thenReturn(false);
        when(areaRepository.save(any(Area.class))).thenAnswer(inv -> inv.getArgument(0));

        Area saved = areaService.actualizarArea(id, dto);

        assertEquals("ZonaNuevo", saved.getNombre());
        assertEquals("U2", saved.getUbicacion());
        assertEquals("t2", saved.getTipo());
        assertEquals("08:00", saved.getHoraApertura());
        assertEquals("17:00", saved.getHoraCierre());
        assertEquals("old.png", saved.getImagen(), "La imagen no debe cambiar cuando dto.imagen es null");
    }

    @Test
    void actualizarArea_happyPath_nombreIgualIgnoreCase_yImagenVacia_noActualizaImagen() throws Exception {
        long id = 1L;
        Area areaActual = new Area();
        areaActual.setId(id);
        areaActual.setNombre("ZonaActual");
        areaActual.setUbicacion("U1");
        areaActual.setTipo("t1");
        areaActual.setHoraApertura("07:00");
        areaActual.setHoraCierre("16:00");
        areaActual.setImagen("old.png");

        AreaDTO dto = new AreaDTO();
        dto.setNombre("zonaactual"); // igual ignoreCase => primera parte del && es false
        dto.setUbicacion("U2");
        dto.setTipo("t2");
        dto.setHoraApertura("08:00");
        dto.setHoraCierre("17:00");
        dto.setImagen(""); // cubre &&: dto.getImagen() != null true, pero !isEmpty false

        when(areaRepository.findById(id)).thenReturn(Optional.of(areaActual));
        // Si se intentara validar duplicado (no debería), fallamos.
        when(areaRepository.existsByNombre(anyString()))
                .thenThrow(new AssertionError("No debe llamarse existsByNombre cuando el nombre es igual (ignoreCase)"));
        when(areaRepository.save(any(Area.class))).thenAnswer(inv -> inv.getArgument(0));

        Area saved = areaService.actualizarArea(id, dto);

        assertEquals("zonaactual", saved.getNombre());
        assertEquals("U2", saved.getUbicacion());
        assertEquals("t2", saved.getTipo());
        assertEquals("08:00", saved.getHoraApertura());
        assertEquals("17:00", saved.getHoraCierre());
        assertEquals("old.png", saved.getImagen(), "La imagen no debe cambiar cuando dto.imagen es vacia");
    }

    @Test
    void actualizarArea_happyPath_nombreSinDuplicado_yImagenNoVacia_actualizaImagen() throws Exception {
        long id = 1L;
        Area areaActual = new Area();
        areaActual.setId(id);
        areaActual.setNombre("ZonaActual");
        areaActual.setUbicacion("U1");
        areaActual.setTipo("t1");
        areaActual.setHoraApertura("07:00");
        areaActual.setHoraCierre("16:00");
        areaActual.setImagen("old.png");

        AreaDTO dto = new AreaDTO();
        dto.setNombre("ZonaNuevo"); // distinto => valida duplicado
        dto.setUbicacion("U2");
        dto.setTipo("t2");
        dto.setHoraApertura("08:00");
        dto.setHoraCierre("17:00");
        dto.setImagen("new.png"); // cubre &&: imagen != null y !isEmpty

        when(areaRepository.findById(id)).thenReturn(Optional.of(areaActual));
        when(areaRepository.existsByNombre(dto.getNombre())).thenReturn(false);
        when(areaRepository.save(any(Area.class))).thenAnswer(inv -> inv.getArgument(0));

        Area saved = areaService.actualizarArea(id, dto);

        assertEquals("ZonaNuevo", saved.getNombre());
        assertEquals("new.png", saved.getImagen());
    }

    @Test
    void bloquearArea_unhappyPath_idNoExiste_lanzaException() {
        long id = 1L;
        BloqueoDTO bloqueoDTO = new BloqueoDTO();
        bloqueoDTO.setMotivoBloqueo("Motivo");
        bloqueoDTO.setFechaInicioBloqueo("2026-03-01");
        bloqueoDTO.setFechaFinBloqueo("2026-03-31");

        when(areaRepository.findById(id)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> areaService.bloquearArea(id, bloqueoDTO));
        assertEquals("Error: Área no encontrada", ex.getMessage());
        verify(areaRepository, never()).save(any(Area.class));
    }

    @Test
    void bloquearArea_happyPath_areaExiste_bloqueaArea() throws Exception {
        long id = 1L;
        Area area = new Area();
        area.setId(id);
        area.setNombre("Zona1");
        area.setEstado("disponible");

        BloqueoDTO bloqueoDTO = new BloqueoDTO();
        bloqueoDTO.setMotivoBloqueo("Mantenimiento");
        bloqueoDTO.setFechaInicioBloqueo("2026-05-01");
        bloqueoDTO.setFechaFinBloqueo("2026-05-31");

        when(areaRepository.findById(id)).thenReturn(Optional.of(area));
        when(areaRepository.save(any(Area.class))).thenAnswer(inv -> inv.getArgument(0));

        Area saved = areaService.bloquearArea(id, bloqueoDTO);

        assertEquals("bloqueada", saved.getEstado());
        assertEquals("Mantenimiento", saved.getMotivoBloqueo());
        assertEquals("2026-05-01", saved.getFechaInicioBloqueo());
        assertEquals("2026-05-31", saved.getFechaFinBloqueo());
    }

    @Test
    void desbloquearArea_unhappyPath_idNoExiste_lanzaException() {
        long id = 1L;
        when(areaRepository.findById(id)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> areaService.desbloquearArea(id));
        assertEquals("Error: Área no encontrada", ex.getMessage());
        verify(areaRepository, never()).save(any(Area.class));
    }

    @Test
    void desbloquearArea_happyPath_areaExiste_desbloqueaArea() throws Exception {
        long id = 1L;
        Area area = new Area();
        area.setId(id);
        area.setEstado("bloqueada");
        area.setMotivoBloqueo("Mantenimiento");
        area.setFechaInicioBloqueo("2026-03-01");
        area.setFechaFinBloqueo("2026-03-31");

        when(areaRepository.findById(id)).thenReturn(Optional.of(area));
        when(areaRepository.save(any(Area.class))).thenAnswer(inv -> inv.getArgument(0));

        Area saved = areaService.desbloquearArea(id);

        assertEquals("disponible", saved.getEstado());
        assertNull(saved.getMotivoBloqueo());
        assertNull(saved.getFechaInicioBloqueo());
        assertNull(saved.getFechaFinBloqueo());
    }
}

