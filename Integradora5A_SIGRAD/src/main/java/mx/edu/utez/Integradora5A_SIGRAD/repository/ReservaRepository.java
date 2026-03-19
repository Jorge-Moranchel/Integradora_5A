package mx.edu.utez.Integradora5A_SIGRAD.repository;

import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByAreaIdAndFechaAndEstadoNot(Long idArea, String fecha, String estado);

    List<Reserva> findByUsuarioId(Long idUsuario);

    // 3. EL CAZADOR DE CHOQUES PARA EDICIÓN:
    // Igual que el primero, pero ignora el ID de la reserva que estamos modificando
    List<Reserva> findByAreaIdAndFechaAndEstadoNotAndIdNot(Long idArea, String fecha, String estado, Long idReserva);
}