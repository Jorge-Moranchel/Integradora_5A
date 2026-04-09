package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.DashboardDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.ReservaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AreaRepository areaRepository;

    public DashboardDTO obtenerEstadisticas() {
        DashboardDTO dto = new DashboardDTO();

        // 1. Obtener la fecha de hoy
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 2. Traer TODAS las reservas de la base de datos
        List<Reserva> todasLasReservas = reservaRepository.findAll();

        // 👇 SEPARAMOS POR ESTADOS 👇
        List<Reserva> reservasConfirmadas = todasLasReservas.stream()
                .filter(r -> "CONFIRMADA".equalsIgnoreCase(r.getEstado()))
                .collect(Collectors.toList());

        List<Reserva> reservasCompletadas = todasLasReservas.stream()
                .filter(r -> "COMPLETADA".equalsIgnoreCase(r.getEstado()))
                .collect(Collectors.toList());

        List<Reserva> reservasCanceladas = todasLasReservas.stream()
                .filter(r -> "CANCELADA".equalsIgnoreCase(r.getEstado()))
                .collect(Collectors.toList());

        // 👇 CREAMOS UNA LISTA DE RESERVAS "VALIDAS" PARA LAS GRÁFICAS (Confirmadas + Completadas) 👇
        List<Reserva> reservasValidasParaGraficas = new ArrayList<>(reservasConfirmadas);
        reservasValidasParaGraficas.addAll(reservasCompletadas);

        // Filtrar las reservas de HOY (usamos las válidas para saber qué tanto se usa hoy)
        List<Reserva> reservasHoyList = reservasValidasParaGraficas.stream()
                .filter(r -> fechaHoy.equals(r.getFecha()))
                .collect(Collectors.toList());

        // Asignar los conteos a las tarjetas
        dto.setReservasActivas(reservasConfirmadas.size()); // Solo las que están pendientes por ocurrir
        dto.setReservasHoy(reservasHoyList.size());
        dto.setUsuariosRegistrados(usuarioRepository.count());
        dto.setReservasCompletadas(reservasCompletadas.size()); // Nuevos datos
        dto.setReservasCanceladas(reservasCanceladas.size());   // Nuevos datos

        // =========================================================
        // 3. CÁLCULO DINÁMICO DE TASA DE OCUPACIÓN
        // =========================================================
        long totalAreas = areaRepository.count();
        int tasaOcupacion = 0;

        if (totalAreas > 0) {
            Map<Long, Long> reservasPorAreaHoy = reservasHoyList.stream()
                    .filter(r -> r.getArea() != null)
                    .collect(Collectors.groupingBy(r -> r.getArea().getId(), Collectors.counting()));

            long maxReservasEnUnArea = reservasPorAreaHoy.values().stream()
                    .max(Long::compare)
                    .orElse(0L);

            long baseCapacidad = Math.max(5L, maxReservasEnUnArea);
            long capacidadTotalDiaria = totalAreas * baseCapacidad;

            tasaOcupacion = (int) ((reservasHoyList.size() * 100.0f) / capacidadTotalDiaria);
        }

        dto.setTasaOcupacion(Math.min(tasaOcupacion, 100));

        // =========================================================
        // 4. DATOS PARA LAS GRÁFICAS (USANDO RESERVAS VÁLIDAS)
        // =========================================================

        // Gráfica de Dona: Reservas totales agrupadas por Nombre del Área
        Map<String, Long> porArea = reservasValidasParaGraficas.stream()
                .filter(r -> r.getArea() != null && r.getArea().getNombre() != null)
                .collect(Collectors.groupingBy(r -> r.getArea().getNombre(), Collectors.counting()));
        dto.setReservasPorArea(porArea);

        // Gráfica de Barras: Reservas Mensuales
        Map<String, Long> porMes = reservasValidasParaGraficas.stream()
                .filter(r -> r.getFecha() != null && r.getFecha().length() >= 7)
                .collect(Collectors.groupingBy(
                        r -> r.getFecha().substring(0, 7),
                        TreeMap::new,
                        Collectors.counting()
                ));
        dto.setReservasPorMes(porMes);

        return dto;
    }
}