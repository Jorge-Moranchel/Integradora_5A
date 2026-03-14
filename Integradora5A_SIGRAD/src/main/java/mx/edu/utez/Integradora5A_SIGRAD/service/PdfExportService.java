package mx.edu.utez.Integradora5A_SIGRAD.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Service
public class PdfExportService {

    public void exportAreasToPdf(HttpServletResponse response, List<Area> areas) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Título del PDF
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        fontTitle.setColor(new Color(44, 62, 80)); // Azul marino de tu diseño

        Paragraph p = new Paragraph("Reporte de Áreas Deportivas\n", fontTitle);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        // Tabla de datos
        PdfPTable table = new PdfPTable(4); // 4 columnas
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);

        // Encabezados de tabla
        writeTableHeader(table);

        // Datos de las áreas
        for (Area area : areas) {
            table.addCell(area.getNombre());
            table.addCell(area.getTipoDeporte());
            table.addCell(area.getHorario());
            table.addCell(area.getEstado());
        }

        document.add(table);
        document.close();
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(0, 168, 84)); // Verde esmeralda de tu login
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Nombre", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Deporte", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Horario", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Estado", font));
        table.addCell(cell);
    }
}