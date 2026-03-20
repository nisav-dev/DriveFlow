package com.driveflow.service;

import com.driveflow.entity.Booking;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * יוצר PDF של אישור הזמנה ישירות מ-HTML — ללא תלות בטמפלייט חיצוני.
 * משתמש ב-OpenHTMLtoPDF (PDFBox).
 */
@Service
@RequiredArgsConstructor
public class PdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateBookingConfirmationPdf(Booking booking) {
        String html = buildHtml(booking);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(baos);
            builder.run();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("שגיאה ביצירת PDF: " + e.getMessage(), e);
        }
    }

    private String buildHtml(Booking b) {
        String customerName = b.getCustomer() != null ? b.getCustomer().getUser().getFullName() : "—";
        String vehicleName  = b.getVehicle() != null  ? b.getVehicle().getDisplayName()       : "—";
        String pickup       = b.getPickupBranch() != null ? b.getPickupBranch().getName()     : "—";
        String ret          = b.getReturnBranch() != null ? b.getReturnBranch().getName()     : "—";
        String pickupDate   = b.getPickupDatetime()  != null ? b.getPickupDatetime().format(DATE_FMT)  : "—";
        String returnDate   = b.getReturnDatetime()  != null ? b.getReturnDatetime().format(DATE_FMT)  : "—";

        return """
                <!DOCTYPE html>
                <html dir="rtl" lang="he">
                <head>
                  <meta charset="UTF-8"/>
                  <style>
                    * { font-family: Arial, Helvetica, sans-serif; margin: 0; padding: 0; box-sizing: border-box; }
                    body { background: #fff; color: #222; font-size: 13px; direction: rtl; }
                    .header { background: #1A3C5E; color: #fff; padding: 24px 32px; display: flex; justify-content: space-between; align-items: center; }
                    .header h1 { font-size: 26px; font-weight: 800; }
                    .header .gold { color: #D4A843; }
                    .header .sub { font-size: 12px; color: #ccc; margin-top: 4px; }
                    .badge { background: #D4A843; color: #1A3C5E; padding: 6px 16px; border-radius: 20px; font-weight: 700; font-size: 13px; }
                    .content { padding: 32px; }
                    .booking-num { background: #f0f4f8; border-right: 4px solid #1A3C5E; padding: 12px 16px; border-radius: 4px; margin-bottom: 24px; }
                    .booking-num h2 { font-size: 18px; color: #1A3C5E; font-weight: 700; }
                    .booking-num p  { color: #666; font-size: 12px; margin-top: 2px; }
                    .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 24px; }
                    .card { border: 1px solid #e0e0e0; border-radius: 8px; padding: 16px; }
                    .card h3 { font-size: 12px; color: #888; font-weight: 600; text-transform: uppercase; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 6px; }
                    .card p  { font-size: 13px; color: #333; font-weight: 500; margin-top: 6px; }
                    .card .label { font-size: 11px; color: #999; margin-top: 10px; }
                    .price-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
                    .price-row.total { font-weight: 800; font-size: 16px; color: #1A3C5E; border-top: 2px solid #1A3C5E; border-bottom: none; padding-top: 12px; margin-top: 4px; }
                    .footer { margin-top: 32px; text-align: center; font-size: 11px; color: #999; border-top: 1px solid #eee; padding-top: 16px; }
                  </style>
                </head>
                <body>
                  <div class="header">
                    <div>
                      <h1>Drive<span class="gold">Flow</span></h1>
                      <div class="sub">www.driveflow.co.il &nbsp;|&nbsp; 1-700-DRIVE</div>
                    </div>
                    <div class="badge">אישור הזמנה</div>
                  </div>

                  <div class="content">
                    <div class="booking-num">
                      <h2>%s</h2>
                      <p>אישור זה מהווה קבלה של ההזמנה שלך — הצג אותו בעת איסוף הרכב</p>
                    </div>

                    <div class="grid">
                      <div class="card">
                        <h3>פרטי לקוח</h3>
                        <p>%s</p>
                      </div>
                      <div class="card">
                        <h3>הרכב</h3>
                        <p>%s</p>
                      </div>
                      <div class="card">
                        <h3>איסוף</h3>
                        <div class="label">סניף</div>
                        <p>%s</p>
                        <div class="label">תאריך ושעה</div>
                        <p>%s</p>
                      </div>
                      <div class="card">
                        <h3>החזרה</h3>
                        <div class="label">סניף</div>
                        <p>%s</p>
                        <div class="label">תאריך ושעה</div>
                        <p>%s</p>
                      </div>
                    </div>

                    <div class="card" style="margin-bottom:24px">
                      <h3>סיכום תשלום</h3>
                      <div class="price-row"><span>מחיר בסיס (%d ימים)</span><span>&#8362;%s</span></div>
                      <div class="price-row"><span>תוספות</span><span>&#8362;%s</span></div>
                      <div class="price-row total"><span>סה"כ לתשלום</span><span>&#8362;%s</span></div>
                    </div>

                    <div class="footer">
                      DriveFlow Car Rental Ltd &nbsp;|&nbsp; כל הזכויות שמורות &copy; 2026<br/>
                      לשאלות ולביטולים: <strong>1-700-DRIVE</strong> &nbsp;|&nbsp; support@driveflow.co.il
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                b.getBookingNumber(),
                customerName,
                vehicleName,
                pickup, pickupDate,
                ret, returnDate,
                b.getNumDays(),
                b.getBasePrice(),
                b.getExtrasPrice(),
                b.getTotalPrice()
        );
    }
}
