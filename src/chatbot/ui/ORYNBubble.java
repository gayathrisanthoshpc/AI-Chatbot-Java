package chatbot.ui;

import chatbot.model.Message;
import chatbot.util.AppConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ORYN Trademark Bubble — unique conversation style:
 * • Sender name + timestamp on same line above bubble
 * • Glowing left-edge accent bar (ORYN=teal, User=amber)
 * • Asymmetric rounded corners (top fully round, opposite bottom slightly sharp)
 * • Frosted glass inner glow
 * • Proper emoji rendering via font fallback
 * • **bold** markdown rendered as actual bold
 * • Proper line breaks
 */
public class ORYNBubble extends JPanel {

    private final Message msg;
    private final boolean isUser;
    private static final int BAR_WIDTH   = 3;
    private static final int CORNER_BIG  = 20;
    private static final int CORNER_SML  = 6;
    private static final int PAD_H       = 14;
    private static final int PAD_V       = 11;
    private static final int MAX_WIDTH   = 400;
    private static final int LINE_GAP    = 4;

    // Emoji-aware font chain
    private static final Font FONT_EMOJI = new Font("Segoe UI Emoji", Font.PLAIN, 15);
    private static final Font FONT_BASE  = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font FONT_MONO  = new Font("Consolas",  Font.PLAIN, 13);
    private static final Font FONT_NAME  = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_TIME  = new Font("Segoe UI", Font.PLAIN, 10);

    private boolean highlighted = false;

    public Message getMsg()              { return msg; }
    public void setHighlighted(boolean h){ this.highlighted = h; repaint(); }

