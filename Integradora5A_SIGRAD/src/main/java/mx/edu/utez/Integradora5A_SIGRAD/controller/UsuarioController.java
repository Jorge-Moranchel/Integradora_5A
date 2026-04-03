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

    // Este endpoint es GET porque los clics en enlaces de correo siempre hacen peticiones GET
    @GetMapping("/verificar")
    public ResponseEntity<String> verificarCuentaUrl(@RequestParam("token") String token) {
        // Buscamos si existe algún usuario que tenga ese token guardado
        // OJO: Para hacer esto necesitas agregar un método en tu UsuarioRepository:
        // Optional<Usuario> findByCodigoVerificacion(String codigoVerificacion);

        Usuario usuario = usuarioRepository.findByCodigoVerificacion(token)
                .orElseThrow(() -> new IllegalArgumentException("Enlace inválido o expirado."));

        // Si lo encuentra, lo validamos y borramos el token para que no se reuse
        usuario.setValidado(true);
        usuario.setCodigoVerificacion(null); // Limpiamos el token
        usuarioRepository.save(usuario);

        // Devolvemos un mensaje en HTML súper básico para que se vea bien en el navegador
        String htmlResponse = "<html><body style='font-family: Arial; text-align: center; margin-top: 50px;'>"
                + "<h2 style='color: #198754;'>¡Cuenta Verificada con Éxito!</h2>"
                + "<p>Tu cuenta de SIGRAD ya está activa. Ya puedes cerrar esta pestaña y regresar a la aplicación para iniciar sesión.</p>"
                + "</body></html>";

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(htmlResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario user = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        return ResponseEntity.ok(user);
    }
}