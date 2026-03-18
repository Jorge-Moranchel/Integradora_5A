package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. LISTAR USUARIOS
    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // 2. REGISTRAR NUEVO USUARIO
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        Map<String, Object> response = new HashMap<>();

        // Validaciones de campos obligatorios
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
                usuario.getMatricula() == null || usuario.getMatricula().trim().isEmpty() ||
                usuario.getEmailInstitucional() == null || usuario.getEmailInstitucional().trim().isEmpty() ||
                usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {

            response.put("status", "error");
            response.put("message", "Faltan datos obligatorios (Nombre, Matrícula, Correo o Contraseña).");
            return ResponseEntity.badRequest().body(response);
        }

        // Validación de dominio de correo
        if (!usuario.getEmailInstitucional().endsWith("@utez.edu.mx")) {
            response.put("status", "error");
            response.put("message", "Solo se permiten correos institucionales (@utez.edu.mx).");
            return ResponseEntity.badRequest().body(response);
        }

        // Verificar duplicados
        if (usuarioRepository.existsByEmailInstitucional(usuario.getEmailInstitucional())) {
            response.put("status", "error");
            response.put("message", "Este correo ya está registrado.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            // Valores por defecto
            if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                usuario.setRol("ESTUDIANTE");
            }
            if (usuario.getEstado() == null) {
                usuario.setEstado(true); // Activo por defecto
            }

            usuarioRepository.save(usuario);

            response.put("status", "success");
            response.put("message", "¡Usuario " + usuario.getNombre() + " registrado con éxito!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al guardar en la base de datos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 3. ACTUALIZAR USUARIO EXISTENTE
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        Map<String, Object> response = new HashMap<>();

        return usuarioRepository.findById(id).map(usuarioExistente -> {
            try {
                // Actualización de campos básicos
                usuarioExistente.setNombre(usuarioActualizado.getNombre());
                usuarioExistente.setMatricula(usuarioActualizado.getMatricula());
                usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
                usuarioExistente.setCarrera(usuarioActualizado.getCarrera());
                usuarioExistente.setRol(usuarioActualizado.getRol());
                usuarioExistente.setEmailInstitucional(usuarioActualizado.getEmailInstitucional());

                // Solo actualizar contraseña si el usuario envió una nueva en el modal
                if (usuarioActualizado.getContrasena() != null && !usuarioActualizado.getContrasena().trim().isEmpty()) {
                    usuarioExistente.setContrasena(usuarioActualizado.getContrasena());
                }

                usuarioRepository.save(usuarioExistente);

                response.put("status", "success");
                response.put("message", "¡Datos de " + usuarioExistente.getNombre() + " actualizados!");
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                response.put("status", "error");
                response.put("message", "Error al actualizar: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }).orElseGet(() -> {
            response.put("status", "error");
            response.put("message", "Usuario con ID " + id + " no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }

    // 4. CAMBIAR ESTADO (BLOQUEAR/ACTIVAR)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        return usuarioRepository.findById(id).map(user -> {
            // Si el estado es null, asumimos que estaba activo (true)
            boolean estadoActual = (user.getEstado() != null) ? user.getEstado() : true;
            user.setEstado(!estadoActual);

            usuarioRepository.save(user);

            response.put("status", "success");
            response.put("message", "Estado actualizado: " + (user.getEstado() ? "ACTIVO" : "BLOQUEADO"));
            return ResponseEntity.ok().body(response);
        }).orElseGet(() -> {
            response.put("status", "error");
            response.put("message", "Usuario no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }
}