package com.vgarcia.marketplace.application.helpers;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static com.vgarcia.marketplace.infraestructure.utils.Constans.ERROR_GENERAR_EXCEL;

@Component
public class GenericExportHelper {

    /**************************************** PDF ****************************************/
    public <T> ByteArrayInputStream exportToPdf(String title, List<String> headers, List<T> data,
                                                List<Function<T, String>> mappers) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (Document document = new Document()) {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph paragraphTitle = new Paragraph(title, titleFont);
            paragraphTitle.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(paragraphTitle);
            document.add(new Paragraph(" "));

            // Tabla
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100);

            // Cabeceras
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            for (String headerTitle : headers) {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(Color.LIGHT_GRAY);
                header.setBorderWidth(1);
                header.setPhrase(new Paragraph(headerTitle, headerFont));
                header.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
                table.addCell(header);
            }

            // Datos
            for (T item : data) {
                for (Function<T, String> mapper : mappers) {
                    table.addCell(mapper.apply(item));
                }
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    /**************************************** EXCEL ****************************************/
    public <T> ByteArrayInputStream exportToExcel(String sheetName, List<String> headers, List<T> data,
                                                  List<Function<T, String>> mappers) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // Estilo para la cabecera
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Fila de cabecera
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerCellStyle);
            }

            // Llenar datos
            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < mappers.size(); i++) {
                    row.createCell(i).setCellValue(mappers.get(i).apply(item));
                }
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(ERROR_GENERAR_EXCEL + e.getMessage(), e);
        }
    }
}
