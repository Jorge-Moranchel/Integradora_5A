package mx.edu.utez.Integradora5A_SIGRAD.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UsuarioTest {

    @Test
    void constructorPorDefecto_instanciaObjeto() {
        Usuario usuario = new Usuario();
        assertNotNull(usuario);
    }

    @Test
    void gettersAndSetters_todosLosCampos_devuelvenMismosValores() {
        Usuario usuario = new Usuario();

        usuario.setId(1L);
        usuario.setNombre("Osvaldo");
        usuario.setMatricula("20213tn012");
        usuario.setTelefono("7771234567");
        usuario.setCarrera("TI");
        usuario.setEmailInstitucional("osvaldo@utez.edu.mx");
        usuario.setContrasena("123456");
        usuario.setRol("ADMIN");
        usuario.setEstado(true);

        assertEquals(1L, usuario.getId());
        assertEquals("Osvaldo", usuario.getNombre());
        assertEquals("20213tn012", usuario.getMatricula());
        assertEquals("7771234567", usuario.getTelefono());
        assertEquals("TI", usuario.getCarrera());
        assertEquals("osvaldo@utez.edu.mx", usuario.getEmailInstitucional());
        assertEquals("123456", usuario.getContrasena());
        assertEquals("ADMIN", usuario.getRol());
        assertEquals(true, usuario.getEstado());
    }
}

