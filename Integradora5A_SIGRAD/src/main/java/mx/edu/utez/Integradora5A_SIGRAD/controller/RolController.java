package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Rol;
import mx.edu.utez.Integradora5A_SIGRAD.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:5173")// Permite peticiones desde tu frontend
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public List<Rol> obtenerRoles() {
        return rolService.listarRoles();
    }

    // MÉTODO PARA GUARDAR NUEVOS ROLES
    @PostMapping
    public ResponseEntity<Rol> guardarRol(@RequestBody Rol rol) {
        // Forzamos que el estado inicial sea true si viene nulo
        if (rol.getActivo() == null) rol.setActivo(true);
        Rol nuevoRol = rolService.guardar(rol);
        return ResponseEntity.ok(nuevoRol);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        rolService.cambiarEstado(id);
        return ResponseEntity.ok().build();
    }
}