package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.ReservaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.ReservaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class ReservaService {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private AreaRepository areaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    public Reserva crearReserva(ReservaDTO dto) throws Exception {

        // 1. Verificar que el alumno y la zona realmente existan en la base de datos
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new Exception("Error: El usuario no existe."));

        Area area = areaRepository.findById(dto.getIdArea())
                .orElseThrow(() -> new Exception("Error: La zona deportiva no existe."));

        // 2. Verificar que la zona esté disponible
        if (area.getEstado() != null && !area.getEstado().equalsIgnoreCase("disponible")) {
            throw new Exception("Lo sentimos, esta área se encuentra " + area.getEstado() + " o en mantenimiento.");
        }

        // Convertimos las horas de texto ("14:00") a objetos de tiempo para poder usar matemáticas
        LocalTime inicioNuevo = LocalTime.parse(dto.getHoraInicio());
        LocalTime finNuevo = LocalTime.parse(dto.getHoraFin());
        LocalTime aperturaArea = LocalTime.parse(area.getHoraApertura());
        LocalTime cierreArea = LocalTime.parse(area.getHoraCierre());

        // 3. Reglas de tiempo lógicas y de negocio
        if (inicioNuevo.isAfter(finNuevo) || inicioNuevo.equals(finNuevo)) {
            throw new Exception("La hora de inicio debe ser antes que la hora de fin.");
        }

        if (inicioNuevo.isBefore(aperturaArea) || finNuevo.isAfter(cierreArea)) {
            throw new Exception("El horario está fuera del rango de servicio de esta zona (" + area.getHoraApertura() + " a " + area.getHoraCierre() + ").");
        }

        // 4. EL NÚCLEO: Validar que no choque con otras reservas ese mismo día
        // Traemos todas las reservas de esa cancha, en esa fecha, que NO estén canceladas
        List<Reserva> reservasDelDia = reservaRepository.findByAreaIdAndFechaAndEstadoNot(area.getId(), dto.getFecha(), "CANCELADA");

        for (Reserva existente : reservasDelDia) {
            LocalTime inicioExistente = LocalTime.parse(existente.getHoraInicio());
            LocalTime finExistente = LocalTime.parse(existente.getHoraFin());

            // Fórmula matemática de Traslape de Horarios
            if (inicioNuevo.isBefore(finExistente) && finNuevo.isAfter(inicioExistente)) {
                throw new Exception("¡Horario ocupado! Choca con otra reserva de " + existente.getHoraInicio() + " a " + existente.getHoraFin() + ".");
            }
        }

        // 5. Si superó todas las pruebas, armamos la reserva y la guardamos
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setUsuario(usuario);
        nuevaReserva.setArea(area);
        nuevaReserva.setFecha(dto.getFecha());
        nuevaReserva.setHoraInicio(dto.getHoraInicio());
        nuevaReserva.setHoraFin(dto.getHoraFin());
        nuevaReserva.setDescripcion(dto.getDescripcion());
        nuevaReserva.setEstado("CONFIRMADA");

        return reservaRepository.save(nuevaReserva);
    }

    // --- MÉTODO PARA CANCELAR ---
    public Reserva cancelarReserva(Long id) throws Exception {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: La reserva no existe."));

        if (reserva.getEstado().equalsIgnoreCase("CANCELADA")) {
            throw new Exception("La reserva ya se encontraba cancelada.");
        }

        reserva.setEstado("CANCELADA");
        return reservaRepository.save(reserva);
    }

    // --- MÉTODO PARA ACTUALIZAR ---
    public Reserva actualizarReserva(Long id, ReservaDTO dto) throws Exception {
        Reserva existente = reservaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: La reserva que intentas editar no existe."));

        // Validar que la nueva área exista y esté disponible
        Area area = areaRepository.findById(dto.getIdArea())
                .orElseThrow(() -> new Exception("Error: La zona deportiva no existe."));

        if (area.getEstado() != null && !area.getEstado().equalsIgnoreCase("disponible")) {
            throw new Exception("Lo sentimos, esta área se encuentra " + area.getEstado() + ".");
        }

        // Validaciones de tiempo
        LocalTime inicioNuevo = LocalTime.parse(dto.getHoraInicio());
        LocalTime finNuevo = LocalTime.parse(dto.getHoraFin());
        LocalTime aperturaArea = LocalTime.parse(area.getHoraApertura());
        LocalTime cierreArea = LocalTime.parse(area.getHoraCierre());

        if (inicioNuevo.isAfter(finNuevo) || inicioNuevo.equals(finNuevo)) {
            throw new Exception("La hora de inicio debe ser antes que la hora de fin.");
        }
        if (inicioNuevo.isBefore(aperturaArea) || finNuevo.isAfter(cierreArea)) {
            throw new Exception("El horario está fuera del rango de servicio (" + area.getHoraApertura() + " a " + area.getHoraCierre() + ").");
        }

        // Buscar choques IGNORANDO la reserva que estamos editando (por su ID)
        List<Reserva> conflictos = reservaRepository.findConflictingConfirmadasForUpdate(
                area.getId(),
                dto.getFecha(),
                id,
                dto.getHoraInicio(),
                dto.getHoraFin()
        );

        if (!conflictos.isEmpty()) {
            throw new IllegalArgumentException("El horario seleccionado ya está ocupado para esta área");
        }

        // Si todo está bien, actualizamos los datos
        existente.setArea(area);
        existente.setFecha(dto.getFecha());
        existente.setHoraInicio(dto.getHoraInicio());
        existente.setHoraFin(dto.getHoraFin());
        existente.setDescripcion(dto.getDescripcion());
        // No cambiamos el usuario porque la reserva sigue siendo de él

        return reservaRepository.save(existente);
    }
}