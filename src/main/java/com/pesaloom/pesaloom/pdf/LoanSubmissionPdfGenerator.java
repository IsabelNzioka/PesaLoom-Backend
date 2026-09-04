package com.pesaloom.pesaloom.pdf;

import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


@Service
public class LoanSubmissionPdfGenerator {

    private static final Color BRAND_COLOR = new Color(0x1F, 0x4E, 0x79); // matches --primary in the frontend
    private static final Color MUTED = new Color(0x64, 0x74, 0x8B);
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm").withZone(ZoneOffset.UTC);

    private final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BRAND_COLOR);
    private final Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BRAND_COLOR);
    private final Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, MUTED);
    private final Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
    private final Font mutedFont = FontFactory.getFont(FontFactory.HELVETICA, 9, MUTED);

    public byte[] generate(LoanApplication application) {
        Document document = new Document(PageSize.A4, 48, 48, 56, 56);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("PesaLoom", titleFont));
            document.add(new Paragraph("Loan Application Summary", labelFont));
            document.add(Chunk.NEWLINE);

            String applicantName = application.getPersonalInfo() != null ? application.getPersonalInfo().fullName() : "—";
            String loanType = application.getLoanType() != null ? formatLoanType(application.getLoanType().name()) : "—";

            document.add(section("Application"));
            document.add(table(
                    row("Reference number", application.getReferenceNumber()),
                    row("Applicant", applicantName),
                    row("Submitted", application.getSubmittedAt() != null ? DATE_FORMAT.format(application.getSubmittedAt()) + " UTC" : "—")
            ));
            document.add(Chunk.NEWLINE);

            document.add(section("Loan Details"));
            document.add(table(
                    row("Loan type", loanType),
                    row("Amount requested", currency(application.getLoanAmount())),
                    row("Tenure", application.getLoanTenureMonths() != null ? application.getLoanTenureMonths() + " months" : "—"),
                    row("Interest rate", application.getInterestRate() != null ? application.getInterestRate() + "% p.a." : "—")
            ));
            document.add(Chunk.NEWLINE);

            document.add(section("Repayment Estimate"));
            document.add(table(
                    row("Monthly EMI", currency(application.getEmi())),
                    row("Processing fee", currency(application.getProcessingFee())),
                    row("Total interest", currency(application.getTotalInterest())),
                    row("Total payable", currency(application.getTotalPayable()))
            ));
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph footer = new Paragraph(
                    "This is a summary of the information you submitted and does not constitute loan approval. "
                            + "Our team will review your application and contact you with a decision.",
                    mutedFont);
            document.add(footer);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    private Paragraph section(String title) {
        Paragraph p = new Paragraph(title, sectionFont);
        p.setSpacingBefore(8);
        p.setSpacingAfter(6);
        return p;
    }

    private String[] row(String label, String value) {
        return new String[]{label, value == null ? "—" : value};
    }

    private PdfPTable table(String[]... rows) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{1.2f, 2f});
        } catch (Exception ignored) {
            // widths are optional cosmetics; never worth failing the PDF over
        }

        for (String[] row : rows) {
            PdfPCell labelCell = new PdfPCell(new Phrase(row[0], labelFont));
            labelCell.setBorder(0);
            labelCell.setPaddingBottom(4);
            table.addCell(labelCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(row[1], valueFont));
            valueCell.setBorder(0);
            valueCell.setPaddingBottom(4);
            valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(valueCell);
        }
        return table;
    }

    private String currency(BigDecimal amount) {
        return amount == null ? "—" : "KES " + amount.toPlainString();
    }

    private String formatLoanType(String loanType) {
        String lower = loanType.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
