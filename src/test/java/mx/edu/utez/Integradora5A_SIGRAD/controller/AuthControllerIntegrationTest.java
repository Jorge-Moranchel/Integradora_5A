package mx.edu.utez.Integradora5A_SIGRAD.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pruebas de Integración — AuthController")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioActivo;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        usuarioActivo = new Usuario();
        usuarioActivo.setNombre("Admin Test");
        usuarioActivo.setEmailInstitucional("admin@utez.edu.mx");
        usuarioActivo.setContrasena("admin123");
        usuarioActivo.setRol("ESTUDIANTE");
        usuarioActivo.setEstado(true);
        usuarioActivo.setValidado(true);
        usuarioActivo = usuarioRepository.save(usuarioActivo);
    }

    @Test
    @DisplayName("IT-AUTH-01: Login exitoso con credenciales correctas")
    void login_exitoso() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("correo", "admin@utez.edu.mx");
        body.put("password", "admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.nombre").value("Admin Test"));
    }

    @Test
    @DisplayName("IT-AUTH-02: Login falla con contraseña incorrecta")
    void login_passwordIncorrecta() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("correo", "admin@utez.edu.mx");
        body.put("password", "INCORRECTA");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("IT-AUTH-03: Login falla con correo inexistente")
    void login_correoNoRegistrado() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("correo", "noexiste@utez.edu.mx");
        body.put("password", "cualquiera");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("IT-AUTH-04: Login falla con campos vacíos")
    void login_camposVacios() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("correo", "");
        body.put("password", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("IT-AUTH-05: Login falla si la cuenta está desactivada (estado=false)")
    void login_cuentaDesactivada() throws Exception {
        usuarioActivo.setEstado(false);
        usuarioRepository.save(usuarioActivo);

        Map<String, String> body = new HashMap<>();
        body.put("correo", "admin@utez.edu.mx");
        body.put("password", "admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("IT-AUTH-06: Login falla si la cuenta no está validada")
    void login_cuentaNoValidada() throws Exception {
        usuarioActivo.setValidado(false);
        usuarioRepository.save(usuarioActivo);

        Map<String, String> body = new HashMap<>();
        body.put("correo", "admin@utez.edu.mx");
        body.put("password", "admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }
}
