package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Division;
import mx.edu.utez.Integradora5A_SIGRAD.service.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisiones")
@CrossOrigin(origins = "http://localhost:5173")
public class DivisionController {

    @Autowired
    private DivisionService divisionService;

    @GetMapping("/listar")
    public ResponseEntity<List<Division>> listar() {
        return ResponseEntity.ok(divisionService.listar());
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@RequestBody Division division) {
        Division nueva = divisionService.guardar(division);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Division division) {
        Division actualizada = divisionService.actualizar(id, division);
        if (actualizada != null) {
            return ResponseEntity.ok(actualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        boolean ok = divisionService.cambiarEstado(id);
        if (ok) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}