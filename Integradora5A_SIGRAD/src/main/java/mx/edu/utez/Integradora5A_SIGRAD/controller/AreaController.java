package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.CacheControl; // ✅ IMPORTACIÓN PARA CACHÉ
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit; // ✅ IMPORTACIÓN PARA TIEMPO

@RestController
@RequestMapping("/api/areas")
@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AreaController {

    @Autowired
    private AreaService areaService;

    // Endpoint para Módulo 1.1 (POST)
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarArea(@RequestBody AreaDTO areaDTO) throws Exception {
        Area areaGuardada = areaService.registrarArea(areaDTO);
        return ResponseEntity.ok(areaGuardada);
    }

    // Endpoint para Módulo 1.2 (GET)
    @GetMapping("/listar")
    public ResponseEntity<List<Area>> listarAreas() {
        List<Area> areas = areaService.obtenerTodasLasAreas();

        // TRUCO DE OPTIMIZACIÓN EXTREMA
        // Vaciamos el Base64 para que el JSON de las áreas viaje súper ligero
        areas.forEach(area -> area.setImagen(null));

        return ResponseEntity.ok(areas);
    }

    // Endpoint para Módulo 1.4 (PUT)
    @PutMapping("/bloquear/{id}")
    public ResponseEntity<?> bloquearArea(
            @PathVariable Long id,
            @RequestBody mx.edu.utez.Integradora5A_SIGRAD.dto.BloqueoDTO bloqueoDTO
    ) throws Exception {
        Area areaBloqueada = areaService.bloquearArea(id, bloqueoDTO);
        return ResponseEntity.ok(areaBloqueada);
    }

    // Endpoint para Módulo 1.4 (PUT) - Desbloquear
    @PutMapping("/desbloquear/{id}")
    public ResponseEntity<?> desbloquearArea(@PathVariable Long id) throws Exception {
        Area areaDesbloqueada = areaService.desbloquearArea(id);
        return ResponseEntity.ok(areaDesbloqueada);
    }

    // Endpoint para Módulo 1.3 (PUT) - Actualizar
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarArea(@PathVariable Long id, @RequestBody AreaDTO areaDTO) throws Exception {
        Area areaActualizada = areaService.actualizarArea(id, areaDTO);
        return ResponseEntity.ok(areaActualizada);
    }

    // ✅ ENDPOINT DE IMÁGENES CORREGIDO Y OPTIMIZADO
    @GetMapping(value = "/{id}/imagen", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> obtenerImagenArea(@PathVariable Long id) {
        try {
            Area area = areaService.obtenerAreaPorId(id);

            if (area != null && area.getImagen() != null && !area.getImagen().isEmpty()) {
                String base64Image = area.getImagen();

                // Quitamos la cabecera de HTML si la trae
                if (base64Image.contains(",")) {
                    base64Image = base64Image.split(",")[1];
                }

                // 🔴 SOLUCIÓN 1: Limpiar cualquier espacio o salto de línea invisible
                base64Image = base64Image.replaceAll("\\s+", "");

                // Decodificamos de forma segura
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                // 🔴 SOLUCIÓN 2: Instruir a la App Móvil que guarde la imagen en memoria por 30 días
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                        .body(imageBytes);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Esto te mostrará en consola si llega a haber otro error
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }
}