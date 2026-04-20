package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.dto.ReservaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import mx.edu.utez.Integradora5A_SIGRAD.service.ReservaService;
import mx.edu.utez.Integradora5A_SIGRAD.repository.ReservaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.service.PdfExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private PdfExportService pdfExportService;

    @GetMapping("/listar")
    public ResponseEntity<List<Reserva>> listarTodasLasReservas() {
        reservaService.actualizarEstadosVencidos();
        List<Reserva> reservas = reservaRepository.findAll();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/paginadas")
    public ResponseEntity<Page<Reserva>> listarReservasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String termino,
            @RequestParam(defaultValue = "") String estado) {

        reservaService.actualizarEstadosVencidos();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());


        Page<Reserva> reservas = reservaRepository.buscarConPaginacion(termino, estado, pageable);
        return ResponseEntity.ok(reservas);
    }

    // ✅ CORREGIDO: Se eliminó @Transactional de aquí.
    // Se llama actualizarEstadosVencidos() primero (tiene su propio REQUIRES_NEW
    // y hace commit solo). Luego, en una llamada completamente separada, se consultan
    // las reservas. Así Oracle no ve un UPDATE y un SELECT en paralelo sobre la misma
    // tabla, lo que causaba el ORA-12838.
    @PostMapping(value = "/exportar-pdf", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void exportarPDF(@RequestBody Map<String, String> body, HttpServletResponse response) throws IOException {
        if (body == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cuerpo JSON requerido");
            return;
        }
        String fechaInicio = body.get("fechaInicio");
        String fechaFin = body.get("fechaFin");
        if (fechaInicio == null || fechaFin == null || fechaInicio.isBlank() || fechaFin.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "fechaInicio y fechaFin son obligatorias");
            return;
        }

        LocalDate inicio;
        LocalDate fin;
        try {
            inicio = LocalDate.parse(fechaInicio.trim());
            fin = LocalDate.parse(fechaFin.trim());
        } catch (DateTimeParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Usa fechas en formato yyyy-MM-dd");
            return;
        }

        if (inicio.isAfter(fin)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La fecha de inicio no puede ser posterior a la fecha fin");
            return;
        }

        // ✅ Paso 1: actualizar estados — hace su propio commit con REQUIRES_NEW y termina.
        reservaService.actualizarEstadosVencidos();

        // ✅ Paso 2: ahora sí consultamos. Transacción completamente nueva, sin conflicto.
        List<Reserva> listReservas = reservaService.listarReservasParaExportPdf(inicio, fin);

        if (listReservas.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"mensaje\":\"No hay reservas registradas entre las fechas seleccionadas.\"}");
            return;
        }

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"historial_reservas_" + currentDateTime + ".pdf\"");

        pdfExportService.exportReservasToPdf(response, listReservas, inicio.toString(), fin.toString());
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
        reservaService.actualizarEstadosVencidos();
        List<Reserva> misReservas = reservaRepository.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(misReservas);
    }
}