    public ORYNBubble(Message msg) {
        this.msg    = msg;
        this.isUser = msg.getSender() == Message.Sender.USER;
        setOpaque(false);
        setMaximumSize(new Dimension(MAX_WIDTH + 60, 2000));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // ── Layout measurements ───────────────────────────────────────────────
        List<List<Segment>> lines = parseLines(msg.getText());

        FontMetrics fmBase = g2.getFontMetrics(FONT_BASE);
        int lineH  = fmBase.getHeight() + LINE_GAP;
        int ascent = fmBase.getAscent();

        // Calculate bubble width
        int contentW = 0;
        for (List<Segment> line : lines) {
            int lw = lineWidth(g2, line);
            contentW = Math.max(contentW, lw);
        }
        contentW = Math.min(contentW, MAX_WIDTH);

        int bubbleW = contentW + PAD_H * 2 + BAR_WIDTH + 6;
        int bubbleH = lines.size() * lineH + PAD_V * 2 + 4;

        // Name + time row height
        int nameRowH = 18;
        int totalH   = nameRowH + bubbleH + 4;
        setPreferredSize(new Dimension(bubbleW + 4, totalH));

        int bx = isUser ? getWidth() - bubbleW - 2 : 2;
        int by = nameRowH;

        // ── Name + Timestamp row ──────────────────────────────────────────────
        String senderName = isUser ? "You" : AppConfig.BOT_NAME;
        Color  nameColor  = isUser ? AppConfig.ACCENT_AMBER : AppConfig.ACCENT;

        g2.setFont(FONT_NAME);
        g2.setColor(nameColor);
        int nameX = isUser ? bx + bubbleW - g2.getFontMetrics().stringWidth(senderName) - BAR_WIDTH - 4 : bx + BAR_WIDTH + PAD_H;
        g2.drawString(senderName, nameX, nameRowH - 4);

        g2.setFont(FONT_TIME);
        g2.setColor(AppConfig.TEXT_SECONDARY());
        String time = msg.getTimestamp();
        int timeW = g2.getFontMetrics().stringWidth(time);
        int timeX = isUser ? bx + bubbleW - g2.getFontMetrics().stringWidth(senderName) - timeW - 12 - BAR_WIDTH
                           : nameX + g2.getFontMetrics(FONT_NAME).stringWidth(senderName) + 8;
        g2.drawString(time, timeX, nameRowH - 4);

        // ── Drop shadow ───────────────────────────────────────────────────────
        for (int i = 3; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 8 * i));
            g2.fill(makeShape(bx + i, by + i, bubbleW, bubbleH));
        }

        // ── Bubble fill ───────────────────────────────────────────────────────
        Color base = isUser ? AppConfig.BG_USER_BUBBLE() : AppConfig.BG_BOT_BUBBLE();
        GradientPaint fill = isUser
            ? new GradientPaint(bx, by, base, bx + bubbleW, by + bubbleH, base.darker())
            : new GradientPaint(bx, by, base, bx + bubbleW, by + bubbleH,
                new Color(Math.max(0, base.getRed()-8), Math.max(0, base.getGreen()-5), Math.max(0, base.getBlue()-5)));
        g2.setPaint(fill);
        g2.fill(makeShape(bx, by, bubbleW, bubbleH));

        // ── Inner top glow (frosted feel) ─────────────────────────────────────
        if (!isUser) {
            GradientPaint frost = new GradientPaint(bx, by, new Color(0, 200, 170, 18), bx, by + bubbleH / 2, new Color(0,0,0,0));
            g2.setPaint(frost);
            g2.fill(makeShape(bx, by, bubbleW, bubbleH));
        }

        // ── Border ────────────────────────────────────────────────────────────
        Color borderColor = isUser ? new Color(220, 150, 40, 80) : new Color(0, 180, 155, 70);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(0.8f));
        g2.draw(makeShape(bx, by, bubbleW, bubbleH));

        // ── Highlight overlay ────────────────────────────────────────────────────
        if (highlighted) {
            g2.setColor(new Color(0, 200, 170, 30));
            g2.fill(makeShape(bx, by, bubbleW, bubbleH));
            g2.setColor(new Color(0, 220, 180, 120));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(makeShape(bx, by, bubbleW, bubbleH));
        }

        // ── Accent edge bar ───────────────────────────────────────────────────
        Color barColor = isUser ? AppConfig.ACCENT_AMBER : AppConfig.ACCENT;
        int barX = isUser ? bx + bubbleW - BAR_WIDTH : bx;
        GradientPaint barGrad = new GradientPaint(barX, by, barColor, barX, by + bubbleH, new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 80));
        g2.setPaint(barGrad);
        int barR = isUser ? CORNER_SML : CORNER_BIG;
        g2.fillRoundRect(barX, by, BAR_WIDTH, bubbleH, barR, barR);

        // ── Text rendering ────────────────────────────────────────────────────
        int textX = bx + BAR_WIDTH + PAD_H + (isUser ? 0 : 2);
        int textY = by + PAD_V + ascent;

        for (List<Segment> line : lines) {
            int cx = textX;
            for (Segment seg : line) {
                Font  f = seg.bold ? FONT_BOLD : FONT_BASE;
                g2.setFont(f);
                g2.setColor(isUser ? AppConfig.TEXT_USER() : AppConfig.TEXT_PRIMARY());

                // Render char by char with emoji fallback
                for (int ci = 0; ci < seg.text.length(); ) {
                    int cp = seg.text.codePointAt(ci);
                    String ch = new String(Character.toChars(cp));
                    Font chosen = isEmoji(cp) ? FONT_EMOJI : f;
                    g2.setFont(chosen);
                    g2.drawString(ch, cx, textY);
                    cx += g2.getFontMetrics(chosen).stringWidth(ch);
                    ci += Character.charCount(cp);
                }
            }
            textY += lineH;
        }

        g2.dispose();
    }

    // ── Asymmetric rounded shape ──────────────────────────────────────────────

    private Shape makeShape(int x, int y, int w, int h) {
        // ORYN Trademark: top corners both round, bottom-near-bar corner sharp
        Path2D path = new Path2D.Float();
        int tl = isUser ? CORNER_BIG : CORNER_BIG;  // top-left
        int tr = isUser ? CORNER_BIG : CORNER_BIG;  // top-right
        int br = isUser ? CORNER_SML : CORNER_BIG;  // bottom-right (sharp for user = near bar)
        int bl = isUser ? CORNER_BIG : CORNER_SML;  // bottom-left (sharp for bot = near bar)

        path.moveTo(x + tl, y);
        path.lineTo(x + w - tr, y);
        path.quadTo(x + w, y, x + w, y + tr);
        path.lineTo(x + w, y + h - br);
        path.quadTo(x + w, y + h, x + w - br, y + h);
        path.lineTo(x + bl, y + h);
        path.quadTo(x, y + h, x, y + h - bl);
        path.lineTo(x, y + tl);
        path.quadTo(x, y, x + tl, y);
        path.closePath();
        return path;
    }

    // ── Text parsing — bold + line breaks ────────────────────────────────────

    private List<List<Segment>> parseLines(String text) {
        List<List<Segment>> result = new ArrayList<>();
        String[] rawLines = text.replace("\\n", "\n").split("\n", -1);
        for (String raw : rawLines) {
            result.add(parseSegments(raw));
        }
        return result;
    }

    private List<Segment> parseSegments(String line) {
        List<Segment> segs = new ArrayList<>();
        int i = 0;
        while (i < line.length()) {
            if (line.startsWith("**", i)) {
                int end = line.indexOf("**", i + 2);
                if (end != -1) {
                    segs.add(new Segment(line.substring(i + 2, end), true));
                    i = end + 2;
                } else {
                    segs.add(new Segment(line.substring(i), false));
                    break;
                }
            } else {
                int next = line.indexOf("**", i);
                String chunk = next == -1 ? line.substring(i) : line.substring(i, next);
                if (!chunk.isEmpty()) segs.add(new Segment(chunk, false));
                i = next == -1 ? line.length() : next;
            }
        }
        if (segs.isEmpty()) segs.add(new Segment("", false));
        return segs;
    }

    private int lineWidth(Graphics2D g2, List<Segment> segs) {
        int w = 0;
        for (Segment seg : segs) {
            Font f = seg.bold ? FONT_BOLD : FONT_BASE;
            for (int ci = 0; ci < seg.text.length(); ) {
                int cp = seg.text.codePointAt(ci);
                String ch = new String(Character.toChars(cp));
                Font chosen = isEmoji(cp) ? FONT_EMOJI : f;
                w += g2.getFontMetrics(chosen).stringWidth(ch);
                ci += Character.charCount(cp);
            }
        }
        return w;
    }

    private boolean isEmoji(int cp) {
        return (cp >= 0x1F000 && cp <= 0x1FFFF) ||
               (cp >= 0x2600  && cp <= 0x27BF)  ||
               (cp >= 0xFE00  && cp <= 0xFE0F)  ||
               (cp >= 0x2300  && cp <= 0x23FF);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(MAX_WIDTH + 10, 80);
    }

    private record Segment(String text, boolean bold) {}
}
