package mx.edu.utez.Integradora5A_SIGRAD.repository;

import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByAreaIdAndFechaAndEstadoNot(Long idArea, String fecha, String estado);

    List<Reserva> findByUsuarioId(Long idUsuario);

    // 3. EL CAZADOR DE CHOQUES PARA EDICIÓN:
    // Igual que el primero, pero ignora el ID de la reserva que estamos modificando
    List<Reserva> findByAreaIdAndFechaAndEstadoNotAndIdNot(Long idArea, String fecha, String estado, Long idReserva);

    // Traslape estricto para edición:
    // Solapa si: existente.horaInicio < nueva.horaFin AND existente.horaFin > nueva.horaInicio
    // Solo considera reservas con estado = 'CONFIRMADA' en el mismo Area y misma fecha,
    // excluyendo la reserva actual por su ID.
    @Query("""
            SELECT r
            FROM Reserva r
            WHERE r.area.id = :idArea
              AND r.fecha = :fecha
              AND r.estado = 'CONFIRMADA'
              AND r.id <> :idReservaActual
              AND r.horaInicio < :horaFinNueva
              AND r.horaFin > :horaInicioNueva
            """)
    List<Reserva> findConflictingConfirmadasForUpdate(
            Long idArea,
            String fecha,
            Long idReservaActual,
            String horaInicioNueva,
            String horaFinNueva
    );

    // Cuenta todas las reservas que estén "CONFIRMADA"
    long countByEstado(String estado);

    // Cuenta las reservas de un día en específico que estén "CONFIRMADA"
    long countByFechaAndEstado(String fecha, String estado);
}