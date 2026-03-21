package chatbot.util;

import java.awt.*;

/**
 * Central config for ORYN — colors, fonts, sizing.
 * Change values here to retheme the entire app.
 */
public class AppConfig {

    // ── Identity ──────────────────────────────────────────────────────────────
    public static final String BOT_NAME    = "ORYN";
    public static final String BOT_TAGLINE = "Light of Knowledge";
    public static final String VERSION     = "2.0";

    // ── Window ────────────────────────────────────────────────────────────────
    public static final int WINDOW_WIDTH  = 520;
    public static final int WINDOW_HEIGHT = 700;

    // ── Dark Theme Colors ─────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(18,  18,  24);   // main background
    public static final Color BG_PANEL      = new Color(26,  26,  36);   // panel/sidebar
    public static final Color BG_INPUT      = new Color(36,  36,  50);   // input field bg
    public static final Color BG_BOT_BUBBLE = new Color(38,  38,  58);   // bot message card
    public static final Color BG_USER_BUBBLE= new Color(79,  70, 229);   // user bubble (indigo)
    public static final Color ACCENT        = new Color(139, 92, 246);   // purple accent
    public static final Color ACCENT_GLOW   = new Color(167, 139, 250);  // lighter accent
    public static final Color TEXT_PRIMARY  = new Color(240, 240, 255);  // main text
    public static final Color TEXT_SECONDARY= new Color(148, 148, 180);  // timestamps / hints
    public static final Color TEXT_USER     = Color.WHITE;
    public static final Color BORDER_SUBTLE = new Color(50,  50,  70);

    // ── Typography ────────────────────────────────────────────────────────────
    public static final Font FONT_MESSAGE   = new Font("Segoe UI", Font.PLAIN,  14);
    public static final Font FONT_TIMESTAMP = new Font("Segoe UI", Font.PLAIN,  11);
    public static final Font FONT_HEADER    = new Font("Segoe UI", Font.BOLD,   16);
    public static final Font FONT_INPUT     = new Font("Segoe UI", Font.PLAIN,  14);
    public static final Font FONT_BUTTON    = new Font("Segoe UI", Font.BOLD,   13);

    // ── Bubble sizing ────────────────────────────────────────────────────────
    public static final int BUBBLE_PADDING  = 12;
    public static final int BUBBLE_RADIUS   = 16;
    public static final int MAX_BUBBLE_WIDTH = 340;

    // ── Timing ───────────────────────────────────────────────────────────────
    public static final int TYPING_DELAY_MS = 600;   // simulated typing pause

    private AppConfig() {}
}
