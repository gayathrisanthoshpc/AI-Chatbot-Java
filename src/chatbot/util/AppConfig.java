package chatbot.util;

import java.awt.*;

/**
 * ORYN "Rose Noir" Glassmorphism Theme v4.1
 * Near-black base + rose gold accents + frosted glass bubbles
 */
public class AppConfig {

    public static final String BOT_NAME    = "ORYN";
    public static final String BOT_TAGLINE = "Light of Knowledge";
    public static final String VERSION     = "4.1";

    public static final int WINDOW_WIDTH  = 640;
    public static final int WINDOW_HEIGHT = 800;

    private static boolean darkMode = true;
    public static boolean isDark()        { return darkMode; }
    public static void setDark(boolean d) { darkMode = d; }

    // ── Rose Noir Dark Palette ─────────────────────────────────────────────────
    // Background layers — very dark, warm-tinted
    public static Color BG_DARK()        { return darkMode ? new Color(10, 8, 12)       : new Color(248, 244, 250); }
    public static Color BG_PANEL()       { return darkMode ? new Color(16, 12, 20)      : new Color(240, 235, 245); }
    public static Color BG_INPUT()       { return darkMode ? new Color(22, 16, 28)      : new Color(255, 252, 255); }

    // Glass bubble bases — semi-transparent feel
    public static Color BG_BOT_BUBBLE()  { return darkMode ? new Color(30, 20, 38, 200) : new Color(240, 230, 248, 220); }
    public static Color BG_USER_BUBBLE() { return darkMode ? new Color(80, 30, 50, 210) : new Color(200, 100, 130, 180); }

    // Glass border colors
    public static Color GLASS_BORDER_BOT()  { return darkMode ? new Color(180, 100, 140, 60) : new Color(180, 80, 120, 80); }
    public static Color GLASS_BORDER_USER() { return darkMode ? new Color(220, 140, 170, 80) : new Color(200, 80, 110, 100); }

    // Subtle background border
    public static Color BORDER_SUBTLE()  { return darkMode ? new Color(60, 35, 70)     : new Color(210, 190, 220); }

    // Text
    public static Color TEXT_PRIMARY()   { return darkMode ? new Color(245, 235, 242)  : new Color(25, 15, 30);    }
    public static Color TEXT_SECONDARY() { return darkMode ? new Color(160, 110, 140)  : new Color(130, 80, 110);  }
    public static Color TEXT_USER()      { return darkMode ? new Color(255, 240, 245)  : new Color(255, 245, 248); }

    // ── Rose Gold Accent System ────────────────────────────────────────────────
    public static final Color ACCENT           = new Color(212, 120, 155);  // rose gold
    public static final Color ACCENT_BRIGHT    = new Color(255, 160, 190);  // bright rose
    public static final Color ACCENT_GLOW      = new Color(255, 180, 210);  // light rose glow
    public static final Color ACCENT_DEEP      = new Color(160, 60, 100);   // deep rose
    public static final Color ACCENT_GOLD      = new Color(230, 180, 120);  // warm gold
    public static final Color ACCENT_GOLD_SOFT = new Color(255, 215, 160);  // soft gold

    // Header gradient colors
    public static final Color HEADER_LEFT  = new Color(18, 10, 25);
    public static final Color HEADER_RIGHT = new Color(25, 10, 18);

    // Glow colors for particles and effects
    public static final Color GLOW_ROSE    = new Color(212, 120, 155, 40);
    public static final Color GLOW_GOLD    = new Color(230, 180, 120, 35);

    // ── Typography ────────────────────────────────────────────────────────────
    private static int fontSize = 14;
    public static int  getFontSize()      { return fontSize; }
    public static void setFontSize(int s) { fontSize = s; refreshFonts(); }

    public static Font FONT_MESSAGE;
    public static Font FONT_TIMESTAMP;
    public static Font FONT_HEADER;
    public static Font FONT_INPUT;
    public static Font FONT_BUTTON;
    public static Font FONT_LABEL;

    static { refreshFonts(); }

    public static void refreshFonts() {
        FONT_MESSAGE   = new Font("Segoe UI", Font.PLAIN,  fontSize);
        FONT_TIMESTAMP = new Font("Segoe UI", Font.PLAIN,  fontSize - 3);
        FONT_HEADER    = new Font("Segoe UI", Font.BOLD,   fontSize + 4);
        FONT_INPUT     = new Font("Segoe UI", Font.PLAIN,  fontSize);
        FONT_BUTTON    = new Font("Segoe UI", Font.BOLD,   fontSize);
        FONT_LABEL     = new Font("Segoe UI", Font.BOLD,   fontSize - 2);
    }

    // ── Bubble sizing ─────────────────────────────────────────────────────────
    public static final int BUBBLE_PADDING   = 14;
    public static final int BUBBLE_RADIUS    = 20;
    public static final int MAX_BUBBLE_WIDTH = 420;
    public static final int TYPING_DELAY_MS  = 600;

    private AppConfig() {}
}
