package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Rol;
import mx.edu.utez.Integradora5A_SIGRAD.service.RolService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolControllerTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolController rolController;

    @Test
    void testObtenerRolesBuenaPath() {
        List<Rol> roles = List.of(new Rol());
        when(rolService.listarRoles()).thenReturn(roles);

        List<Rol> result = rolController.obtenerRoles();

        assertEquals(roles, result);
    }

    @Test
    void testGuardarRolBuenaPath() {
        Rol rol = new Rol();
        rol.setNombre("DOCENTE");
        rol.setActivo(true);
        Rol guardado = new Rol();
        guardado.setId(1L);
        when(rolService.guardar(any(Rol.class))).thenReturn(guardado);

        ResponseEntity<Rol> response = rolController.guardarRol(rol);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(guardado, response.getBody());
    }

    @Test
    void testGuardarRolBuenaPathActivosNull() {
        Rol rol = new Rol();
        rol.setNombre("ALUMNO");
        rol.setActivo(null);
        when(rolService.guardar(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Rol> response = rolController.guardarRol(rol);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getActivo());
        verify(rolService).guardar(argThat(r -> Boolean.TRUE.equals(r.getActivo())));
    }

    @Test
    void testCambiarEstadoRolBuenaPath() {
        doNothing().when(rolService).cambiarEstado(5L);

        ResponseEntity<?> response = rolController.cambiarEstado(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rolService).cambiarEstado(5L);
    }

    // me falto el test cuando el service truena en obtener roles y en guardar
}
