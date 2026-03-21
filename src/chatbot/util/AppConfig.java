package chatbot.util;

import java.awt.*;

/**
 * ORYN "Aether" Theme v3.0 — supports Dark and Light modes.
 */
public class AppConfig {

    public static final String BOT_NAME    = "ORYN";
    public static final String BOT_TAGLINE = "Light of Knowledge";
    public static final String VERSION     = "3.0";

    public static final int WINDOW_WIDTH  = 560;
    public static final int WINDOW_HEIGHT = 720;

    private static boolean darkMode = true;
    public static boolean isDark()        { return darkMode; }
    public static void setDark(boolean d) { darkMode = d; }

    public static Color BG_DARK()        { return darkMode ? new Color(6,8,14)      : new Color(245,248,252); }
    public static Color BG_PANEL()       { return darkMode ? new Color(10,14,22)    : new Color(235,240,248); }
    public static Color BG_INPUT()       { return darkMode ? new Color(14,20,34)    : new Color(255,255,255); }
    public static Color BG_BOT_BUBBLE()  { return darkMode ? new Color(10,28,38)    : new Color(225,245,242); }
    public static Color BG_USER_BUBBLE() { return darkMode ? new Color(140,80,20)   : new Color(255,220,160); }
    public static Color BORDER_SUBTLE()  { return darkMode ? new Color(20,50,60)    : new Color(200,220,218); }
    public static Color TEXT_PRIMARY()   { return darkMode ? new Color(220,245,240) : new Color(20,40,38);    }
    public static Color TEXT_SECONDARY() { return darkMode ? new Color(80,160,150)  : new Color(80,130,125);  }
    public static Color TEXT_USER()      { return darkMode ? new Color(255,240,210) : new Color(80,40,10);    }

    public static final Color AURORA_START = new Color(0,180,160);
    public static final Color AURORA_END   = new Color(220,160,40);
    public static final Color ACCENT       = new Color(0,200,170);
    public static final Color ACCENT_GLOW  = new Color(100,240,200);
    public static final Color ACCENT_AMBER = new Color(255,180,60);

    private static int fontSize = 14;
    public static int  getFontSize()      { return fontSize; }
    public static void setFontSize(int s) { fontSize = s; refreshFonts(); }

    public static Font FONT_MESSAGE;
    public static Font FONT_TIMESTAMP;
    public static Font FONT_HEADER;
    public static Font FONT_INPUT;
    public static Font FONT_BUTTON;

    static { refreshFonts(); }

    public static void refreshFonts() {
        FONT_MESSAGE   = new Font("Segoe UI", Font.PLAIN, fontSize);
        FONT_TIMESTAMP = new Font("Segoe UI", Font.PLAIN, fontSize - 3);
        FONT_HEADER    = new Font("Segoe UI", Font.BOLD,  fontSize + 3);
        FONT_INPUT     = new Font("Segoe UI", Font.PLAIN, fontSize);
        FONT_BUTTON    = new Font("Segoe UI", Font.BOLD,  fontSize - 1);
    }

    public static final int BUBBLE_PADDING   = 13;
    public static final int BUBBLE_RADIUS    = 18;
    public static final int MAX_BUBBLE_WIDTH = 360;
    public static final int TYPING_DELAY_MS  = 600;

    private AppConfig() {}
}
