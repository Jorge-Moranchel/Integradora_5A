package mx.edu.utez.Integradora5A_SIGRAD.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CarreraTest {

    @Test
    void constructorPorDefecto_instanciaObjeto() {
        Carrera carrera = new Carrera();
        assertNotNull(carrera);
    }

    @Test
    void gettersAndSetters_idNombreHabilitada_devuelvenMismosValores() {
        Carrera carrera = new Carrera();

        carrera.setId(1L);
        carrera.setNombre("Ingenieria en Software");
        carrera.setHabilitada(true);

        assertEquals(1L, carrera.getId());
        assertEquals("Ingenieria en Software", carrera.getNombre());
        assertEquals(true, carrera.getHabilitada());
    }
}

