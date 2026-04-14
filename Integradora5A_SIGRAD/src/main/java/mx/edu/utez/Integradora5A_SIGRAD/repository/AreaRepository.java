package mx.edu.utez.Integradora5A_SIGRAD.repository;

import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // ✅ Importación necesaria

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    boolean existsByNombre(String nombre);

    // ✅ NUEVO: Para buscar áreas que están bloqueadas
    List<Area> findByEstado(String estado);
}