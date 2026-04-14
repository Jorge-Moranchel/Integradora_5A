package mx.edu.utez.Integradora5A_SIGRAD.repository;

import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT r FROM Reserva r JOIN FETCH r.usuario JOIN FETCH r.area ORDER BY r.id DESC")
    List<Reserva> findAll();

    @Query("SELECT r FROM Reserva r JOIN FETCH r.usuario JOIN FETCH r.area WHERE r.usuario.id = :idUsuario ORDER BY r.fecha DESC, r.horaInicio DESC")
    List<Reserva> findByUsuarioId(Long idUsuario);

    List<Reserva> findByAreaIdAndFechaAndEstadoNot(Long idArea, String fecha, String estado);

    List<Reserva> findByAreaIdAndFechaAndEstadoNotAndIdNot(Long idArea, String fecha, String estado, Long idReserva);

    List<Reserva> findByEstado(String estado);

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

    long countByEstado(String estado);

    long countByFechaAndEstado(String fecha, String estado);

    // ✅ OPTIMIZACIÓN 1: Paginación ordenada. Agregamos el ORDER BY r.id DESC para que la web no se trabe
    @EntityGraph(attributePaths = {"usuario", "area"})
    @Query("SELECT r FROM Reserva r LEFT JOIN r.usuario u LEFT JOIN r.area a WHERE " +
            "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(u.rol) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
            "ORDER BY r.id DESC")
    Page<Reserva> buscarConPaginacion(@Param("termino") String termino, Pageable pageable);

    // ✅ OPTIMIZACIÓN 2: Actualización masiva (Bulk Update) directamente en la BD
    @Modifying
    @Query("UPDATE Reserva r SET r.estado = 'COMPLETADA' WHERE r.estado = 'CONFIRMADA' AND (r.fecha < :fechaHoy OR (r.fecha = :fechaHoy AND r.horaFin < :horaActual))")
    int marcarVencidasComoCompletadas(@Param("fechaHoy") String fechaHoy, @Param("horaActual") String horaActual);

    // Métodos del Dashboard...
    long countByEstadoIgnoreCase(String estado);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.fecha = :fecha AND r.estado IN ('CONFIRMADA', 'COMPLETADA')")
    long countReservasValidasPorFecha(@Param("fecha") String fecha);

    @Query("SELECT r.area.id, COUNT(r) FROM Reserva r WHERE r.fecha = :fecha AND r.estado IN ('CONFIRMADA', 'COMPLETADA') GROUP BY r.area.id")
    List<Object[]> countReservasValidasPorAreaYFecha(@Param("fecha") String fecha);

    @Query("SELECT a.nombre, COUNT(r) FROM Reserva r JOIN r.area a WHERE r.estado IN ('CONFIRMADA', 'COMPLETADA') GROUP BY a.nombre")
    List<Object[]> countReservasValidasPorNombreArea();

    @Query("SELECT SUBSTRING(r.fecha, 1, 7), COUNT(r) FROM Reserva r WHERE r.estado IN ('CONFIRMADA', 'COMPLETADA') GROUP BY SUBSTRING(r.fecha, 1, 7)")
    List<Object[]> countReservasValidasPorMes();
}