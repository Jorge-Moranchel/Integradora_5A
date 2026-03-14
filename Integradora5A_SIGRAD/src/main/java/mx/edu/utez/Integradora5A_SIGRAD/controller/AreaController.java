package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    @Autowired
    private AreaRepository areaRepository;

    @GetMapping("/")
    public List<Area> listarAreas() {
        return areaRepository.findAll();
    }

    @PostMapping("/crear")
    public ResponseEntity<Area> crearArea(@RequestBody Area area) {
        Area nuevaArea = areaRepository.save(area);
        return ResponseEntity.ok(nuevaArea);
    }
}