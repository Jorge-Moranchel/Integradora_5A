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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservaService {

    private static final Pattern ISO_DATE_IN_TEXT = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private AreaRepository areaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // MÉTODO AUXILIAR PARA REVISAR SI YA PASÓ LA HORA (Para cancelar manual)
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

    private void validarFechaYHoraFutura(String fechaStr, String horaInicioStr) throws Exception {
        try {
            LocalDate fechaReserva = LocalDate.parse(fechaStr);
            LocalTime horaInicio = LocalTime.parse(horaInicioStr);
            LocalDateTime fechaHoraReserva = LocalDateTime.of(fechaReserva, horaInicio);

            if (fechaHoraReserva.isBefore(LocalDateTime.now())) {
                throw new Exception("No puedes agendar ni editar una reserva con una fecha u hora que ya pasó.");
            }
        } catch (DateTimeParseException e) {
            throw new Exception("Formato de fecha u hora incorrecto.");
        }
    }

    // ✅ OPTIMIZACIÓN GIGANTE: Actualizamos miles de reservas vencidas en 2 milisegundos directamente en SQL
    public void actualizarEstadosVencidos() {
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String horaActual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        reservaRepository.marcarVencidasComoCompletadas(fechaHoy, horaActual);
    }

    public List<Reserva> listarReservasParaExportPdf(LocalDate inicio, LocalDate fin) {
        actualizarEstadosVencidos();
        return reservaRepository.findAll().stream()
                .filter(r -> parseFechaReserva(r.getFecha())
                        .map(d -> !d.isBefore(inicio) && !d.isAfter(fin))
                        .orElse(false))
                .sorted(Comparator
                        .comparing((Reserva r) -> parseFechaReserva(r.getFecha()).orElse(LocalDate.MIN)).reversed()
                        .thenComparing(r -> r.getId() != null ? r.getId() : 0L, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private Optional<LocalDate> parseFechaReserva(String raw) {
        if (raw == null) return Optional.empty();
        String s = raw.trim();
        if (s.isEmpty()) return Optional.empty();
        Matcher iso = ISO_DATE_IN_TEXT.matcher(s);
        if (iso.find()) {
            try { return Optional.of(LocalDate.parse(iso.group(1))); }
            catch (DateTimeParseException ignored) {}
        }
        try { return Optional.of(LocalDate.parse(s)); }
        catch (DateTimeParseException e) {
            try { return Optional.of(LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"))); }
            catch (DateTimeParseException e2) {
                try { return Optional.of(LocalDate.parse(s, DateTimeFormatter.ofPattern("d/M/yyyy"))); }
                catch (DateTimeParseException e3) { return Optional.empty(); }
            }
        }
    }

    public Reserva crearReserva(ReservaDTO dto) throws Exception {
        validarFechaYHoraFutura(dto.getFecha(), dto.getHoraInicio());

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

        if (reserva.getEstado().equals("CONFIRMADA") && isReservaPasada(reserva.getFecha(), reserva.getHoraFin())) {
            reserva.setEstado("COMPLETADA");
            reservaRepository.save(reserva);
        }

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
        validarFechaYHoraFutura(dto.getFecha(), dto.getHoraInicio());

        Reserva existente = reservaRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: La reserva que intentas editar no existe."));

        if (existente.getEstado().equals("CONFIRMADA") && isReservaPasada(existente.getFecha(), existente.getHoraFin())) {
            existente.setEstado("COMPLETADA");
            reservaRepository.save(existente);
        }

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