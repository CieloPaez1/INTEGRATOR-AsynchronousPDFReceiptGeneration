package com.cielo.adapter.scheduler;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import model.Order;
import model.PendingTask;
import org.springframework.context.annotation.Configuration;
import output.PdfOutput;

import java.io.ByteArrayOutputStream;

@Configuration
public class PdfGenerate implements PdfOutput {
    @Override
    public byte[] generate(PendingTask task) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            Order order = task.getOrder();

            document.add(new Paragraph("ORDER RECEIPT"));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("User ID: " + order.getUser().getId()));
            document.add(new Paragraph("Amount: $" + order.getAmount()));
            document.add(new Paragraph("Status: " + order.getStatus().name()));
            document.add(new Paragraph("Created At: " + order.getCreatedAt()));

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
