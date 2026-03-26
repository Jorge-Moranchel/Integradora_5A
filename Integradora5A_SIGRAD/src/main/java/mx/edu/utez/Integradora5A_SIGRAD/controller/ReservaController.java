package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.dto.ReservaDTO;
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
    public ResponseEntity<?> crearReserva(@RequestBody ReservaDTO reservaDTO) throws Exception {
        // Mandamos el DTO al Service para que haga toda la validación matemática
        Reserva nuevaReserva = reservaService.crearReserva(reservaDTO);

        // Si todo sale bien, devolvemos la reserva creada con un código 200 OK
        return ResponseEntity.ok("¡Reserva creada con éxito! ID asignado: " + nuevaReserva.getId());
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) throws Exception {
        Map<String, Object> response = new HashMap<>();
        reservaService.cancelarReserva(id);
        response.put("exito", true);
        response.put("mensaje", "¡Reserva cancelada correctamente!");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarReserva(@PathVariable Long id, @RequestBody ReservaDTO reservaDTO) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Reserva actualizada = reservaService.actualizarReserva(id, reservaDTO);
        response.put("exito", true);
        response.put("mensaje", "¡Reserva actualizada correctamente!");
        response.put("idReserva", actualizada.getId());
        return ResponseEntity.ok(response);
    }

    // NUEVO ENDPOINT PARA LA APP MÓVIL: Trae solo las reservas de un usuario específico
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Reserva>> listarReservasPorUsuario(@PathVariable Long idUsuario) {
        List<Reserva> misReservas = reservaRepository.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(misReservas);
    }
}