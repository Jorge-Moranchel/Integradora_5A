package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    // Lógica para Módulo 1.1: Registrar Área
    public Area registrarArea(AreaDTO dto) throws Exception {
        // Regla de negocio: Nombre único
        if (areaRepository.existsByNombre(dto.getNombre())) {
            throw new Exception("Error: El nombre de la zona ya está registrado");
        }

        // Regla de negocio: Apertura menor a cierre
        if (dto.getHoraApertura().compareTo(dto.getHoraCierre()) >= 0) {
            throw new Exception("Error: La hora de apertura no puede ser mayor o igual a la hora de cierre");
        }

        // Pasamos los datos del DTO a la Entidad real
        Area nuevaArea = new Area();
        nuevaArea.setNombre(dto.getNombre());
        nuevaArea.setUbicacion(dto.getUbicacion());
        nuevaArea.setHoraApertura(dto.getHoraApertura());
        nuevaArea.setHoraCierre(dto.getHoraCierre());
        nuevaArea.setImagen(dto.getImagen());
        nuevaArea.setEstado("disponible"); // Regla del DFR: Estado inicial por defecto

        return areaRepository.save(nuevaArea);
    }

    // Lógica para Módulo 1.2: Consultar Áreas
    public List<Area> obtenerTodasLasAreas() {
        return areaRepository.findAll();
    }

    // Lógica para Módulo 1.4: Bloquear Área
    public Area bloquearArea(Long id, mx.edu.utez.Integradora5A_SIGRAD.dto.BloqueoDTO bloqueoDTO) throws Exception {
        // Buscamos el área en la BD
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: Área no encontrada"));

        // Aplicamos las reglas de negocio del DFR
        area.setEstado("bloqueada");
        area.setMotivoBloqueo(bloqueoDTO.getMotivoBloqueo());
        area.setFechaInicioBloqueo(bloqueoDTO.getFechaInicioBloqueo());
        area.setFechaFinBloqueo(bloqueoDTO.getFechaFinBloqueo());

        return areaRepository.save(area);
    }

    // Lógica para Módulo 1.4: Desbloquear Área
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

        // Regla del DFR: Validar que el nuevo nombre no esté duplicado (a menos que sea su propio nombre)
        if (!areaActual.getNombre().equalsIgnoreCase(dto.getNombre()) && areaRepository.existsByNombre(dto.getNombre())) {
            throw new Exception("Error: El nombre de la zona ya está ocupado");
        }

        // Regla del DFR: Apertura menor a cierre
        if (dto.getHoraApertura().compareTo(dto.getHoraCierre()) >= 0) {
            throw new Exception("Error: La hora de apertura no puede ser mayor o igual a la hora de cierre");
        }

        // Actualizamos los datos
        areaActual.setNombre(dto.getNombre());
        areaActual.setUbicacion(dto.getUbicacion());
        areaActual.setHoraApertura(dto.getHoraApertura());
        areaActual.setHoraCierre(dto.getHoraCierre());

        // Solo reemplazamos la imagen si el administrador subió una nueva
        if (dto.getImagen() != null && !dto.getImagen().isEmpty()) {
            areaActual.setImagen(dto.getImagen());
        }

        return areaRepository.save(areaActual);
    }
}