package mx.edu.utez.Integradora5A_SIGRAD.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RolDTOTest {

    @Test
    void constructorSinArgumentos_instanciaObjeto() {
        RolDTO dto = new RolDTO();
        assertNotNull(dto);
    }

    @Test
    void constructorConArgumentos_asignaValoresCorrectos() {
        RolDTO dto = new RolDTO(10L, "ADMIN", "Descricion");

        assertEquals(10L, dto.getId());
        assertEquals("ADMIN", dto.getNombre());
    }

    @Test
    void settersAndGetters_idYNombre_devuelvenMismosValores() {
        RolDTO dto = new RolDTO();
        dto.setId(20L);
        dto.setNombre("ALUMNO");

        assertEquals(20L, dto.getId());
        assertEquals("ALUMNO", dto.getNombre());
    }
}

