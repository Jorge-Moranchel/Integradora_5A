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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class ReservaService {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private AreaRepository areaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // 👇 MÉTODO AUXILIAR PARA REVISAR SI YA PASÓ LA HORA 👇
    private boolean isReservaPasada(String fechaStr, String horaFinStr) {
        try {
            LocalDate fecha = LocalDate.parse(fechaStr);
            LocalTime horaFin = LocalTime.parse(horaFinStr);
            LocalDateTime dateTimeFin = LocalDateTime.of(fecha, horaFin);
            return LocalDateTime.now().isAfter(dateTimeFin);
        } catch (Exception e) {
            return false;
        }
    }

    //  Convierte las CONFIRMADAS a COMPLETADAS si ya pasó el tiempo 👇
    public void actualizarEstadosVencidos() {
        List<Reserva> confirmadas = reservaRepository.findByEstado("CONFIRMADA");
        boolean hayCambios = false;
        for (Reserva r : confirmadas) {
            if (isReservaPasada(r.getFecha(), r.getHoraFin())) {
                r.setEstado("COMPLETADA");
                hayCambios = true;
            }
        }
        if (hayCambios) {
            reservaRepository.saveAll(confirmadas);
        }
    }

    public Reserva crearReserva(ReservaDTO dto) throws Exception {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new Exception("Error: El usuario no existe."));
        Area area = areaRepository.findById(dto.getIdArea())
                .orElseThrow(() -> new Exception("Error: La zona deportiva no existe."));

        if (area.getEstado() != null && !area.getEstado().equalsIgnoreCase("disponible")) {
            throw new Exception("Lo sentimos, esta área se encuentra " + area.getEstado() + " o en mantenimiento.");
        }

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

        List<Reserva> reservasDelDia = reservaRepository.findByAreaIdAndFechaAndEstadoNot(area.getId(), dto.getFecha(), "CANCELADA");
        for (Reserva existente : reservasDelDia) {
            LocalTime inicioExistente = LocalTime.parse(existente.getHoraInicio());
            LocalTime finExistente = LocalTime.parse(existente.getHoraFin());
            if (inicioNuevo.isBefore(finExistente) && finNuevo.isAfter(inicioExistente)) {
                throw new Exception("¡Horario ocupado! Choca con otra reserva de " + existente.getHoraInicio() + " a " + existente.getHoraFin() + ".");
            }
        }

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

    public Reserva cancelarReserva(Long id) throws Exception {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: La reserva no existe."));

        // Validar si justo acaba de vencer
        if (reserva.getEstado().equals("CONFIRMADA") && isReservaPasada(reserva.getFecha(), reserva.getHoraFin())) {
            reserva.setEstado("COMPLETADA");
            reservaRepository.save(reserva);
        }

        // 👇 BLOQUEOS ACTUALIZADOS 👇
        if (reserva.getEstado().equalsIgnoreCase("COMPLETADA")) {
            throw new Exception("No puedes cancelar una reserva que ya finalizó su horario.");
        }
        if (reserva.getEstado().equalsIgnoreCase("CANCELADA")) {
            throw new Exception("La reserva ya se encontraba cancelada.");
        }

        reserva.setEstado("CANCELADA");
        return reservaRepository.save(reserva);
    }

    public Reserva actualizarReserva(Long id, ReservaDTO dto) throws Exception {
        Reserva existente = reservaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: La reserva que intentas editar no existe."));

        // Validar si justo acaba de vencer
        if (existente.getEstado().equals("CONFIRMADA") && isReservaPasada(existente.getFecha(), existente.getHoraFin())) {
            existente.setEstado("COMPLETADA");
            reservaRepository.save(existente);
        }

        // 👇 BLOQUEOS ACTUALIZADOS 👇
        if (existente.getEstado().equalsIgnoreCase("COMPLETADA")) {
            throw new Exception("No puedes editar una reserva que ya finalizó.");
        }
        if (existente.getEstado().equalsIgnoreCase("CANCELADA")) {
            throw new Exception("No puedes editar una reserva cancelada.");
        }

        Area area = areaRepository.findById(dto.getIdArea())
                .orElseThrow(() -> new Exception("Error: La zona deportiva no existe."));

        if (area.getEstado() != null && !area.getEstado().equalsIgnoreCase("disponible")) {
            throw new Exception("Lo sentimos, esta área se encuentra " + area.getEstado() + ".");
        }

        LocalTime inicioNuevo = LocalTime.parse(dto.getHoraInicio());
        LocalTime finNuevo = LocalTime.parse(dto.getHoraFin());
        LocalTime aperturaArea = LocalTime.parse(area.getHoraApertura());
        LocalTime cierreArea = LocalTime.parse(area.getHoraCierre());

        if (inicioNuevo.isAfter(finNuevo) || inicioNuevo.equals(finNuevo)) {
            throw new Exception("La hora de inicio debe ser antes que la hora de fin.");
        }
        if (inicioNuevo.isBefore(aperturaArea) || finNuevo.isAfter(cierreArea)) {
            throw new Exception("El horario está fuera del rango de servicio.");
        }

        List<Reserva> conflictos = reservaRepository.findConflictingConfirmadasForUpdate(
                area.getId(), dto.getFecha(), id, dto.getHoraInicio(), dto.getHoraFin());

        if (!conflictos.isEmpty()) {
            throw new IllegalArgumentException("El horario seleccionado ya está ocupado para esta área");
        }

        existente.setArea(area);
        existente.setFecha(dto.getFecha());
        existente.setHoraInicio(dto.getHoraInicio());
        existente.setHoraFin(dto.getHoraFin());
        existente.setDescripcion(dto.getDescripcion());

        return reservaRepository.save(existente);
    }
}