package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.DTO.ReservaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import mx.edu.utez.Integradora5A_SIGRAD.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(originPatterns = "*")
    public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaDTO reservaDTO) {
        try {
            // Mandamos el DTO al Service para que haga toda la validación matemática
            Reserva nuevaReserva = reservaService.crearReserva(reservaDTO);

            // Si todo sale bien, devolvemos la reserva creada con un código 200 OK
            return ResponseEntity.ok("¡Reserva creada con éxito! ID asignado: " + nuevaReserva.getId());

        } catch (Exception e) {
            // Si el Service lanza un error (choca horario, área bloqueada, etc.),
            // atrapamos el mensaje y se lo mandamos a la app móvil con un código 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            reservaService.cancelarReserva(id);
            response.put("exito", true);
            response.put("mensaje", "¡Reserva cancelada correctamente!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("exito", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarReserva(@PathVariable Long id, @RequestBody ReservaDTO reservaDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Reserva actualizada = reservaService.actualizarReserva(id, reservaDTO);
            response.put("exito", true);
            response.put("mensaje", "¡Reserva actualizada correctamente!");
            response.put("idReserva", actualizada.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("exito", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}