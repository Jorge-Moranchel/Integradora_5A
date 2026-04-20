package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.dto.BloqueoDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias — AreaService")
class AreaServiceTest {

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private AreaService areaService;

    private AreaDTO dtoValido;
    private Area areaMock;

    @BeforeEach
    void setUp() {
        dtoValido = new AreaDTO();
        dtoValido.setNombre("Cancha de Básquetbol");
        dtoValido.setUbicacion("Edificio A");
        dtoValido.setHoraApertura("07:00");
        dtoValido.setHoraCierre("21:00");
        dtoValido.setImagen(null);

        areaMock = new Area();
        areaMock.setId(1L);
        areaMock.setNombre("Cancha de Básquetbol");
        areaMock.setEstado("disponible");
        areaMock.setHoraApertura("07:00");
        areaMock.setHoraCierre("21:00");
    }

    @Test
    @DisplayName("TC-AS-01: Registrar área exitosamente")
    void registrarArea_exitoso() throws Exception {
        when(areaRepository.existsByNombre("Cancha de Básquetbol")).thenReturn(false);
        when(areaRepository.save(any(Area.class))).thenReturn(areaMock);

        Area resultado = areaService.registrarArea(dtoValido);

        assertNotNull(resultado);
        assertEquals("Cancha de Básquetbol", resultado.getNombre());
        verify(areaRepository).save(any(Area.class));
    }

    @Test
    @DisplayName("TC-AS-02: Registrar área falla si el nombre ya existe")
    void registrarArea_nombreDuplicado() {
        when(areaRepository.existsByNombre("Cancha de Básquetbol")).thenReturn(true);

        Exception ex = assertThrows(Exception.class, () -> areaService.registrarArea(dtoValido));
        assertTrue(ex.getMessage().contains("nombre de la zona ya está registrado"));
    }

    @Test
    @DisplayName("TC-AS-03: Registrar área falla si horaApertura >= horaCierre")
    void registrarArea_horariosInvalidos() {
        dtoValido.setHoraApertura("22:00");
        dtoValido.setHoraCierre("07:00");
        when(areaRepository.existsByNombre(any())).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> areaService.registrarArea(dtoValido));
        assertTrue(ex.getMessage().contains("hora de apertura no puede ser mayor"));
    }

    @Test
    @DisplayName("TC-AS-04: Bloquear área exitosamente con fechas futuras válidas")
    void bloquearArea_exitoso() throws Exception {
        BloqueoDTO bloqueo = new BloqueoDTO();
        bloqueo.setMotivoBloqueo("Mantenimiento de piso");
        bloqueo.setFechaInicioBloqueo(LocalDate.now().plusDays(1).toString());
        bloqueo.setFechaFinBloqueo(LocalDate.now().plusDays(5).toString());

        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));
        when(areaRepository.save(any())).thenReturn(areaMock);

        Area resultado = areaService.bloquearArea(1L, bloqueo);
        assertNotNull(resultado);
        verify(areaRepository).save(any());
    }

    @Test
    @DisplayName("TC-AS-05: Bloquear área falla si la fecha de inicio ya pasó")
    void bloquearArea_fechaInicioEnPasado() {
        BloqueoDTO bloqueo = new BloqueoDTO();
        bloqueo.setMotivoBloqueo("Test");
        bloqueo.setFechaInicioBloqueo("2020-01-01");
        bloqueo.setFechaFinBloqueo(LocalDate.now().plusDays(5).toString());

        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));

        Exception ex = assertThrows(Exception.class, () -> areaService.bloquearArea(1L, bloqueo));
        assertTrue(ex.getMessage().contains("fecha que ya pasó") || ex.getMessage().contains("pasado"));
    }

    @Test
    @DisplayName("TC-AS-06: Bloquear área falla si fecha fin es anterior a fecha inicio")
    void bloquearArea_fechaFinAntesDeFechaInicio() {
        BloqueoDTO bloqueo = new BloqueoDTO();
        bloqueo.setMotivoBloqueo("Test");
        bloqueo.setFechaInicioBloqueo(LocalDate.now().plusDays(5).toString());
        bloqueo.setFechaFinBloqueo(LocalDate.now().plusDays(1).toString());

        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));

        Exception ex = assertThrows(Exception.class, () -> areaService.bloquearArea(1L, bloqueo));
        assertTrue(ex.getMessage().contains("fecha de fin no puede ser anterior"));
    }

    @Test
    @DisplayName("TC-AS-07: Bloquear área falla si el área no existe")
    void bloquearArea_noExiste() {
        BloqueoDTO bloqueo = new BloqueoDTO();
        bloqueo.setMotivoBloqueo("Test");
        bloqueo.setFechaInicioBloqueo(LocalDate.now().plusDays(1).toString());
        bloqueo.setFechaFinBloqueo(LocalDate.now().plusDays(5).toString());

        when(areaRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> areaService.bloquearArea(99L, bloqueo));
        assertTrue(ex.getMessage().contains("Área no encontrada"));
    }

    @Test
    @DisplayName("TC-AS-08: Desbloquear área exitosamente")
    void desbloquearArea_exitoso() throws Exception {
        areaMock.setEstado("bloqueada");
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));
        when(areaRepository.save(any())).thenReturn(areaMock);

        Area resultado = areaService.desbloquearArea(1L);
        assertNotNull(resultado);
        verify(areaRepository).save(any());
    }

    @Test
    @DisplayName("TC-AS-09: Actualizar área falla si el nombre nuevo ya está ocupado por otra área")
    void actualizarArea_nombreOcupado() {
        areaMock.setNombre("Cancha Vieja");
        AreaDTO dto = new AreaDTO();
        dto.setNombre("Nombre Nuevo");
        dto.setUbicacion("Edificio B");
        dto.setHoraApertura("08:00");
        dto.setHoraCierre("20:00");

        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaMock));
        when(areaRepository.existsByNombre("Nombre Nuevo")).thenReturn(true);

        Exception ex = assertThrows(Exception.class, () -> areaService.actualizarArea(1L, dto));
        assertTrue(ex.getMessage().contains("nombre de la zona ya está ocupado"));
    }
}
