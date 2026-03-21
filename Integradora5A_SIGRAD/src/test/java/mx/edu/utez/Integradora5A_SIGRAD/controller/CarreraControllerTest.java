package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.model.Carrera;
import mx.edu.utez.Integradora5A_SIGRAD.service.CarreraService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarreraControllerTest {

    @Mock
    private CarreraService carreraService;

    @InjectMocks
    private CarreraController carreraController;

    @Test
    void testListarCarrerasBuenaPath() {
        List<Carrera> lista = List.of(new Carrera());
        when(carreraService.listarTodas()).thenReturn(lista);

        ResponseEntity<List<Carrera>> response = carreraController.listarCarreras();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lista, response.getBody());
    }

    @Test
    void testGuardarCarreraBuenaPath() throws Exception {
        Carrera input = new Carrera();
        Carrera guardada = new Carrera();
        guardada.setId(1L);
        when(carreraService.guardar(any(Carrera.class))).thenReturn(guardada);

        ResponseEntity<?> response = carreraController.guardarCarrera(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(guardada, response.getBody());
    }

    @Test
    void testGuardarCarreraMalaPath() throws Exception {
        Carrera input = new Carrera();
        when(carreraService.guardar(any(Carrera.class))).thenThrow(new Exception("nombre obligatorio"));

        ResponseEntity<?> response = carreraController.guardarCarrera(input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("nombre obligatorio", response.getBody());
    }

    @Test
    void testActualizarCarreraBuenaPath() throws Exception {
        Carrera carrera = new Carrera();
        when(carreraService.actualizar(eq(10L), any(Carrera.class))).thenReturn(carrera);

        ResponseEntity<?> response = carreraController.actualizarCarrera(10L, new Carrera());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(carrera, response.getBody());
    }

    @Test
    void testActualizarCarreraMalaPath() throws Exception {
        when(carreraService.actualizar(eq(10L), any(Carrera.class))).thenThrow(new Exception("no encontrada"));

        ResponseEntity<?> response = carreraController.actualizarCarrera(10L, new Carrera());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("no encontrada", response.getBody());
    }

    @Test
    void testCambiarEstadoCarreraBuenaPath() throws Exception {
        Carrera c = new Carrera();
        when(carreraService.cambiarEstado(5L)).thenReturn(c);

        ResponseEntity<?> response = carreraController.cambiarEstado(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(c, response.getBody());
    }

    // falta el catch de cambiar estado (400) - me quede sin tiempo
}
