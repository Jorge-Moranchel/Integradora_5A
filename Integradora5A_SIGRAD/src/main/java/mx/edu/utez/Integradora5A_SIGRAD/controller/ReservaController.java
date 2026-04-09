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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private PdfExportService pdfExportService;

    @GetMapping("/listar")
    public ResponseEntity<List<Reserva>> listarTodasLasReservas() {
        // 👇 Disparamos la limpieza de estados antes de entregar datos 👇
        reservaService.actualizarEstadosVencidos();
        List<Reserva> reservas = reservaRepository.findAll();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/paginadas")
    public ResponseEntity<Page<Reserva>> listarReservasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String termino) {

        // 👇 Disparamos la limpieza de estados antes de entregar datos 👇
        reservaService.actualizarEstadosVencidos();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Reserva> reservas = reservaRepository.buscarConPaginacion(termino, pageable);

        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/exportar-pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException {
        reservaService.actualizarEstadosVencidos(); // <-- Actualizamos antes de exportar
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=historial_reservas_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Reserva> listReservas = reservaRepository.findAll();
        pdfExportService.exportReservasToPdf(response, listReservas);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaDTO reservaDTO) throws Exception {
        Reserva nuevaReserva = reservaService.crearReserva(reservaDTO);
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

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Reserva>> listarReservasPorUsuario(@PathVariable Long idUsuario) {
        // 👇 Disparamos la limpieza de estados antes de entregar datos 👇
        reservaService.actualizarEstadosVencidos();
        List<Reserva> misReservas = reservaRepository.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(misReservas);
    }
}