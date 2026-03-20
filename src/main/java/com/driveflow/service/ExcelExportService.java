package com.driveflow.service;

import com.driveflow.entity.Booking;
import com.driveflow.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── הזמנות ────────────────────────────────────────────────────────────

    public byte[] exportBookings(List<Booking> bookings) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("הזמנות");
            sheet.setRightToLeft(true);
            sheet.setDefaultColumnWidth(18);

            // Styles
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle dateStyle   = createDateStyle(wb);
            CellStyle moneyStyle  = createMoneyStyle(wb);
            CellStyle titleStyle  = createTitleStyle(wb);

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DriveFlow — דוח הזמנות");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));

            // Date row
            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue("תאריך הפקה: " +
                    java.time.LocalDateTime.now().format(DT));

            sheet.createRow(2); // spacer

            // Headers
            String[] headers = {
                "מס' הזמנה", "לקוח", "רכב", "לוחית רישוי",
                "סניף איסוף", "תאריך איסוף", "תאריך החזרה",
                "ימים", "מחיר בסיס", "תוספות", "סה\"כ", "סטטוס"
            };
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 4;
            for (Booking b : bookings) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(b.getBookingNumber());
                row.createCell(1).setCellValue(b.getCustomer() != null
                        ? b.getCustomer().getUser().getFullName() : "—");
                row.createCell(2).setCellValue(b.getVehicle() != null
                        ? b.getVehicle().getDisplayName() : "—");
                row.createCell(3).setCellValue(b.getVehicle() != null
                        ? b.getVehicle().getLicensePlate() : "—");
                row.createCell(4).setCellValue(b.getPickupBranch() != null
                        ? b.getPickupBranch().getName() : "—");

                Cell pickupCell = row.createCell(5);
                pickupCell.setCellValue(b.getPickupDatetime() != null
                        ? b.getPickupDatetime().format(DT) : "");
                pickupCell.setCellStyle(dateStyle);

                Cell returnCell = row.createCell(6);
                returnCell.setCellValue(b.getReturnDatetime() != null
                        ? b.getReturnDatetime().format(DT) : "");
                returnCell.setCellStyle(dateStyle);

                row.createCell(7).setCellValue(b.getNumDays());

                Cell baseCell = row.createCell(8);
                baseCell.setCellValue(b.getBasePrice() != null ? b.getBasePrice().doubleValue() : 0);
                baseCell.setCellStyle(moneyStyle);

                Cell extraCell = row.createCell(9);
                extraCell.setCellValue(b.getExtrasPrice() != null ? b.getExtrasPrice().doubleValue() : 0);
                extraCell.setCellStyle(moneyStyle);

                Cell totalCell = row.createCell(10);
                totalCell.setCellValue(b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0);
                totalCell.setCellStyle(moneyStyle);

                row.createCell(11).setCellValue(b.getStatus() != null ? b.getStatus().name() : "");
            }

            // Auto-size first 5 columns
            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("שגיאה ביצירת Excel", e);
        }
    }

    // ── רכבים ────────────────────────────────────────────────────────────

    public byte[] exportVehicles(List<Vehicle> vehicles) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("רכבים");
            sheet.setRightToLeft(true);
            sheet.setDefaultColumnWidth(16);

            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle moneyStyle  = createMoneyStyle(wb);
            CellStyle titleStyle  = createTitleStyle(wb);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DriveFlow — דוח ציי רכבים");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            sheet.createRow(1);

            String[] headers = {
                "לוחית רישוי", "שם רכב", "שנה", "צבע",
                "קטגוריה", "סניף", "תיבת הילוכים",
                "מחיר יומי", "ק\"מ", "סטטוס"
            };
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 3;
            for (Vehicle v : vehicles) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(v.getLicensePlate());
                row.createCell(1).setCellValue(v.getDisplayName());
                row.createCell(2).setCellValue(v.getYear());
                row.createCell(3).setCellValue(v.getColor() != null ? v.getColor() : "");
                row.createCell(4).setCellValue(v.getCategory() != null ? v.getCategory().getName() : "");
                row.createCell(5).setCellValue(v.getBranch() != null ? v.getBranch().getName() : "");
                row.createCell(6).setCellValue(v.getTransmission() != null ? v.getTransmission().name() : "");

                Cell priceCell = row.createCell(7);
                priceCell.setCellValue(v.getDailyRate() != null ? v.getDailyRate().doubleValue() : 0);
                priceCell.setCellStyle(moneyStyle);

                row.createCell(8).setCellValue(v.getMileage());
                row.createCell(9).setCellValue(v.getStatus() != null ? v.getStatus().name() : "");
            }

            for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("שגיאה ביצירת Excel", e);
        }
    }

    // ── Style helpers ─────────────────────────────────────────────────────

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        DataFormat fmt = wb.createDataFormat();
        style.setDataFormat(fmt.getFormat("₪#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}
