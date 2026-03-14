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

        Optional<Usuario> userOpt = usuarioRepository.findByEmailInstitucional(correo);

        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            // Ahora que los getters son manuales, esto NO debe marcar error
            if (user.getContrasena().equals(password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("nombre", user.getNombre());
                response.put("rol", user.getRol() != null ? user.getRol() : "ADMIN");
                
                return ResponseEntity.ok(response);
            }
        }

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Correo o contraseña incorrectos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}