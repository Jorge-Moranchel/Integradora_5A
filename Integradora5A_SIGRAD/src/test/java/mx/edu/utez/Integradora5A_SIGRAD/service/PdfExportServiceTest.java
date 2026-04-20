//package mx.edu.utez.Integradora5A_SIGRAD.service;
//
//import jakarta.servlet.http.HttpServletResponse;
//import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
//import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
//import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
//import org.junit.jupiter.api.Test;
//import org.springframework.mock.web.MockHttpServletResponse;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class PdfExportServiceTest {
//
//    private final PdfExportService pdfExportService = new PdfExportService();
//
//    @Test
//    void exportReservasToPdf_happyPath_generatesPdfAndCubreRamasTernarias() throws IOException {
//        MockHttpServletResponse response = new MockHttpServletResponse();
//
//        // 1) usuario/area/estado null => cubre ramas de "N/A" para todos los ternarios
//        Reserva r1 = new Reserva();
//        r1.setUsuario(null);
//        r1.setArea(null);
//        r1.setFecha("2026-03-19");
//        r1.setHoraInicio("14:00");
//        r1.setHoraFin("15:00");
//        r1.setDescripcion("desc");
//        r1.setEstado(null);
//
//        // 2) usuario no-null pero rol null => segunda celda debe ir a "N/A"
//        Usuario u2 = new Usuario();
//        u2.setNombre("Alice");
//        u2.setRol(null);
//        u2.setEstado(true);
//
//        Reserva r2 = new Reserva();
//        r2.setUsuario(u2);
//        r2.setArea(new Area());
//        r2.getArea().setNombre("Cancha 1");
//        r2.setFecha("2026-03-19");
//        r2.setHoraInicio("15:00");
//        r2.setHoraFin("16:00");
//        r2.setDescripcion("desc");
//        r2.setEstado("CONFIRMADA");
//
//        // 3) usuario + rol no-null, área null => cubre rol true y área else
//        Usuario u3 = new Usuario();
//        u3.setNombre("Bob");
//        u3.setRol("ADMIN");
//        u3.setEstado(true);
//
//        Reserva r3 = new Reserva();
//        r3.setUsuario(u3);
//        r3.setArea(null);
//        r3.setFecha("2026-03-19");
//        r3.setHoraInicio("16:00");
//        r3.setHoraFin("17:00");
//        r3.setDescripcion("desc");
//        r3.setEstado("CANCELADA");
//
//        assertDoesNotThrow(() ->
//                pdfExportService.exportReservasToPdf(response, List.of(r1, r2, r3)));
//
//        byte[] bytes = response.getContentAsByteArray();
//        assertNotNull(bytes);
//        assertTrue(bytes.length > 0, "Se espera que el PDF genere contenido en el body del response");
//    }
//
//    @Test
//    void exportReservasToPdf_unhappyPath_outputStreamIOException_propagates() throws Exception {
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        when(response.getOutputStream()).thenThrow(new IOException("boom"));
//
//        Reserva r = new Reserva();
//        r.setFecha("2026-03-19");
//        r.setHoraInicio("14:00");
//        r.setHoraFin("15:00");
//        r.setEstado("CONFIRMADA");
//
//        IOException ex = assertThrows(IOException.class,
//                () -> pdfExportService.exportReservasToPdf(response, List.of(r)));
//        assertEquals("boom", ex.getMessage());
//    }
//}
//
