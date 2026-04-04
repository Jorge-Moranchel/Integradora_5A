package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Rol;
import mx.edu.utez.Integradora5A_SIGRAD.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:5173")
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
        if (rol.getActivo() == null) rol.setActivo(true);
        Rol nuevoRol = rolService.guardar(rol);
        return ResponseEntity.ok(nuevoRol);
    }

    // 👇 NUEVO MÉTODO PARA EDITAR ROLES EXISTENTES 👇
    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizarRol(@PathVariable Long id, @RequestBody Rol rol) {
        Rol rolActualizado = rolService.actualizar(id, rol);
        return ResponseEntity.ok(rolActualizado);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        rolService.cambiarEstado(id);
        return ResponseEntity.ok().build();
    }
}