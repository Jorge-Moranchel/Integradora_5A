package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    // ✅ NUEVO MÉTODO: Libera automáticamente las áreas si su fecha de fin de bloqueo ya pasó
    private void liberarAreasBloqueadasVencidas() {
        List<Area> areasBloqueadas = areaRepository.findByEstado("bloqueada");
        boolean hayCambios = false;
        LocalDate hoy = LocalDate.now();

        for (Area area : areasBloqueadas) {
            if (area.getFechaFinBloqueo() != null) {
                try {
                    LocalDate fechaFin = LocalDate.parse(area.getFechaFinBloqueo());
                    // Si el día de hoy ya es DESPUÉS del día que terminaba el bloqueo...
                    if (hoy.isAfter(fechaFin)) {
                        area.setEstado("disponible");
                        area.setMotivoBloqueo(null);
                        area.setFechaInicioBloqueo(null);
                        area.setFechaFinBloqueo(null);
                        hayCambios = true;
                    }
                } catch (Exception e) {
                    // Ignorar si la fecha tiene un formato raro
                }
            }
        }

        if (hayCambios) {
            areaRepository.saveAll(areasBloqueadas);
        }
    }

    // Lógica para Módulo 1.1: Registrar Área
    public Area registrarArea(AreaDTO dto) throws Exception {
        if (areaRepository.existsByNombre(dto.getNombre())) {
            throw new Exception("Error: El nombre de la zona ya está registrado");
        }
        if (dto.getHoraApertura().compareTo(dto.getHoraCierre()) >= 0) {
            throw new Exception("Error: La hora de apertura no puede ser mayor o igual a la hora de cierre");
        }

        Area nuevaArea = new Area();
        nuevaArea.setNombre(dto.getNombre());
        nuevaArea.setUbicacion(dto.getUbicacion());
        nuevaArea.setHoraApertura(dto.getHoraApertura());
        nuevaArea.setHoraCierre(dto.getHoraCierre());
        nuevaArea.setImagen(dto.getImagen());
        nuevaArea.setTipo(dto.getTipo());
        nuevaArea.setEstado("disponible");

        return areaRepository.save(nuevaArea);
    }

    // Lógica para Módulo 1.2: Consultar Áreas
    // Lógica para Módulo 1.2: Consultar Áreas
    public List<Area> obtenerTodasLasAreas() {
        liberarAreasBloqueadasVencidas();

        // ✅ AHORA USAMOS LA CONSULTA OPTIMIZADA
        // Esto evita que la BD descargue gigabytes de texto a la RAM del servidor
        return areaRepository.findAllSinImagen();
    }

    // Lógica para Módulo 1.4: Bloquear Área
    public Area bloquearArea(Long id, mx.edu.utez.Integradora5A_SIGRAD.dto.BloqueoDTO bloqueoDTO) throws Exception {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: Área no encontrada"));

        // ✅ VALIDACIÓN: Evitar que pongan bloqueos en el pasado
        LocalDate inicio = LocalDate.parse(bloqueoDTO.getFechaInicioBloqueo());
        LocalDate fin = LocalDate.parse(bloqueoDTO.getFechaFinBloqueo());

        if (inicio.isBefore(LocalDate.now())) {
            throw new Exception("Error: No puedes iniciar un mantenimiento en una fecha que ya pasó.");
        }
        if (fin.isBefore(inicio)) {
            throw new Exception("Error: La fecha de fin no puede ser anterior a la de inicio.");
        }

        area.setEstado("bloqueada");
        area.setMotivoBloqueo(bloqueoDTO.getMotivoBloqueo());
        area.setFechaInicioBloqueo(bloqueoDTO.getFechaInicioBloqueo());
        area.setFechaFinBloqueo(bloqueoDTO.getFechaFinBloqueo());

        return areaRepository.save(area);
    }

    // Lógica para Módulo 1.4: Desbloquear Área Manualmente
    public Area desbloquearArea(Long id) throws Exception {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: Área no encontrada"));

        area.setEstado("disponible");
        area.setMotivoBloqueo(null);
        area.setFechaInicioBloqueo(null);
        area.setFechaFinBloqueo(null);

        return areaRepository.save(area);
    }

    // Lógica para Módulo 1.3: Actualizar Área
    public Area actualizarArea(Long id, mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO dto) throws Exception {
        Area areaActual = areaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: Área no encontrada"));

        if (!areaActual.getNombre().equalsIgnoreCase(dto.getNombre()) && areaRepository.existsByNombre(dto.getNombre())) {
            throw new Exception("Error: El nombre de la zona ya está ocupado");
        }
        if (dto.getHoraApertura().compareTo(dto.getHoraCierre()) >= 0) {
            throw new Exception("Error: La hora de apertura no puede ser mayor o igual a la hora de cierre");
        }

        areaActual.setNombre(dto.getNombre());
        areaActual.setUbicacion(dto.getUbicacion());
        areaActual.setHoraApertura(dto.getHoraApertura());
        areaActual.setHoraCierre(dto.getHoraCierre());
        areaActual.setTipo(dto.getTipo());

        if (dto.getImagen() != null && !dto.getImagen().isEmpty()) {
            areaActual.setImagen(dto.getImagen());
        }

        return areaRepository.save(areaActual);
    }

    public Area obtenerAreaPorId(Long id) {
        return areaRepository.findById(id).orElse(null);
    }
}