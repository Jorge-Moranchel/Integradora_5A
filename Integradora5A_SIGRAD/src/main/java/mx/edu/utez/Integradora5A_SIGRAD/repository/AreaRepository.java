package mx.edu.utez.Integradora5A_SIGRAD.repository;

import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    boolean existsByNombre(String nombre);

    List<Area> findByEstado(String estado);

    // ✅ NUEVO: Consulta ultra rápida que ignora la columna de imagen (CLOB/Base64)
    @Query("SELECT new mx.edu.utez.Integradora5A_SIGRAD.model.Area(a.id, a.nombre, a.tipo, a.ubicacion, a.horaApertura, a.horaCierre, a.estado, a.motivoBloqueo, a.fechaInicioBloqueo, a.fechaFinBloqueo) FROM Area a")
    List<Area> findAllSinImagen();
}