package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.model.Rol;
import mx.edu.utez.Integradora5A_SIGRAD.repository.RolRepository;
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
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    @Test
    void listarRoles_happyPath_devuelveLista() {
        Rol r1 = new Rol();
        // id no es necesario para el service
        r1.setActivo(true);

        Rol r2 = new Rol();
        r2.setActivo(false);

        when(rolRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Rol> result = rolService.listarRoles();

        assertEquals(2, result.size());
        assertEquals(true, result.get(0).getActivo());
        assertEquals(false, result.get(1).getActivo());
    }

    @Test
    void guardar_happyPath_guardaRol() {
        Rol rol = new Rol();
        rol.setActivo(true);

        when(rolRepository.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        Rol saved = rolService.guardar(rol);

        assertNotNull(saved);
        assertEquals(true, saved.getActivo());
        verify(rolRepository).save(any(Rol.class));
    }

    @Test
    void cambiarEstado_happyPath_toggleActivo_guardaRolActualizado() {
        long id = 10L;
        Rol rol = new Rol();
        rol.setActivo(true);

        when(rolRepository.findById(id)).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        rolService.cambiarEstado(id);

        assertEquals(false, rol.getActivo());
        verify(rolRepository).save(rol);
    }

    @Test
    void cambiarEstado_unhappyPath_idNoExiste_lanzaRuntimeException() {
        long id = 10L;
        when(rolRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rolService.cambiarEstado(id));
        assertEquals("Rol no encontrado", ex.getMessage());
        verify(rolRepository, never()).save(any(Rol.class));
    }
}

