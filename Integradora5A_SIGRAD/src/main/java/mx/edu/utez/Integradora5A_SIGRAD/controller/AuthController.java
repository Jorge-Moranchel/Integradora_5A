package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.exception.ResourceNotFoundException;
import mx.edu.utez.Integradora5A_SIGRAD.exception.UnauthorizedException;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String password = request.get("password");
        Map<String, Object> response = new HashMap<>();

        // 1. Validación: No dejar campos vacíos
        if (correo == null || correo.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes ingresar correo y contraseña para entrar.");
        }

        // 2. Buscar al usuario en la base de datos de Oracle
        Optional<Usuario> userOpt = usuarioRepository.findByEmailInstitucional(correo);

        if (!userOpt.isPresent()) {
            throw new ResourceNotFoundException("No existe una cuenta con ese correo institucional.");
        }

        Usuario user = userOpt.get();

        // 3. Comparar contraseñas
        if (!Objects.equals(user.getContrasena(), password)) {
            throw new UnauthorizedException("La contraseña es incorrecta. Intenta de nuevo.");
        }

        // 4. CANDADO: Verificamos que el usuario no esté bloqueado (estado = false)
        if (user.getEstado() != null && !user.getEstado()) {
            throw new UnauthorizedException("Tu cuenta ha sido bloqueada. Contacta a un administrador.");
        }

        // 5. CANDADO: Verificamos que haya dado clic en el enlace del correo (validado = false)
        if (user.getValidado() != null && !user.getValidado()) {
            throw new UnauthorizedException("Tu cuenta aún no está activa. Revisa tu correo institucional y haz clic en el enlace de verificación.");
        }

        response.put("status", "success");
        response.put("message", "¡Bienvenido de nuevo, " + user.getNombre() + "!");
        response.put("nombre", user.getNombre());
        response.put("rol", user.getRol() != null ? user.getRol() : "ADMIN");

        return ResponseEntity.ok(response);
    }
}