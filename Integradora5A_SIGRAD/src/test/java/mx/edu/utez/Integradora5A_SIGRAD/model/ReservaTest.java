package mx.edu.utez.Integradora5A_SIGRAD.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReservaTest {

    @Test
    void constructorPorDefecto_instanciaObjeto() {
        Reserva reserva = new Reserva();
        assertNotNull(reserva);
    }

    @Test
    void gettersAndSetters_todosLosCampos_devuelvenMismosValores() {
        Reserva reserva = new Reserva();
        Usuario usuario = new Usuario();
        Area area = new Area();

        reserva.setId(10L);
        reserva.setUsuario(usuario);
        reserva.setArea(area);
        reserva.setFecha("2026-03-19");
        reserva.setHoraInicio("10:00");
        reserva.setHoraFin("12:00");
        reserva.setDescripcion("Entrenamiento");
        reserva.setEstado("CONFIRMADA");

        assertEquals(10L, reserva.getId());
        assertEquals(usuario, reserva.getUsuario());
        assertEquals(area, reserva.getArea());
        assertEquals("2026-03-19", reserva.getFecha());
        assertEquals("10:00", reserva.getHoraInicio());
        assertEquals("12:00", reserva.getHoraFin());
        assertEquals("Entrenamiento", reserva.getDescripcion());
        assertEquals("CONFIRMADA", reserva.getEstado());
    }
}

