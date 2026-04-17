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
        // ✅ NUEVO: Recibimos un parámetro extra desde React para saber si es la Web
        boolean esWeb = Boolean.parseBoolean(request.getOrDefault("esWeb", "false"));

        Map<String, Object> response = new HashMap<>();

        if (correo == null || correo.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes ingresar correo y contraseña para entrar.");
        }

        Optional<Usuario> userOpt = usuarioRepository.findByEmailInstitucional(correo);

        if (!userOpt.isPresent() || !Objects.equals(userOpt.get().getContrasena(), password)) {
            throw new UnauthorizedException("El correo y/o la contraseña son incorrectos.");
        }

        Usuario user = userOpt.get();

        // ✅ NUEVA REGLA ESTRICTA PARA LA WEB
        if (esWeb) {
            // Si intenta entrar a la web y NO es el ID 103 (o el rol ADMINISTRADOR si prefieres), lo pateamos
            if (user.getId() != 103L) {
                throw new UnauthorizedException("Acceso denegado. Solo el administrador principal puede entrar a este panel.");
            }
        }

        if (user.getEstado() != null && !user.getEstado()) {
            throw new UnauthorizedException("Tu cuenta ha sido bloqueada. Contacta a un administrador.");
        }

        if (user.getValidado() != null && !user.getValidado()) {
            throw new UnauthorizedException("Tu cuenta aún no está activa. Revisa tu correo institucional y haz clic en el enlace de verificación.");
        }

        response.put("status", "success");
        response.put("message", "¡Bienvenido de nuevo, " + user.getNombre() + "!");

        // 2. PERFIL COMPLETO: Mandamos todos los datos a la app móvil de un solo golpe
        response.put("id", user.getId());
        response.put("nombre", user.getNombre());
        response.put("rol", user.getRol() != null ? user.getRol() : "ADMIN");
        response.put("matricula", user.getMatricula() != null ? user.getMatricula() : "");
        response.put("telefono", user.getTelefono() != null ? user.getTelefono() : "");
        response.put("carrera", user.getCarrera() != null ? user.getCarrera() : "");
        response.put("emailInstitucional", user.getEmailInstitucional());

        return ResponseEntity.ok(response);
    }
}