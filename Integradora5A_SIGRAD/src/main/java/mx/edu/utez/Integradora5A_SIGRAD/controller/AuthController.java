package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            response.put("status", "error");
            response.put("message", "Debes ingresar correo y contraseña para entrar.");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. Buscar al usuario en la base de datos de Oracle
        Optional<Usuario> userOpt = usuarioRepository.findByEmailInstitucional(correo);

        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();

            // 3. Comparar contraseñas (Recuerda que estamos usando getters manuales)
            if (user.getContrasena().equals(password)) {
                response.put("status", "success");
                response.put("message", "¡Bienvenido de nuevo, " + user.getNombre() + "!");
                response.put("nombre", user.getNombre());
                response.put("rol", user.getRol() != null ? user.getRol() : "ADMIN");

                return ResponseEntity.ok(response);
            } else {
                // Contraseña incorrecta
                response.put("status", "error");
                response.put("message", "La contraseña es incorrecta. Intenta de nuevo.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

        // 4. Usuario no encontrado
        response.put("status", "error");
        response.put("message", "No existe una cuenta con ese correo institucional.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}