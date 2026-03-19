package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.DTO.ReservaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import mx.edu.utez.Integradora5A_SIGRAD.service.ReservaService;
import mx.edu.utez.Integradora5A_SIGRAD.repository.ReservaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.service.PdfExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(originPatterns = "*")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    // Inyectamos el repositorio y el servicio de PDF para los nuevos endpoints
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PdfExportService pdfExportService;

    // ==========================================
    // NUEVOS ENDPOINTS PARA FRONTEND E HISTORIAL
    // ==========================================

    @GetMapping("/listar")
    public ResponseEntity<List<Reserva>> listarTodasLasReservas() {
        // Traemos todas las reservas de la base de datos para mostrarlas en la tabla de React
        List<Reserva> reservas = reservaRepository.findAll();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/exportar-pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=historial_reservas_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        // Obtenemos todas las reservas y generamos el PDF
        List<Reserva> listReservas = reservaRepository.findAll();
        pdfExportService.exportReservasToPdf(response, listReservas);
    }

    // ==========================================
    // TUS ENDPOINTS ORIGINALES (LÓGICA DE NEGOCIO)
    // ==========================================

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaDTO reservaDTO) {
        try {
            // Mandamos el DTO al Service para que haga toda la validación matemática
            Reserva nuevaReserva = reservaService.crearReserva(reservaDTO);

            // Si todo sale bien, devolvemos la reserva creada con un código 200 OK
            return ResponseEntity.ok("¡Reserva creada con éxito! ID asignado: " + nuevaReserva.getId());

        } catch (Exception e) {
            // Si el Service lanza un error (choca horario, área bloqueada, etc.)
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