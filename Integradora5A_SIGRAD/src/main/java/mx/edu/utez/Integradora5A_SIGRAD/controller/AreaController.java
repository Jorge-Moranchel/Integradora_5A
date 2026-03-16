package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AreaController {

    @Autowired
    private AreaService areaService;

    // Endpoint para Módulo 1.1 (POST)
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarArea(@RequestBody AreaDTO areaDTO) {
        try {
            Area areaGuardada = areaService.registrarArea(areaDTO);
            return ResponseEntity.ok(areaGuardada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para Módulo 1.2 (GET)
    @GetMapping("/listar")
    public ResponseEntity<List<Area>> listarAreas() {
        List<Area> areas = areaService.obtenerTodasLasAreas();
        return ResponseEntity.ok(areas);
    }

    // Endpoint para Módulo 1.4 (PUT)
    @PutMapping("/bloquear/{id}")
    public ResponseEntity<?> bloquearArea(@PathVariable Long id, @RequestBody mx.edu.utez.Integradora5A_SIGRAD.dto.BloqueoDTO bloqueoDTO) {
        try {
            Area areaBloqueada = areaService.bloquearArea(id, bloqueoDTO);
            return ResponseEntity.ok(areaBloqueada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para Módulo 1.4 (PUT) - Desbloquear
    @PutMapping("/desbloquear/{id}")
    public ResponseEntity<?> desbloquearArea(@PathVariable Long id) {
        try {
            Area areaDesbloqueada = areaService.desbloquearArea(id);
            return ResponseEntity.ok(areaDesbloqueada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para Módulo 1.3 (PUT) - Actualizar
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarArea(@PathVariable Long id, @RequestBody AreaDTO areaDTO) {
        try {
            Area areaActualizada = areaService.actualizarArea(id, areaDTO);
            return ResponseEntity.ok(areaActualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}