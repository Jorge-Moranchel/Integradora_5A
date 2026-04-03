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

import java.util.UUID;
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

        // 👇 PARCHE PARA LA APP MÓVIL 👇
        // Traducimos los roles que vienen de Android al formato de Spring Boot
        if (rol != null) {
            if (rol.equalsIgnoreCase("Alumno")) {
                rol = "ESTUDIANTE";
            } else if (rol.equalsIgnoreCase("Docente")) {
                rol = "DOCENTE";
            }
        }

        if (rol == null || rol.isEmpty()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        if ("ESTUDIANTE".equalsIgnoreCase(rol)) {
            String matricula = dto.getMatricula() != null ? dto.getMatricula().trim() : null;
            String localPart = obtenerParteAntesDeArroba(email);

            if (matricula == null || matricula.isEmpty()) {
                throw new IllegalArgumentException("La matrícula es obligatoria para ESTUDIANTE");
            }
            if (!matricula.equalsIgnoreCase(localPart)) {
                throw new IllegalArgumentException("La matrícula debe coincidir con el email (antes del @)");
            }
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setMatricula(dto.getMatricula() != null ? dto.getMatricula().trim() : null);
        usuario.setTelefono(dto.getTelefono());
        usuario.setCarrera(dto.getCarrera());
        usuario.setEmailInstitucional(email);
        usuario.setContrasena(dto.getContrasena());
        usuario.setRol(rol); // Guardamos el rol ya traducido
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        usuario.setValidado(false);

        // 👇 LA MAGIA ARREGLADA (El orden es la clave) 👇

        // 1. Generamos el token (UUID)
        String tokenVerificacion = generarCodigoVerificacion();

        // 2. Se lo asignamos al usuario ANTES de ir a la BD
        usuario.setCodigoVerificacion(tokenVerificacion);

        // 3. Ahora SÍ guardamos. El token viaja junto con los demás datos hacia Oracle.
        Usuario guardado = usuarioRepository.save(usuario);

        // 4. Mandamos el correo con el mismo token
        enviarCorreoConfirmacion(guardado, tokenVerificacion);

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
        // Genera un token larguísimo y único, ej: "550e8400-e29b-41d4-a716-446655440000"
        return UUID.randomUUID().toString();
    }

    private void enviarCorreoConfirmacion(Usuario usuario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(usuario.getEmailInstitucional());
        if (mailFrom != null && !mailFrom.isBlank()) {
            message.setFrom(mailFrom);
        }
        message.setSubject("Activa tu cuenta de SIGRAD");

        // Creamos la URL que apuntará a un nuevo endpoint en tu backend
        String urlVerificacion = "http://localhost:8080/api/usuarios/verificar?token=" + token;

        message.setText("Hola " + usuario.getNombre() + ",\n\n"
                + "Gracias por registrarte. Para activar tu cuenta, por favor haz clic en el siguiente enlace:\n\n"
                + urlVerificacion + "\n\n"
                + "Si no solicitaste este registro, ignora este correo.");

        mailSender.send(message);
    }
}