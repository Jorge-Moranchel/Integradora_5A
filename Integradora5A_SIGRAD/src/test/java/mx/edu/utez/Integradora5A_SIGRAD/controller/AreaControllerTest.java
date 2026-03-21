package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.dto.AreaDTO;
import mx.edu.utez.Integradora5A_SIGRAD.dto.BloqueoDTO;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.service.AreaService;
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
class AreaControllerTest {

    @Mock
    private AreaService areaService;

    @InjectMocks
    private AreaController areaController;

    @Test
    void testRegistrarAreaBuenaPath() throws Exception {
        AreaDTO dto = new AreaDTO();
        Area guardada = new Area();
        guardada.setId(1L);
        when(areaService.registrarArea(any(AreaDTO.class))).thenReturn(guardada);

        ResponseEntity<?> response = areaController.registrarArea(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(guardada, response.getBody());
    }

    @Test
    void testRegistrarAreaMalaPath() throws Exception {
        AreaDTO dto = new AreaDTO();
        when(areaService.registrarArea(any(AreaDTO.class))).thenThrow(new Exception("nombre duplicado"));

        ResponseEntity<?> response = areaController.registrarArea(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("nombre duplicado", response.getBody());
    }

    @Test
    void testListarAreasBuenaPath() {
        List<Area> lista = List.of(new Area());
        when(areaService.obtenerTodasLasAreas()).thenReturn(lista);

        ResponseEntity<List<Area>> response = areaController.listarAreas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lista, response.getBody());
    }

    // me falto tiempo pa probar si el service truena en listar lol

    @Test
    void testBloquearAreaBuenaPath() throws Exception {
        BloqueoDTO bloqueo = new BloqueoDTO();
        Area bloqueada = new Area();
        when(areaService.bloquearArea(eq(1L), any(BloqueoDTO.class))).thenReturn(bloqueada);

        ResponseEntity<?> response = areaController.bloquearArea(1L, bloqueo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(bloqueada, response.getBody());
    }

    @Test
    void testBloquearAreaMalaPath() throws Exception {
        BloqueoDTO bloqueo = new BloqueoDTO();
        when(areaService.bloquearArea(eq(1L), any(BloqueoDTO.class))).thenThrow(new Exception("no encontrada"));

        ResponseEntity<?> response = areaController.bloquearArea(1L, bloqueo);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("no encontrada", response.getBody());
    }

    @Test
    void testDesbloquearAreaBuenaPath() throws Exception {
        Area area = new Area();
        when(areaService.desbloquearArea(2L)).thenReturn(area);

        ResponseEntity<?> response = areaController.desbloquearArea(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(area, response.getBody());
    }

    // no hice test del catch de desbloquear (se me paso)

    @Test
    void testActualizarAreaBuenaPath() throws Exception {
        AreaDTO dto = new AreaDTO();
        Area actualizada = new Area();
        when(areaService.actualizarArea(eq(3L), any(AreaDTO.class))).thenReturn(actualizada);

        ResponseEntity<?> response = areaController.actualizarArea(3L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(actualizada, response.getBody());
    }

    @Test
    void testActualizarAreaMalaPath() throws Exception {
        AreaDTO dto = new AreaDTO();
        when(areaService.actualizarArea(eq(3L), any(AreaDTO.class))).thenThrow(new Exception("hora inválida"));

        ResponseEntity<?> response = areaController.actualizarArea(3L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("hora inválida", response.getBody());
    }
}
