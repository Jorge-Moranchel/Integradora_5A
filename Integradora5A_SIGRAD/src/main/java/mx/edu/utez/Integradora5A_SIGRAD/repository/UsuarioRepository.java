package mx.edu.utez.Integradora5A_SIGRAD.repository;

import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailInstitucional(String email);
    boolean existsByEmailInstitucional(String email);
    Optional<Usuario> findByCodigoVerificacion(String codigoVerificacion);

    List<Usuario> findByValidado(Boolean validado);
}