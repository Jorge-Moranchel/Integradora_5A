package mx.edu.utez.Integradora5A_SIGRAD.repository;

import mx.edu.utez.Integradora5A_SIGRAD.model.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {

    Optional<Division> findByNombreIgnoreCaseAndHabilitadaTrue(String nombre);
}