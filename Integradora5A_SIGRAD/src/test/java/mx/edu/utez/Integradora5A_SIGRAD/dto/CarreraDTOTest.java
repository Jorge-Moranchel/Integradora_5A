package mx.edu.utez.Integradora5A_SIGRAD.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CarreraDTOTest {

    @Test
    void constructorPorDefecto_instanciaObjeto() {
        CarreraDTO dto = new CarreraDTO();
        assertNotNull(dto);
    }

    @Test
    void settersAndGetters_idYNombre_devuelvenMismosValores() {
        CarreraDTO dto = new CarreraDTO();

        dto.setId(1L);
        dto.setNombre("Ingenieria en Software");

        assertEquals(1L, dto.getId());
        assertEquals("Ingenieria en Software", dto.getNombre());
    }
}

