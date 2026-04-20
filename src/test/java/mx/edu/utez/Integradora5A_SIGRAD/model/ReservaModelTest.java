package mx.edu.utez.Integradora5A_SIGRAD.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — Modelo Reserva")
class ReservaModelTest {

    @Test
    @DisplayName("TC-RM-01: Estado por defecto de Reserva es CONFIRMADA")
    void estadoDefecto_esConfirmada() {
        Reserva reserva = new Reserva();
        assertEquals("CONFIRMADA", reserva.getEstado());
    }

    @Test
    @DisplayName("TC-RM-02: Getters y Setters de Reserva funcionan correctamente")
    void gettersSetters_funcionanCorrectamente() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFecha("2025-12-01");
        reserva.setHoraInicio("10:00");
        reserva.setHoraFin("12:00");
        reserva.setDescripcion("Partido de fútbol");
        reserva.setEstado("CANCELADA");

        assertEquals(1L, reserva.getId());
        assertEquals("2025-12-01", reserva.getFecha());
        assertEquals("10:00", reserva.getHoraInicio());
        assertEquals("12:00", reserva.getHoraFin());
        assertEquals("Partido de fútbol", reserva.getDescripcion());
        assertEquals("CANCELADA", reserva.getEstado());
    }

    @Test
    @DisplayName("TC-RM-03: Estado por defecto de Area es disponible")
    void areaEstadoDefecto_esDisponible() {
        Area area = new Area();
        assertEquals("disponible", area.getEstado());
    }

    @Test
    @DisplayName("TC-RM-04: Constructor completo de Area asigna imagen como null")
    void areaConstructorCompleto_imagenEsNull() {
        Area area = new Area(1L, "Cancha", "Fútbol", "Edificio A", "07:00", "21:00",
                "disponible", null, null, null);
        assertNull(area.getImagen());
        assertEquals("Cancha", area.getNombre());
    }
}
