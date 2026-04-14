package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.DashboardDTO;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.ReservaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 1. Tarjetas Superiores (Consultas de conteo directas y rápidas)
        dto.setReservasActivas((int) reservaRepository.countByEstadoIgnoreCase("CONFIRMADA"));
        dto.setReservasCompletadas((int) reservaRepository.countByEstadoIgnoreCase("COMPLETADA"));
        dto.setReservasCanceladas((int) reservaRepository.countByEstadoIgnoreCase("CANCELADA"));
        dto.setUsuariosRegistrados(usuarioRepository.count());

        long totalReservasHoy = reservaRepository.countReservasValidasPorFecha(fechaHoy);
        dto.setReservasHoy((int) totalReservasHoy);

        // 2. Tasa de Ocupación Dinámica
        long totalAreas = areaRepository.count();
        int tasaOcupacion = 0;

        if (totalAreas > 0 && totalReservasHoy > 0) {
            List<Object[]> reservasPorAreaHoy = reservaRepository.countReservasValidasPorAreaYFecha(fechaHoy);

            long maxReservasEnUnArea = 0;
            for (Object[] result : reservasPorAreaHoy) {
                long count = ((Number) result[1]).longValue();
                if (count > maxReservasEnUnArea) {
                    maxReservasEnUnArea = count;
                }
            }

            long baseCapacidad = Math.max(5L, maxReservasEnUnArea);
            long capacidadTotalDiaria = totalAreas * baseCapacidad;

            tasaOcupacion = (int) ((totalReservasHoy * 100.0f) / capacidadTotalDiaria);
        }
        dto.setTasaOcupacion(Math.min(tasaOcupacion, 100));

        // 3. Gráfica de Dona (Por Área)
        List<Object[]> reservasPorNombreArea = reservaRepository.countReservasValidasPorNombreArea();
        Map<String, Long> porArea = new HashMap<>();
        for (Object[] result : reservasPorNombreArea) {
            String nombreArea = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            porArea.put(nombreArea, count);
        }
        dto.setReservasPorArea(porArea);

        // 4. Gráfica de Barras (Por Mes)
        List<Object[]> reservasPorMes = reservaRepository.countReservasValidasPorMes();
        Map<String, Long> porMes = new TreeMap<>(); // TreeMap para que se ordene por fecha automáticamente
        for (Object[] result : reservasPorMes) {
            String mes = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            porMes.put(mes, count);
        }
        dto.setReservasPorMes(porMes);

        return dto;
    }
}