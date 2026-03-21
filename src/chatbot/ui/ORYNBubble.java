package chatbot.ui;

import chatbot.model.Message;
import chatbot.util.AppConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ORYN Rose Noir Glassmorphism Bubble
 * - Frosted glass effect via layered semi-transparent fills
 * - Inner shimmer highlight at top
 * - Rose gold / warm gold gradient border
 * - Proper emoji rendering via font fallback chain
 * - Bold markdown rendering
 * - Sender name + time header
 * - Asymmetric ORYN-signature corner radius
 */
public class ORYNBubble extends JPanel {

    private final Message msg;
    private final boolean isUser;
    private boolean highlighted = false;

    private static final int PAD_H      = 16;
    private static final int PAD_V      = 12;
    private static final int LINE_GAP   = 5;
    private static final int NAME_ROW_H = 20;

    // Font chain for proper emoji
    private static final String[] FONT_NAMES = {"Segoe UI Emoji","Apple Color Emoji","Noto Color Emoji","Segoe UI"};

    public Message getMsg()               { return msg; }
    public void setHighlighted(boolean h) { this.highlighted = h; repaint(); }

    public ORYNBubble(Message msg) {
        this.msg    = msg;
        this.isUser = msg.getSender() == Message.Sender.USER;
        setOpaque(false);
        setMaximumSize(new Dimension(AppConfig.MAX_BUBBLE_WIDTH + 60, 3000));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,       RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,          RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,  RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // ── Measure text ──────────────────────────────────────────────────────
        List<List<Segment>> paragraphs = parseText(msg.getText());
        Font baseFont = AppConfig.FONT_MESSAGE;
        Font boldFont = new Font(baseFont.getName(), Font.BOLD, baseFont.getSize());
        FontMetrics fmBase = g2.getFontMetrics(baseFont);
        int lineH  = fmBase.getHeight() + LINE_GAP;
        int ascent = fmBase.getAscent();

        int contentW = 0;
        for (List<Segment> para : paragraphs) {
            contentW = Math.max(contentW, measureLine(g2, para, baseFont, boldFont));
        }
        contentW = Math.min(contentW, AppConfig.MAX_BUBBLE_WIDTH);
        contentW = Math.max(contentW, 80);

        FontMetrics fmTime = g2.getFontMetrics(AppConfig.FONT_TIMESTAMP);
        int timeW   = fmTime.stringWidth(msg.getTimestamp());
        int bubbleW = Math.max(contentW, timeW + 20) + PAD_H * 2;
        int bubbleH = paragraphs.size() * lineH + PAD_V * 2 + 6;
        int totalH  = NAME_ROW_H + bubbleH + 6;

        setPreferredSize(new Dimension(bubbleW + 4, totalH));

        int bx = isUser ? getWidth() - bubbleW - 2 : 2;
        int by = NAME_ROW_H;

        // ── Sender name + timestamp ────────────────────────────────────────────
        String senderName = isUser ? "You" : AppConfig.BOT_NAME;
        Color  nameColor  = isUser ? AppConfig.ACCENT_GOLD : AppConfig.ACCENT_BRIGHT;
        g2.setFont(AppConfig.FONT_LABEL);
        g2.setColor(nameColor);
        FontMetrics fmLabel = g2.getFontMetrics(AppConfig.FONT_LABEL);
        int nameX = isUser ? bx + bubbleW - fmLabel.stringWidth(senderName) - PAD_H : bx + PAD_H;
        g2.drawString(senderName, nameX, NAME_ROW_H - 5);

        g2.setFont(AppConfig.FONT_TIMESTAMP);
        g2.setColor(AppConfig.TEXT_SECONDARY());
        FontMetrics fmTs = g2.getFontMetrics(AppConfig.FONT_TIMESTAMP);
        int tsX = isUser
            ? nameX - fmTs.stringWidth(msg.getTimestamp()) - 8
            : nameX + fmLabel.stringWidth(senderName) + 8;
        g2.drawString(msg.getTimestamp(), tsX, NAME_ROW_H - 5);

        // ── Multi-layer shadow ────────────────────────────────────────────────
        for (int i = 4; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 6 * i));
            g2.fill(makeGlassShape(bx + i, by + i, bubbleW, bubbleH));
        }

        // ── Glass base fill ───────────────────────────────────────────────────
        Color baseColor = isUser ? AppConfig.BG_USER_BUBBLE() : AppConfig.BG_BOT_BUBBLE();
        g2.setColor(baseColor);
        g2.fill(makeGlassShape(bx, by, bubbleW, bubbleH));

        // ── Inner gradient shimmer (top highlight — the "glass" look) ─────────
        Color shimmerTop = isUser
            ? new Color(255, 200, 220, 45)
            : new Color(220, 160, 190, 35);
        Color shimmerBot = new Color(0, 0, 0, 0);
        GradientPaint shimmer = new GradientPaint(
            bx, by, shimmerTop,
            bx, by + bubbleH * 0.45f, shimmerBot
        );
        g2.setPaint(shimmer);
        g2.fill(makeGlassShape(bx, by, bubbleW, bubbleH));

        // ── Left/right inner edge glow strip ──────────────────────────────────
        Color edgeColor = isUser
            ? new Color(255, 180, 200, 50)
            : new Color(212, 120, 155, 40);
        int edgeX = isUser ? bx + bubbleW - 3 : bx;
        GradientPaint edgeGrad = new GradientPaint(
            edgeX, by, edgeColor,
            edgeX, by + bubbleH, new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 10)
        );
        g2.setPaint(edgeGrad);
        g2.fillRect(edgeX, by + AppConfig.BUBBLE_RADIUS/2, 3, bubbleH - AppConfig.BUBBLE_RADIUS);

        // ── Glass border — rose gold gradient ─────────────────────────────────
        if (highlighted) {
            g2.setColor(new Color(255, 180, 210, 180));
            g2.setStroke(new BasicStroke(2f));
        } else {
            Color b1 = isUser ? new Color(255, 160, 190, 100) : new Color(212, 120, 155, 70);
            Color b2 = isUser ? new Color(230, 180, 120, 60)  : new Color(180, 80, 120, 40);
            GradientPaint borderPaint = new GradientPaint(bx, by, b1, bx + bubbleW, by + bubbleH, b2);
            g2.setPaint(borderPaint);
            g2.setStroke(new BasicStroke(1f));
        }
        g2.draw(makeGlassShape(bx, by, bubbleW, bubbleH));

        // ── Top shimmer line ──────────────────────────────────────────────────
        g2.setColor(isUser ? new Color(255, 200, 220, 80) : new Color(255, 170, 200, 55));
        g2.setStroke(new BasicStroke(0.8f));
        int r = AppConfig.BUBBLE_RADIUS;
        g2.drawLine(bx + r, by + 1, bx + bubbleW - r, by + 1);

        // ── Text rendering ────────────────────────────────────────────────────
        int textX = bx + PAD_H;
        int textY = by + PAD_V + ascent;
        Color textColor = isUser ? AppConfig.TEXT_USER() : AppConfig.TEXT_PRIMARY();

        for (List<Segment> para : paragraphs) {
            int cx = textX;
            for (Segment seg : para) {
                Font f = seg.bold ? boldFont : baseFont;
                for (int ci = 0; ci < seg.text.length(); ) {
                    int cp = seg.text.codePointAt(ci);
                    String ch = new String(Character.toChars(cp));
                    Font chosen = pickFont(cp, f);
                    g2.setFont(chosen);
                    g2.setColor(textColor);
                    g2.drawString(ch, cx, textY);
                    cx += g2.getFontMetrics(chosen).stringWidth(ch);
                    ci += Character.charCount(cp);
                }
            }
            textY += lineH;
        }

        g2.dispose();
    }

    // ── ORYN Signature Glass Shape ────────────────────────────────────────────
    // Top corners = fully round, opposite bottom corner = slightly sharp
    private Shape makeGlassShape(int x, int y, int w, int h) {
        int r  = AppConfig.BUBBLE_RADIUS;
        int rs = 6; // sharp corner radius
        Path2D p = new Path2D.Float();

        if (isUser) {
            // User: sharp bottom-right
            p.moveTo(x + r, y);
            p.quadTo(x + w, y, x + w, y + r);        // top-right round
            p.lineTo(x + w, y + h - rs);
            p.quadTo(x + w, y + h, x + w - rs, y + h); // bottom-right sharp
            p.lineTo(x + r, y + h);
            p.quadTo(x, y + h, x, y + h - r);         // bottom-left round
            p.lineTo(x, y + r);
            p.quadTo(x, y, x + r, y);                  // top-left round
        } else {
            // ORYN: sharp bottom-left
            p.moveTo(x + r, y);
            p.quadTo(x + w, y, x + w, y + r);          // top-right round
            p.lineTo(x + w, y + h - r);
            p.quadTo(x + w, y + h, x + w - r, y + h);  // bottom-right round
            p.lineTo(x + rs, y + h);
            p.quadTo(x, y + h, x, y + h - rs);          // bottom-left sharp
            p.lineTo(x, y + r);
            p.quadTo(x, y, x + r, y);                   // top-left round
        }
        p.closePath();
        return p;
    }

    // ── Text parsing ──────────────────────────────────────────────────────────

    private List<List<Segment>> parseText(String text) {
        List<List<Segment>> result = new ArrayList<>();
        String[] lines = text.replace("\\n", "\n").split("\n", -1);
        for (String line : lines) result.add(parseSegments(line));
        return result;
    }

    private List<Segment> parseSegments(String line) {
        List<Segment> segs = new ArrayList<>();
        int i = 0;
        while (i < line.length()) {
            if (line.startsWith("**", i)) {
                int end = line.indexOf("**", i + 2);
                if (end != -1) { segs.add(new Segment(line.substring(i+2, end), true)); i = end+2; continue; }
            }
            int next = line.indexOf("**", i);
            String chunk = next == -1 ? line.substring(i) : line.substring(i, next);
            if (!chunk.isEmpty()) segs.add(new Segment(chunk, false));
            if (next == -1) break;
            i = next;
        }
        if (segs.isEmpty()) segs.add(new Segment("", false));
        return segs;
    }

    private int measureLine(Graphics2D g2, List<Segment> segs, Font base, Font bold) {
        int w = 0;
        for (Segment seg : segs) {
            Font f = seg.bold ? bold : base;
            for (int ci = 0; ci < seg.text.length(); ) {
                int cp = seg.text.codePointAt(ci);
                String ch = new String(Character.toChars(cp));
                w += g2.getFontMetrics(pickFont(cp, f)).stringWidth(ch);
                ci += Character.charCount(cp);
            }
        }
        return w;
    }

    // Pick best font for this codepoint
    private Font pickFont(int cp, Font base) {
        if (isEmoji(cp)) {
            for (String name : FONT_NAMES) {
                Font f = new Font(name, Font.PLAIN, base.getSize());
                if (f.canDisplay(cp)) return f;
            }
        }
        return base;
    }

    private boolean isEmoji(int cp) {
        return (cp >= 0x1F000 && cp <= 0x1FFFF) || (cp >= 0x2600 && cp <= 0x27BF)
            || (cp >= 0xFE00 && cp <= 0xFE0F)   || (cp >= 0x2300 && cp <= 0x23FF)
            || (cp >= 0x1F300 && cp <= 0x1F9FF);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(AppConfig.MAX_BUBBLE_WIDTH + 20, 80);
    }

    private record Segment(String text, boolean bold) {}
}
