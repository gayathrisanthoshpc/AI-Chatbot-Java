package chatbot.util;

import java.io.*;
import java.nio.file.*;

/**
 * Persists user profile (name, sound, theme) to ~/ORYN_Chats/profile.json
 * Manual JSON — no external libraries.
 */
public class UserProfile {

    private static final String PROFILE_DIR  = System.getProperty("user.home") + "/ORYN_Chats";
    private static final String PROFILE_FILE = PROFILE_DIR + "/profile.json";

    public String  userName  = "";
    public boolean soundOn   = true;
    public boolean darkMode  = true;
    public int     fontSize  = 14;

    // ── Save ──────────────────────────────────────────────────────────────────

    public void save() {
        try {
            Files.createDirectories(Paths.get(PROFILE_DIR));
            String json = "{\n" +
                "  \"userName\": \"" + escape(userName) + "\",\n" +
                "  \"soundOn\": "   + soundOn  + ",\n" +
                "  \"darkMode\": "  + darkMode  + ",\n" +
                "  \"fontSize\": "  + fontSize  + "\n" +
                "}";
            Files.writeString(Paths.get(PROFILE_FILE), json);
        } catch (IOException ignored) {}
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    public static UserProfile load() {
        UserProfile p = new UserProfile();
        try {
            if (!Files.exists(Paths.get(PROFILE_FILE))) return p;
            String json = Files.readString(Paths.get(PROFILE_FILE));
            p.userName = readString(json, "userName");
            p.soundOn  = readBool(json, "soundOn",  true);
            p.darkMode = readBool(json, "darkMode", true);
            p.fontSize = readInt (json, "fontSize", 14);
        } catch (Exception ignored) {}
        return p;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String readString(String json, String key) {
        String search = "\"" + key + "\": \"";
        int s = json.indexOf(search);
        if (s == -1) return "";
        s += search.length();
        int e = json.indexOf("\"", s);
        return e == -1 ? "" : json.substring(s, e);
    }

    private static boolean readBool(String json, String key, boolean def) {
        String search = "\"" + key + "\": ";
        int s = json.indexOf(search);
        if (s == -1) return def;
        s += search.length();
        return json.startsWith("true", s);
    }

    private static int readInt(String json, String key, int def) {
        String search = "\"" + key + "\": ";
        int s = json.indexOf(search);
        if (s == -1) return def;
        s += search.length();
        int e = s;
        while (e < json.length() && Character.isDigit(json.charAt(e))) e++;
        try { return Integer.parseInt(json.substring(s, e)); } catch (Exception ex) { return def; }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
