package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.model.Carrera;
import mx.edu.utez.Integradora5A_SIGRAD.repository.CarreraRepository;
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
class CarreraServiceTest {

    @Mock
    private CarreraRepository carreraRepository;

    @InjectMocks
    private CarreraService carreraService;

    @Test
    void listarTodas_happyPath_devuelveListaDeCarreras() {
        Carrera c1 = new Carrera();
        c1.setId(1L);
        c1.setNombre("Ing Sistemas");
        c1.setHabilitada(true);

        Carrera c2 = new Carrera();
        c2.setId(2L);
        c2.setNombre("Ing Industrial");
        c2.setHabilitada(false);

        when(carreraRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Carrera> result = carreraService.listarTodas();

        assertEquals(2, result.size());
        assertEquals("Ing Sistemas", result.get(0).getNombre());
        assertEquals("Ing Industrial", result.get(1).getNombre());
    }

    @Test
    void guardar_unhappyPath_nombreNulo_lanzaException() {
        Carrera carrera = new Carrera();
        carrera.setNombre(null);

        Exception ex = assertThrows(Exception.class, () -> carreraService.guardar(carrera));
        assertEquals("El nombre de la carrera es obligatorio", ex.getMessage());
        verify(carreraRepository, never()).save(any(Carrera.class));
    }

    @Test
    void guardar_unhappyPath_nombreVacio_lanzaException() {
        Carrera carrera = new Carrera();
        carrera.setNombre("");

        Exception ex = assertThrows(Exception.class, () -> carreraService.guardar(carrera));
        assertEquals("El nombre de la carrera es obligatorio", ex.getMessage());
        verify(carreraRepository, never()).save(any(Carrera.class));
    }

    @Test
    void guardar_happyPath_nombreValido_guardaCarrera() throws Exception {
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Ing Sistemas");
        carrera.setHabilitada(true);

        when(carreraRepository.save(any(Carrera.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrera saved = carreraService.guardar(carrera);

        assertNotNull(saved);
        assertEquals("Ing Sistemas", saved.getNombre());
        assertEquals(true, saved.getHabilitada());
        verify(carreraRepository).save(any(Carrera.class));
    }

    @Test
    void actualizar_unhappyPath_idNoExiste_lanzaException() {
        long id = 10L;
        Carrera carreraUpdate = new Carrera();
        carreraUpdate.setNombre("Nuevo nombre");

        when(carreraRepository.findById(id)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> carreraService.actualizar(id, carreraUpdate));
        assertEquals("Error: Carrera no encontrada", ex.getMessage());
        verify(carreraRepository, never()).save(any(Carrera.class));
    }

    @Test
    void actualizar_unhappyPath_nombreVacio_lanzaException() throws Exception {
        long id = 10L;
        Carrera existente = new Carrera();
        existente.setId(id);
        existente.setNombre("Existente");
        existente.setHabilitada(true);

        Carrera carreraUpdate = new Carrera();
        carreraUpdate.setNombre("   "); // trim() vacio => segundo operando del ||

        when(carreraRepository.findById(id)).thenReturn(Optional.of(existente));

        Exception ex = assertThrows(Exception.class, () -> carreraService.actualizar(id, carreraUpdate));
        assertEquals("El nombre de la carrera es obligatorio", ex.getMessage());
        verify(carreraRepository, never()).save(any(Carrera.class));
    }

    @Test
    void actualizar_unhappyPath_nombreNull_lanzaException() {
        long id = 10L;
        Carrera existente = new Carrera();
        existente.setId(id);
        existente.setNombre("Existente");
        existente.setHabilitada(true);

        Carrera carreraUpdate = new Carrera();
        carreraUpdate.setNombre(null);

        when(carreraRepository.findById(id)).thenReturn(Optional.of(existente));

        Exception ex = assertThrows(Exception.class, () -> carreraService.actualizar(id, carreraUpdate));
        assertEquals("El nombre de la carrera es obligatorio", ex.getMessage());
        verify(carreraRepository, never()).save(any(Carrera.class));
    }

    @Test
    void actualizar_happyPath_actualizaNombre() throws Exception {
        long id = 10L;
        Carrera existente = new Carrera();
        existente.setId(id);
        existente.setNombre("Existente");
        existente.setHabilitada(true);

        Carrera carreraUpdate = new Carrera();
        carreraUpdate.setNombre("Nuevo nombre");

        when(carreraRepository.findById(id)).thenReturn(Optional.of(existente));
        when(carreraRepository.save(any(Carrera.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrera saved = carreraService.actualizar(id, carreraUpdate);
        assertEquals("Nuevo nombre", saved.getNombre());
    }

    @Test
    void cambiarEstado_unhappyPath_idNoExiste_lanzaRuntimeException() {
        long id = 123L;
        when(carreraRepository.findById(id)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> carreraService.cambiarEstado(id));
        assertEquals("Error: Carrera no encontrada", ex.getMessage());
    }

    @Test
    void cambiarEstado_happyPath_toggleBooleano_guardaCarreraConEstadoInvertido() throws Exception {
        long id = 123L;
        Carrera existente = new Carrera();
        existente.setId(id);
        existente.setHabilitada(true);

        when(carreraRepository.findById(id)).thenReturn(Optional.of(existente));
        when(carreraRepository.save(any(Carrera.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrera saved = carreraService.cambiarEstado(id);

        assertEquals(false, saved.getHabilitada());
        verify(carreraRepository).save(any(Carrera.class));
    }
}

