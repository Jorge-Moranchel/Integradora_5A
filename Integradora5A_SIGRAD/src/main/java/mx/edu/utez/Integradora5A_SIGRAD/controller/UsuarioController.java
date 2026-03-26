package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.dto.UsuarioDTO;
import mx.edu.utez.Integradora5A_SIGRAD.exception.ResourceNotFoundException;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import mx.edu.utez.Integradora5A_SIGRAD.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UsuarioService usuarioService;

    // 1. LISTAR USUARIOS
    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // 2. REGISTRAR NUEVO USUARIO
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioDTO usuario) {
        Usuario guardado = usuarioService.registrarUsuario(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "¡Usuario " + guardado.getNombre() + " registrado con éxito!");
        return ResponseEntity.ok(response);
    }

    // 3. ACTUALIZAR USUARIO EXISTENTE
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado."));

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

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "¡Datos de " + usuarioExistente.getNombre() + " actualizados!");
        return ResponseEntity.ok(response);
    }

    // 4. CAMBIAR ESTADO (BLOQUEAR/ACTIVAR)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        Usuario user = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        // Si el estado es null, asumimos que estaba activo (true)
        boolean estadoActual = (user.getEstado() != null) ? user.getEstado() : true;
        user.setEstado(!estadoActual);

        usuarioRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Estado actualizado: " + (user.getEstado() ? "ACTIVO" : "BLOQUEADO"));
        return ResponseEntity.ok(response);
    }
}