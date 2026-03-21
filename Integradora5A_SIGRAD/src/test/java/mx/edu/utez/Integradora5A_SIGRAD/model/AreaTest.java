package mx.edu.utez.Integradora5A_SIGRAD.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AreaTest {

    @Test
    void constructorPorDefecto_instanciaObjeto() {
        Area area = new Area();
        assertNotNull(area);
    }

    @Test
    void gettersAndSetters_todosLosCampos_devuelvenMismosValores() {
        Area area = new Area();

        area.setId(5L);
        area.setNombre("Cancha de futbol");
        area.setTipo("Cancha");
        area.setUbicacion("Edificio A");
        area.setHoraApertura("08:00");
        area.setHoraCierre("18:00");
        area.setImagen("base64");
        area.setEstado("disponible");
        area.setMotivoBloqueo("Mantenimiento");
        area.setFechaInicioBloqueo("2026-03-01");
        area.setFechaFinBloqueo("2026-03-10");

        assertEquals(5L, area.getId());
        assertEquals("Cancha de futbol", area.getNombre());
        assertEquals("Cancha", area.getTipo());
        assertEquals("Edificio A", area.getUbicacion());
        assertEquals("08:00", area.getHoraApertura());
        assertEquals("18:00", area.getHoraCierre());
        assertEquals("base64", area.getImagen());
        assertEquals("disponible", area.getEstado());
        assertEquals("Mantenimiento", area.getMotivoBloqueo());
        assertEquals("2026-03-01", area.getFechaInicioBloqueo());
        assertEquals("2026-03-10", area.getFechaFinBloqueo());
    }
}

