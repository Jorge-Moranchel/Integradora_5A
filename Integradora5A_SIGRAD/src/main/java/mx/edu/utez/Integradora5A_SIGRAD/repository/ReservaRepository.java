package mx.edu.utez.Integradora5A_SIGRAD.repository;

import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
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

    @Query("SELECT r FROM Reserva r JOIN FETCH r.usuario JOIN FETCH r.area WHERE r.usuario.id = :idUsuario")
    List<Reserva> findByUsuarioId(Long idUsuario);

    List<Reserva> findByAreaIdAndFechaAndEstadoNot(Long idArea, String fecha, String estado);
    List<Reserva> findByAreaIdAndFechaAndEstadoNotAndIdNot(Long idArea, String fecha, String estado, Long idReserva);

    // Para buscar rápidamente las confirmadas y verificar si ya vencieron 👇
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

    @EntityGraph(attributePaths = {"usuario", "area"})
    @Query("SELECT r FROM Reserva r LEFT JOIN r.usuario u LEFT JOIN r.area a WHERE " +
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(u.rol) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))")
    Page<Reserva> buscarConPaginacion(@Param("termino") String termino, Pageable pageable);
}