package chatbot.intelligence;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/**
 * ORYN LongMemory — persists facts, interests, and context
 * across sessions in ~/ORYN_Chats/memory.json
 *
 * Stores:
 * - User facts (name, age, job, location...)
 * - Interests (topics user frequently asks about)
 * - Last session summary
 * - Topic frequency map
 * - Last seen date
 */
public class LongMemory {

    private static final String DIR  = System.getProperty("user.home") + "/ORYN_Chats";
    private static final String FILE = DIR + "/memory.json";

    // In-memory state
    public  String              userName    = "";
    public  Map<String, String> facts       = new LinkedHashMap<>(); // key→value facts
    public  Map<String, Integer>interests  = new LinkedHashMap<>(); // topic→count
    public  List<String>        recentTopics= new ArrayList<>();
    public  String              lastSeen    = "";
    public  int                 totalMessages = 0;
    private String              lastTopic   = "";

    // ── Load ──────────────────────────────────────────────────────────────────

    public static LongMemory load() {
        LongMemory m = new LongMemory();
        try {
            if (!Files.exists(Paths.get(FILE))) return m;
            String json = Files.readString(Paths.get(FILE));
            m.userName      = readStr(json, "userName");
            m.lastSeen      = readStr(json, "lastSeen");
            m.totalMessages = readInt(json, "totalMessages");

            // Parse facts block
            String factsBlock = readBlock(json, "facts");
            if (factsBlock != null) m.facts = parseMap(factsBlock);

            // Parse interests block
            String intBlock = readBlock(json, "interests");
            if (intBlock != null) {
                for (Map.Entry<String,String> e : parseMap(intBlock).entrySet()) {
                    try { m.interests.put(e.getKey(), Integer.parseInt(e.getValue())); }
                    catch (Exception ignored) {}
                }
            }

            // Parse recent topics
            String topicsBlock = readArr(json, "recentTopics");
            if (topicsBlock != null) {
                for (String t : topicsBlock.split(",")) {
                    String clean = t.trim().replace("\"","");
                    if (!clean.isEmpty()) m.recentTopics.add(clean);
                }
            }

        } catch (Exception ignored) {}
        return m;
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    public void save() {
        try {
            Files.createDirectories(Paths.get(DIR));
            StringBuilder sb = new StringBuilder("{\n");
            sb.append("  \"userName\": \"").append(esc(userName)).append("\",\n");
            sb.append("  \"lastSeen\": \"").append(LocalDate.now()).append("\",\n");
            sb.append("  \"totalMessages\": ").append(totalMessages).append(",\n");

            // Facts
            sb.append("  \"facts\": {\n");
            List<String> fk = new ArrayList<>(facts.keySet());
            for (int i = 0; i < fk.size(); i++) {
                sb.append("    \"").append(esc(fk.get(i))).append("\": \"")
                  .append(esc(facts.get(fk.get(i)))).append("\"");
                if (i < fk.size()-1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  },\n");

            // Interests — top 20
            sb.append("  \"interests\": {\n");
            List<Map.Entry<String,Integer>> sorted = new ArrayList<>(interests.entrySet());
            sorted.sort((a,b)->b.getValue()-a.getValue());
            int limit = Math.min(sorted.size(), 20);
            for (int i = 0; i < limit; i++) {
                sb.append("    \"").append(esc(sorted.get(i).getKey())).append("\": ")
                  .append(sorted.get(i).getValue());
                if (i < limit-1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  },\n");

            // Recent topics — last 10
            sb.append("  \"recentTopics\": [");
            int start = Math.max(0, recentTopics.size()-10);
            for (int i = start; i < recentTopics.size(); i++) {
                sb.append("\"").append(esc(recentTopics.get(i))).append("\"");
                if (i < recentTopics.size()-1) sb.append(", ");
            }
            sb.append("]\n}\n");

            Files.writeString(Paths.get(FILE), sb.toString());
        } catch (Exception ignored) {}
    }

    // ── Track topic ───────────────────────────────────────────────────────────

    public void trackTopic(String topic) {
        if (topic == null || topic.isBlank()) return;
        lastTopic = topic;
        interests.merge(topic.toLowerCase(), 1, Integer::sum);
        recentTopics.add(topic);
        if (recentTopics.size() > 50) recentTopics.remove(0);
        totalMessages++;
    }

    public void storeFact(String key, String value) {
        facts.put(key.toLowerCase(), value);
    }

    /** Returns top 3 interests as a comma-separated string */
    public String getTopInterests() {
        if (interests.isEmpty()) return null;
        List<Map.Entry<String,Integer>> sorted = new ArrayList<>(interests.entrySet());
        sorted.sort((a,b)->b.getValue()-a.getValue());
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(3, sorted.size());
        for (int i = 0; i < limit; i++) {
            if (i > 0) sb.append(", ");
            sb.append(sorted.get(i).getKey());
        }
        return sb.toString();
    }

    /** Returns true if this is user's first session today */
    public boolean isFirstTodaySession() {
        return !LocalDate.now().toString().equals(lastSeen);
    }

    public String getLastTopic() { return lastTopic; }

    // ── JSON helpers ──────────────────────────────────────────────────────────

    private static String readStr(String json, String key) {
        String k = "\"" + key + "\": \"";
        int s = json.indexOf(k);
        if (s == -1) return "";
        s += k.length();
        int e = json.indexOf("\"", s);
        return e == -1 ? "" : json.substring(s, e);
    }

    private static int readInt(String json, String key) {
        String k = "\"" + key + "\": ";
        int s = json.indexOf(k);
        if (s == -1) return 0;
        s += k.length();
        int e = s;
        while (e < json.length() && Character.isDigit(json.charAt(e))) e++;
        try { return Integer.parseInt(json.substring(s, e)); } catch (Exception ex) { return 0; }
    }

    private static String readBlock(String json, String key) {
        String k = "\"" + key + "\": {";
        int s = json.indexOf(k);
        if (s == -1) return null;
        s += k.length() - 1;
        int depth = 0, i = s;
        while (i < json.length()) {
            if (json.charAt(i)=='{') depth++;
            else if (json.charAt(i)=='}') { depth--; if(depth==0) return json.substring(s+1,i); }
            i++;
        }
        return null;
    }

    private static String readArr(String json, String key) {
        String k = "\"" + key + "\": [";
        int s = json.indexOf(k);
        if (s == -1) return null;
        s += k.length();
        int e = json.indexOf("]", s);
        return e == -1 ? null : json.substring(s, e);
    }

    private static Map<String,String> parseMap(String block) {
        Map<String,String> map = new LinkedHashMap<>();
        String[] pairs = block.split(",\n");
        for (String pair : pairs) {
            String[] kv = pair.split("\":\\s*\"?");
            if (kv.length >= 2) {
                String k = kv[0].trim().replace("\"","");
                String v = kv[1].trim().replace("\"","");
                if (!k.isEmpty()) map.put(k, v);
            }
        }
        return map;
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\","\\\\").replace("\"","\\\"");
    }
}
