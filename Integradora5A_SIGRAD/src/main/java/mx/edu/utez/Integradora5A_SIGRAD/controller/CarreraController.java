package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Carrera;
import mx.edu.utez.Integradora5A_SIGRAD.service.CarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
@CrossOrigin(origins = "http://localhost:5173")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    @GetMapping("/listar")
    public ResponseEntity<List<Carrera>> listarCarreras() {
        return ResponseEntity.ok(carreraService.listarTodas());
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarCarrera(@RequestBody Carrera carrera) throws Exception {
        Carrera nueva = carreraService.guardar(carrera);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarCarrera(@PathVariable Long id, @RequestBody Carrera carrera) throws Exception {
        return ResponseEntity.ok(carreraService.actualizar(id, carrera));
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(carreraService.cambiarEstado(id));
    }
}