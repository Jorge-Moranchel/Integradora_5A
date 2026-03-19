package mx.edu.utez.Integradora5A_SIGRAD.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Service
public class PdfExportService {

    public void exportReservasToPdf(HttpServletResponse response, List<Reserva> reservas) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Título del PDF
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        fontTitle.setColor(new Color(44, 62, 80)); // Azul marino

        Paragraph p = new Paragraph("Historial de Reservas Deportivas\n", fontTitle);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        // Tabla de datos (Ahora son 6 columnas)
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);
        // Ajustamos los anchos para que quepa todo bien
        table.setWidths(new float[] {2.5f, 1.5f, 2.0f, 1.5f, 2.0f, 1.5f});

        // Encabezados
        writeTableHeader(table);

        // Llenar tabla con los datos
        for (Reserva reserva : reservas) {
            // Usuario
            table.addCell(reserva.getUsuario() != null ? reserva.getUsuario().getNombre() : "N/A");

            // Rol (NUEVO)
            table.addCell(reserva.getUsuario() != null && reserva.getUsuario().getRol() != null ?
                    reserva.getUsuario().getRol() : "N/A");

            // Área deportiva
            table.addCell(reserva.getArea() != null ? reserva.getArea().getNombre() : "N/A");

            // Fecha
            table.addCell(reserva.getFecha());

            // Horario (Inicio - Fin)
            String horario = reserva.getHoraInicio() + " - " + reserva.getHoraFin();
            table.addCell(horario);

            // Estado
            String estado = reserva.getEstado() != null ?
                    reserva.getEstado().substring(0, 1).toUpperCase() + reserva.getEstado().substring(1).toLowerCase() : "N/A";
            table.addCell(estado);
        }

        document.add(table);
        document.close();
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(0, 168, 84)); // Verde esmeralda
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Usuario", font));
        table.addCell(cell);
        // Nuevo encabezado para el Rol
        cell.setPhrase(new Phrase("Rol", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Área", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Fecha", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Horario", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Estado", font));
        table.addCell(cell);
    }
}