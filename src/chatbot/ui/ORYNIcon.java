package chatbot.ui;

import chatbot.util.AppConfig;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * ORYN Logo Generator — creates the app icon programmatically.
 *
 * Design: A hexagonal rose-gold frame containing a glowing ✦ star,
 * with a subtle inner ring and gradient fill.
 * Renders at any size — used for window icon and header avatar.
 */
public class ORYNIcon {

    /**
     * Generate ORYN logo as a BufferedImage at given size.
     * @param size pixel size (e.g. 32, 64, 128, 256)
     */
    public static BufferedImage generate(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        float cx = size / 2f;
        float cy = size / 2f;
        float r  = size * 0.44f;

        // ── Outer glow shadow ─────────────────────────────────────────────────
        for (int i = 3; i >= 1; i--) {
            g2.setColor(new Color(180, 60, 100, 12 * i));
            g2.fillOval((int)(cx - r - i*2), (int)(cy - r - i*2),
                        (int)(r*2 + i*4),    (int)(r*2 + i*4));
        }

        // ── Background circle with gradient ──────────────────────────────────
        RadialGradientPaint bg = new RadialGradientPaint(
            new Point2D.Float(cx, cy * 0.85f), r,
            new float[]{0f, 0.5f, 0.85f, 1f},
            new Color[]{
                new Color(255, 230, 238),   // light rose center
                new Color(210, 110, 150),   // rose mid
                new Color(150, 50,  90),    // deep rose
                new Color(100, 25,  60)     // dark edge
            }
        );
        g2.setPaint(bg);
        g2.fillOval((int)(cx-r), (int)(cy-r), (int)(r*2), (int)(r*2));

        // ── Hexagonal accent ring ─────────────────────────────────────────────
        g2.setColor(new Color(255, 200, 220, 80));
        g2.setStroke(new BasicStroke(size * 0.025f));
        g2.draw(hexagon(cx, cy, r * 0.85f, 0));

        // ── Inner shimmer arc (top-left) ──────────────────────────────────────
        g2.setColor(new Color(255, 245, 250, 70));
        g2.setStroke(new BasicStroke(size * 0.018f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Arc2D shimmer = new Arc2D.Float(
            cx - r*0.6f, cy - r*0.65f,
            r*1.2f, r*1.2f,
            120, 80, Arc2D.OPEN);
        g2.draw(shimmer);

        // ── Top shimmer highlight ─────────────────────────────────────────────
        RadialGradientPaint shine = new RadialGradientPaint(
            new Point2D.Float(cx - r*0.15f, cy - r*0.35f), r*0.45f,
            new float[]{0f, 1f},
            new Color[]{new Color(255, 255, 255, 55), new Color(255, 255, 255, 0)}
        );
        g2.setPaint(shine);
        g2.fillOval((int)(cx-r), (int)(cy-r), (int)(r*2), (int)(r*2));

        // ── Outer border ring ─────────────────────────────────────────────────
        GradientPaint borderPaint = new GradientPaint(
            cx - r, cy - r, new Color(255, 180, 200, 180),
            cx + r, cy + r, new Color(230, 160, 100, 120));
        g2.setPaint(borderPaint);
        g2.setStroke(new BasicStroke(size * 0.03f));
        g2.drawOval((int)(cx-r)+1, (int)(cy-r)+1, (int)(r*2)-2, (int)(r*2)-2);

        // ── ORYN star symbol ✦ ────────────────────────────────────────────────
        float starSize = size * 0.38f;
        g2.setFont(new Font("Segoe UI", Font.BOLD, (int)starSize));
        FontMetrics fm = g2.getFontMetrics();
        String sym = "\u2736"; // ✶ six-pointed star

        // Shadow
        g2.setColor(new Color(80, 10, 40, 80));
        g2.drawString(sym,
            (int)(cx - fm.stringWidth(sym)/2f + size*0.02f),
            (int)(cy + fm.getAscent()/2f - fm.getDescent()/2f + size*0.02f));

        // Main star — rose gold gradient
        GradientPaint starPaint = new GradientPaint(
            cx - starSize/2, cy - starSize/2, new Color(255, 240, 248),
            cx + starSize/2, cy + starSize/2, new Color(230, 180, 120));
        g2.setPaint(starPaint);
        g2.drawString(sym,
            (int)(cx - fm.stringWidth(sym)/2f),
            (int)(cy + fm.getAscent()/2f - fm.getDescent()/2f));

        g2.dispose();
        return img;
    }

    /** Generate a list of icon sizes for the window taskbar */
    public static List<Image> generateIconList() {
        List<Image> icons = new ArrayList<>();
        for (int size : new int[]{16, 32, 48, 64, 128, 256}) {
            icons.add(generate(size));
        }
        return icons;
    }

    /** Draw a regular hexagon path */
    private static Shape hexagon(float cx, float cy, float r, float rotDeg) {
        Path2D path = new Path2D.Float();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(rotDeg + 60 * i);
            float x = cx + r * (float)Math.cos(angle);
            float y = cy + r * (float)Math.sin(angle);
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.closePath();
        return path;
    }
}
