package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void testListarUsuariosBuenaPath() {
        List<Usuario> lista = List.of(new Usuario());
        when(usuarioRepository.findAll()).thenReturn(lista);

        ResponseEntity<?> response = usuarioController.listarUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lista, response.getBody());
    }

    @Test
    void testRegistrarUsuarioBuenaPath() {
        Usuario u = usuarioValido();
        when(usuarioRepository.existsByEmailInstitucional(u.getEmailInstitucional())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = usuarioController.registrarUsuario(u);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
    }

    @Test
    void testRegistrarUsuarioMalaPath() {
        Usuario u = new Usuario();
        u.setNombre("");

        ResponseEntity<?> response = usuarioController.registrarUsuario(u);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testActualizarUsuarioBuenaPath() {
        Usuario existente = usuarioValido();
        existente.setId(1L);
        Usuario actualizado = usuarioValido();
        actualizado.setNombre("Nuevo");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = usuarioController.actualizarUsuario(1L, actualizado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testActualizarUsuarioMalaPath() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = usuarioController.actualizarUsuario(99L, usuarioValido());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCambiarEstadoUsuarioBuenaPath() {
        Usuario user = new Usuario();
        user.setId(1L);
        user.setEstado(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = usuarioController.cambiarEstado(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRegistrarUsuarioMalaPathEmailNoUtez() {
        Usuario u = usuarioValido();
        u.setEmailInstitucional("x@gmail.com");

        ResponseEntity<?> response = usuarioController.registrarUsuario(u);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // no hice test del correo duplicado (409) ni del error 500 al guardar - prioridad baja

    private static Usuario usuarioValido() {
        Usuario u = new Usuario();
        u.setNombre("Ana");
        u.setMatricula("M001");
        u.setEmailInstitucional("ana@utez.edu.mx");
        u.setContrasena("pass123");
        return u;
    }
}
