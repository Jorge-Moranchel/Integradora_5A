package mx.edu.utez.Integradora5A_SIGRAD.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RolTest {

    @Test
    void constructorPorDefecto_instanciaObjeto() {
        Rol rol = new Rol();
        assertNotNull(rol);
    }

    @Test
    void gettersAndSetters_idNombreActivo_devuelvenMismosValores() {
        Rol rol = new Rol();

        rol.setId(1L);
        rol.setNombre("ADMIN");
        rol.setActivo(true);

        assertEquals(1L, rol.getId());
        assertEquals("ADMIN", rol.getNombre());
        assertEquals(true, rol.getActivo());
    }
}

