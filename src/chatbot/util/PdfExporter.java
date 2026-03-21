package chatbot.util;

import chatbot.model.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports chat history to PDF using Java's built-in PrinterJob.
 * No external libraries needed.
 */
public class PdfExporter implements Printable {

    private final List<Message> messages;
    private final List<String>  lines = new ArrayList<>();

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  16);
    private static final Font FONT_META   = new Font("Segoe UI", Font.ITALIC, 10);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_TEXT   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final int  LINE_HEIGHT = 16;
    private static final int  MARGIN      = 54;

    public PdfExporter(List<Message> messages) {
        this.messages = messages;
        buildLines();
    }

    private void buildLines() {
        lines.add("TITLE:ORYN — Light of Knowledge | Chat Export");
        lines.add("META:Exported: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy  HH:mm")));
        lines.add("META:" + messages.size() + " messages");
        lines.add("DIVIDER:");
        lines.add("BLANK:");

        for (Message msg : messages) {
            boolean isUser = msg.getSender() == Message.Sender.USER;
            String label   = isUser ? "You" : "ORYN";
            lines.add("LABEL:" + label + "  [" + msg.getTimestamp() + "]");
            // Word wrap long messages
            String text = msg.getText();
            lines.add("TEXT:" + text);
            lines.add("BLANK:");
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int pageH   = (int) pf.getImageableHeight();
        int pageW   = (int) pf.getImageableWidth();
        int linesPerPage = (pageH - MARGIN) / LINE_HEIGHT;
        int totalPages   = (int) Math.ceil((double) lines.size() / linesPerPage);

        if (pageIndex >= totalPages) return NO_SUCH_PAGE;

        g2.translate(pf.getImageableX(), pf.getImageableY());

        int startLine = pageIndex * linesPerPage;
        int endLine   = Math.min(startLine + linesPerPage, lines.size());
        int y         = MARGIN / 2;

        for (int i = startLine; i < endLine; i++) {
            String raw = lines.get(i);
            if (raw.startsWith("TITLE:")) {
                g2.setFont(FONT_TITLE);
                g2.setColor(new Color(0, 150, 130));
                g2.drawString(raw.substring(6), 0, y);
            } else if (raw.startsWith("META:")) {
                g2.setFont(FONT_META);
                g2.setColor(Color.GRAY);
                g2.drawString(raw.substring(5), 0, y);
            } else if (raw.startsWith("DIVIDER:")) {
                g2.setColor(new Color(0, 180, 150, 120));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawLine(0, y - 4, pageW, y - 4);
            } else if (raw.startsWith("BLANK:")) {
                // empty line
            } else if (raw.startsWith("LABEL:")) {
                g2.setFont(FONT_LABEL);
                String label = raw.substring(6);
                g2.setColor(label.startsWith("You") ? new Color(180, 100, 20) : new Color(0, 160, 140));
                g2.drawString(label, 0, y);
            } else if (raw.startsWith("TEXT:")) {
                g2.setFont(FONT_TEXT);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(raw.substring(5), 10, y);
            }
            y += LINE_HEIGHT;
        }

        // Page number
        g2.setFont(FONT_META);
        g2.setColor(Color.GRAY);
        g2.drawString("Page " + (pageIndex + 1) + " / " + totalPages, pageW - 60, pageH - 10);

        return PAGE_EXISTS;
    }

    /** Show print dialog and export. Returns true if user confirmed. */
    public static boolean export(List<Message> messages, JFrame parent) {
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No messages to export.", "Export PDF", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("ORYN Chat Export");
        job.setPrintable(new PdfExporter(messages));

        // Show system print dialog (user can choose Save as PDF)
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(parent,
                    "Chat exported successfully!\nChoose 'Save as PDF' in the print dialog to get a PDF file.",
                    "Export Complete ✓", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(parent,
                    "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
}
