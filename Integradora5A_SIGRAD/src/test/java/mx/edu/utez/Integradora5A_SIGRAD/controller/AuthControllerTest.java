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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLoginBuenaPath() {
        Map<String, String> req = new HashMap<>();
        req.put("correo", "user@utez.edu.mx");
        req.put("password", "secret");

        Usuario user = new Usuario();
        user.setNombre("Luis");
        user.setContrasena("secret");
        user.setRol("ADMIN");

        when(usuarioRepository.findByEmailInstitucional("user@utez.edu.mx")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals("ADMIN", body.get("rol"));
    }

    @Test
    void testLoginMalaPath() {
        Map<String, String> req = new HashMap<>();
        req.put("correo", " ");
        req.put("password", "");

        ResponseEntity<?> response = authController.login(req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLoginMalaPathPasswordMal() {
        Map<String, String> req = Map.of("correo", "a@utez.edu.mx", "password", "wrong");
        Usuario user = new Usuario();
        user.setContrasena("good");
        when(usuarioRepository.findByEmailInstitucional("a@utez.edu.mx")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.login(req);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // no hice el caso usuario no existe (404) - se me olvido
}
