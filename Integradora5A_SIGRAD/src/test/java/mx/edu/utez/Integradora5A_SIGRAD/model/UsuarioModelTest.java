package mx.edu.utez.Integradora5A_SIGRAD.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas Unitarias — Modelo Usuario")
class UsuarioModelTest {

    @Test
    @DisplayName("TC-UM-01: Valores por defecto en Usuario")
    void valoresDefecto_usuario() {
        Usuario usuario = new Usuario();
        assertTrue(usuario.getEstado());
        assertFalse(usuario.getValidado());
    }

    @Test
    @DisplayName("TC-UM-02: Getters y Setters de Usuario funcionan correctamente")
    void gettersSetters_usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana Lopez");
        usuario.setMatricula("20223tn001");
        usuario.setTelefono("7771234567");
        usuario.setCarrera("TN");
        usuario.setEmailInstitucional("20223tn001@utez.edu.mx");
        usuario.setContrasena("password123");
        usuario.setRol("ESTUDIANTE");
        usuario.setEstado(false);
        usuario.setValidado(true);
        usuario.setCodigoVerificacion("uuid-test");

        assertEquals(1L, usuario.getId());
        assertEquals("Ana Lopez", usuario.getNombre());
        assertEquals("20223tn001", usuario.getMatricula());
        assertEquals("7771234567", usuario.getTelefono());
        assertEquals("TN", usuario.getCarrera());
        assertEquals("20223tn001@utez.edu.mx", usuario.getEmailInstitucional());
        assertEquals("password123", usuario.getContrasena());
        assertEquals("ESTUDIANTE", usuario.getRol());
        assertFalse(usuario.getEstado());
        assertTrue(usuario.getValidado());
        assertEquals("uuid-test", usuario.getCodigoVerificacion());
    }
}
