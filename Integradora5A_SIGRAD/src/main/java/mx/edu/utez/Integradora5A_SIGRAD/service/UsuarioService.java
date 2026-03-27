package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.UsuarioDTO;
import mx.edu.utez.Integradora5A_SIGRAD.exception.EmailAlreadyExistsException;
import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage; // CORREGIDO: se eliminó el "in"
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    @Transactional
    public Usuario registrarUsuario(UsuarioDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Datos de usuario inválidos");
        }

        validarCamposObligatorios(dto);

        String email = dto.getEmailInstitucional().trim();

        if (usuarioRepository.existsByEmailInstitucional(email)) {
            throw new EmailAlreadyExistsException("Este correo ya está registrado.");
        }

        String rol = dto.getRol() != null ? dto.getRol().trim() : null;
        if (rol == null || rol.isEmpty()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        if ("ESTUDIANTE".equalsIgnoreCase(rol)) {
            String matricula = dto.getMatricula() != null ? dto.getMatricula().trim() : null;
            String localPart = obtenerParteAntesDeArroba(email);

            if (matricula == null || matricula.isEmpty()) {
                throw new IllegalArgumentException("La matrícula es obligatoria para ESTUDIANTE");
            }
            if (!matricula.equals(localPart)) {
                throw new IllegalArgumentException("La matrícula debe coincidir con el email (antes del @)");
            }
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setMatricula(dto.getMatricula().trim());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCarrera(dto.getCarrera());
        usuario.setEmailInstitucional(email);
        usuario.setContrasena(dto.getContrasena());
        usuario.setRol(rol);
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        usuario.setValidado(false);

        Usuario guardado = usuarioRepository.save(usuario);

        String codigoVerificacion = generarCodigoVerificacion();
        enviarCorreoConfirmacion(guardado, codigoVerificacion);

        return guardado;
    }

    private void validarCamposObligatorios(UsuarioDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty() ||
                dto.getMatricula() == null || dto.getMatricula().trim().isEmpty() ||
                dto.getEmailInstitucional() == null || dto.getEmailInstitucional().trim().isEmpty() ||
                dto.getContrasena() == null || dto.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("Faltan datos obligatorios.");
        }

        if (!dto.getEmailInstitucional().endsWith("@utez.edu.mx")) {
            throw new IllegalArgumentException("Solo se permiten correos @utez.edu.mx");
        }
    }

    private String obtenerParteAntesDeArroba(String email) {
        int idx = email.indexOf('@');
        return (idx < 0) ? email : email.substring(0, idx);
    }

    private String generarCodigoVerificacion() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }

    private void enviarCorreoConfirmacion(Usuario usuario, String codigoVerificacion) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(usuario.getEmailInstitucional());
        if (mailFrom != null && !mailFrom.isBlank()) {
            message.setFrom(mailFrom);
        }
        message.setSubject("Confirmación de cuenta");
        message.setText("Hola " + usuario.getNombre() + ",\n\nTu código es: " + codigoVerificacion);
        mailSender.send(message);
    }
}