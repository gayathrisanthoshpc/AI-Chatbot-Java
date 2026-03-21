package chatbot.util;

import java.awt.*;

/**
 * ORYN "Aether" Theme — deep void black + aurora teal-to-gold + bioluminescent glow.
 * Unique color language inspired by deep ocean bioluminescence meets northern lights.
 */
public class AppConfig {

    // ── Identity ──────────────────────────────────────────────────────────────
    public static final String BOT_NAME    = "ORYN";
    public static final String BOT_TAGLINE = "Light of Knowledge";
    public static final String VERSION     = "2.0";

    // ── Window ────────────────────────────────────────────────────────────────
    public static final int WINDOW_WIDTH  = 540;
    public static final int WINDOW_HEIGHT = 720;

    // ── Aether Palette ────────────────────────────────────────────────────────
    public static final Color BG_DARK        = new Color(6,   8,  14);    // void black
    public static final Color BG_PANEL       = new Color(10,  14,  22);   // deep abyss
    public static final Color BG_INPUT       = new Color(14,  20,  34);   // dark navy well
    public static final Color BG_BOT_BUBBLE  = new Color(10,  28,  38);   // deep teal void
    public static final Color BG_USER_BUBBLE = new Color(140,  80,  20);  // warm ember amber
    public static final Color BORDER_SUBTLE  = new Color(20,  50,  60);   // teal-dark border

    // Aurora gradient endpoints (used in header)
    public static final Color AURORA_START   = new Color(0,  180, 160);   // deep teal
    public static final Color AURORA_MID     = new Color(0,  210, 180);   // bioluminescent cyan
    public static final Color AURORA_END     = new Color(220, 160,  40);  // aurora gold

    // Glow / accent colors
    public static final Color ACCENT         = new Color(0,  200, 170);   // bioluminescent teal
    public static final Color ACCENT_GLOW    = new Color(100, 240, 200);  // bright glow
    public static final Color ACCENT_AMBER   = new Color(255, 180,  60);  // amber glow
    public static final Color GLOW_SOFT      = new Color(0,  180, 150,  60); // translucent glow

    // Text
    public static final Color TEXT_PRIMARY   = new Color(220, 245, 240);  // soft bioluminescent white
    public static final Color TEXT_SECONDARY = new Color(80,  160, 150);  // muted teal
    public static final Color TEXT_USER      = new Color(255, 240, 210);  // warm cream

    // ── Typography ────────────────────────────────────────────────────────────
    public static final Font FONT_MESSAGE    = new Font("Segoe UI", Font.PLAIN,  14);
    public static final Font FONT_TIMESTAMP  = new Font("Segoe UI", Font.PLAIN,  11);
    public static final Font FONT_HEADER     = new Font("Segoe UI", Font.BOLD,   17);
    public static final Font FONT_INPUT      = new Font("Segoe UI", Font.PLAIN,  14);
    public static final Font FONT_BUTTON     = new Font("Segoe UI", Font.BOLD,   13);

    // ── Bubble sizing ─────────────────────────────────────────────────────────
    public static final int BUBBLE_PADDING   = 13;
    public static final int BUBBLE_RADIUS    = 18;
    public static final int MAX_BUBBLE_WIDTH = 350;

    // ── Timing ────────────────────────────────────────────────────────────────
    public static final int TYPING_DELAY_MS  = 600;

    private AppConfig() {}
}
