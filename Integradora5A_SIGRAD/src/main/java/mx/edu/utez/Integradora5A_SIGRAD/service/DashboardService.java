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

        // 1. Obtener la fecha de hoy en el formato exacto de tu BD (Ej. "2026-03-25")
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 2. Traer todas las reservas para procesarlas en memoria (ultra rápido)
        List<Reserva> todasLasReservas = reservaRepository.findAll();

        // Filtrar solo las reservas que están CONFIRMADAS
        List<Reserva> reservasConfirmadas = todasLasReservas.stream()
                .filter(r -> "CONFIRMADA".equalsIgnoreCase(r.getEstado()))
                .collect(Collectors.toList());

        // Filtrar las reservas confirmadas que son específicamente para HOY
        List<Reserva> reservasHoyList = reservasConfirmadas.stream()
                .filter(r -> fechaHoy.equals(r.getFecha()))
                .collect(Collectors.toList());

        // Asignar los conteos básicos a las tarjetas del dashboard
        dto.setReservasActivas(reservasConfirmadas.size());
        dto.setReservasHoy(reservasHoyList.size());
        dto.setUsuariosRegistrados(usuarioRepository.count());

        // =========================================================
        // 3. CÁLCULO DINÁMICO DE TASA DE OCUPACIÓN (Lógica Avanzada)
        // =========================================================
        long totalAreas = areaRepository.count();
        int tasaOcupacion = 0;

        if (totalAreas > 0) {
            // Agrupamos las reservas de HOY por área para ver cuál es la más ocupada
            Map<Long, Long> reservasPorAreaHoy = reservasHoyList.stream()
                    .filter(r -> r.getArea() != null)
                    .collect(Collectors.groupingBy(r -> r.getArea().getId(), Collectors.counting()));

            // Encontramos el área con el MAYOR número de reservas hoy
            long maxReservasEnUnArea = reservasPorAreaHoy.values().stream()
                    .max(Long::compare)
                    .orElse(0L);

            // Regla de negocio: El tope es dinámico basado en el área más popular, pero nunca menor a 5
            long baseCapacidad = Math.max(5L, maxReservasEnUnArea);
            long capacidadTotalDiaria = totalAreas * baseCapacidad;

            // Calculamos el porcentaje
            tasaOcupacion = (int) ((reservasHoyList.size() * 100.0f) / capacidadTotalDiaria);
        }

        // Aseguramos que el porcentaje no pase del 100% por si hay algún dato atípico
        dto.setTasaOcupacion(Math.min(tasaOcupacion, 100));

        // =========================================================
        // 4. DATOS PARA LAS GRÁFICAS DE JORGE (Agrupados y Ordenados)
        // =========================================================

        // Gráfica de Dona: Reservas totales agrupadas por Nombre del Área
        Map<String, Long> porArea = reservasConfirmadas.stream()
                .filter(r -> r.getArea() != null && r.getArea().getNombre() != null)
                .collect(Collectors.groupingBy(r -> r.getArea().getNombre(), Collectors.counting()));
        dto.setReservasPorArea(porArea);

        // Gráfica de Barras: Reservas Mensuales
        // Extraemos solo el "YYYY-MM" (ej. "2026-03") de la fecha y usamos TreeMap para que
        // los meses se ordenen cronológicamente de forma automática.
        Map<String, Long> porMes = reservasConfirmadas.stream()
                .filter(r -> r.getFecha() != null && r.getFecha().length() >= 7)
                .collect(Collectors.groupingBy(
                        r -> r.getFecha().substring(0, 7),
                        TreeMap::new, // Magia: Ordena los meses automáticamente de enero a diciembre
                        Collectors.counting()
                ));
        dto.setReservasPorMes(porMes);

        return dto;
    }
}