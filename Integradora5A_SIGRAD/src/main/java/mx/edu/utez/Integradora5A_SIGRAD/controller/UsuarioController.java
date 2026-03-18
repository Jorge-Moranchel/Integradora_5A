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

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        Map<String, Object> response = new HashMap<>();

        // 1. Validaciones de campos obligatorios según tu Modal
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
                usuario.getMatricula() == null || usuario.getMatricula().trim().isEmpty() ||
                usuario.getEmailInstitucional() == null || usuario.getEmailInstitucional().trim().isEmpty() ||
                usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {

            response.put("status", "error");
            response.put("message", "Faltan datos obligatorios. Revisa Nombre, Matrícula, Correo y Contraseña.");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. Validación de correo institucional (que termine en @utez.edu.mx)
        if (!usuario.getEmailInstitucional().endsWith("@utez.edu.mx")) {
            response.put("status", "error");
            response.put("message", "Solo se permiten correos de la UTEZ (@utez.edu.mx).");
            return ResponseEntity.badRequest().body(response);
        }

        // 3. Verificar si el correo ya existe en la base de datos de Oracle
        if (usuarioRepository.existsByEmailInstitucional(usuario.getEmailInstitucional())) {
            response.put("status", "error");
            response.put("message", "Este correo ya está registrado en el sistema.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            // 4. Asignar rol por defecto si no viene en el JSON
            if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                usuario.setRol("Estudiante");
            }

            // 5. Guardar en Oracle Cloud
            usuarioRepository.save(usuario);

            response.put("status", "success");
            response.put("message", "¡Usuario " + usuario.getNombre() + " registrado con éxito!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Fallo al conectar con Oracle: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint extra por si necesitas listar a todos en una tabla después
    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(user -> {
            // Validación anti-error: Si el estado es nulo, lo ponemos en true primero
            boolean estadoActual = (user.getEstado() != null) ? user.getEstado() : true;

            user.setEstado(!estadoActual); // Ahora sí, invertimos el valor con seguridad
            usuarioRepository.save(user);
            return ResponseEntity.ok().body("Estado actualizado con éxito");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        Map<String, Object> response = new HashMap<>();

        return usuarioRepository.findById(id).map(usuarioExistente -> {
            // Actualizar los datos permitidos
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setMatricula(usuarioActualizado.getMatricula());
            usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
            usuarioExistente.setCarrera(usuarioActualizado.getCarrera());
            usuarioExistente.setRol(usuarioActualizado.getRol());
            usuarioExistente.setEmailInstitucional(usuarioActualizado.getEmailInstitucional());

            // Solo actualizar contraseña si el usuario escribió una nueva
            if (usuarioActualizado.getContrasena() != null && !usuarioActualizado.getContrasena().trim().isEmpty()) {
                usuarioExistente.setContrasena(usuarioActualizado.getContrasena());
            }

            usuarioRepository.save(usuarioExistente);

            response.put("status", "success");
            response.put("message", "Usuario actualizado correctamente");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("status", "error");
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }
}