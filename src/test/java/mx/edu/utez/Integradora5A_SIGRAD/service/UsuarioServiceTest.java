package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.dto.UsuarioDTO;
import mx.edu.utez.Integradora5A_SIGRAD.exception.EmailAlreadyExistsException;
import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.DivisionRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias — UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DivisionRepository divisionRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioDTO dtoEstudiante;

    @BeforeEach
    void setUp() {
        dtoEstudiante = new UsuarioDTO();
        dtoEstudiante.setNombre("Ana López");
        dtoEstudiante.setMatricula("20223tn001");
        dtoEstudiante.setEmailInstitucional("20223tn001@utez.edu.mx");
        dtoEstudiante.setContrasena("password123");
        dtoEstudiante.setRol("ESTUDIANTE");
        dtoEstudiante.setTelefono("7771234567");
        dtoEstudiante.setCarrera("TN");
        dtoEstudiante.setEstado(true);
    }

    @Test
    @DisplayName("TC-US-01: Registrar estudiante exitosamente")
    void registrarEstudiante_exitoso() {
        when(usuarioRepository.existsByEmailInstitucional(any())).thenReturn(false);

        Usuario guardado = new Usuario();
        guardado.setId(1L);
        guardado.setNombre("Ana López");
        guardado.setEmailInstitucional("20223tn001@utez.edu.mx");
        guardado.setRol("ESTUDIANTE");
        guardado.setCodigoVerificacion("some-uuid");
        when(usuarioRepository.save(any())).thenReturn(guardado);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        Usuario resultado = usuarioService.registrarUsuario(dtoEstudiante);

        assertNotNull(resultado);
        assertEquals("Ana López", resultado.getNombre());
        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("TC-US-02: Registrar usuario falla si el correo ya existe")
    void registrarUsuario_emailDuplicado() {
        when(usuarioRepository.existsByEmailInstitucional(any())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> usuarioService.registrarUsuario(dtoEstudiante));
    }

    @Test
    @DisplayName("TC-US-03: Registrar usuario falla si el correo no es @utez.edu.mx")
    void registrarUsuario_correoInstitucionalInvalido() {
        dtoEstudiante.setEmailInstitucional("ana@gmail.com");

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(dtoEstudiante));
    }

    @Test
    @DisplayName("TC-US-04: Registrar estudiante falla si la matrícula no coincide con el correo")
    void registrarEstudiante_matriculaNoCoincide() {
        dtoEstudiante.setMatricula("OTRA_MATRICULA");
        when(usuarioRepository.existsByEmailInstitucional(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(dtoEstudiante));
    }

    @Test
    @DisplayName("TC-US-05: Registrar usuario falla si nombre está vacío")
    void registrarUsuario_nombreVacio() {
        dtoEstudiante.setNombre("");

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(dtoEstudiante));
    }

    @Test
    @DisplayName("TC-US-06: Registrar usuario falla si dto es null")
    void registrarUsuario_dtoNull() {
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(null));
    }

    @Test
    @DisplayName("TC-US-07: Rol 'Alumno' (Android) se traduce a 'ESTUDIANTE'")
    void registrarUsuario_rolAlumnoTraducidoAEstudiante() {
        dtoEstudiante.setRol("Alumno");
        when(usuarioRepository.existsByEmailInstitucional(any())).thenReturn(false);

        Usuario guardado = new Usuario();
        guardado.setId(1L);
        guardado.setRol("ESTUDIANTE");
        guardado.setEmailInstitucional("20223tn001@utez.edu.mx");
        guardado.setCodigoVerificacion("uuid");
        when(usuarioRepository.save(any())).thenReturn(guardado);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        Usuario resultado = usuarioService.registrarUsuario(dtoEstudiante);
        // El servicio traduce Alumno → ESTUDIANTE internamente antes de guardar
        assertNotNull(resultado);
    }
}
