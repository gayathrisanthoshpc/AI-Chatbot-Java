package chatbot.intelligence;

import chatbot.util.UserProfile;

/**
 * ORYN BondSystem — tracks relationship level between user and ORYN.
 *
 * Levels:
 *  0 — Stranger      (0–9 messages)
 *  1 — Acquaintance  (10–29)
 *  2 — Friend        (30–74)
 *  3 — Companion     (75–149)
 *  4 — Confidant     (150–299)
 *  5 — Trusted Soul  (300+)
 *
 * At higher levels ORYN:
 * - Uses your name more often
 * - Adds personal callbacks ("last time you asked about X...")
 * - Gets warmer and more playful
 * - Unlocks special responses
 */
public class BondSystem {

    private int totalMessages;
    private final UserProfile profile;

    private static final int[] THRESHOLDS = {0, 10, 30, 75, 150, 300};
    private static final String[] LEVEL_NAMES = {
        "Stranger", "Acquaintance", "Friend", "Companion", "Confidant", "Trusted Soul"
    };
    private static final String[] LEVEL_EMOJIS = {
        "👋", "🤝", "😊", "🌟", "💫", "✨"
    };

    public BondSystem(UserProfile profile, int totalMessages) {
        this.profile       = profile;
        this.totalMessages = totalMessages;
    }

    public void increment() {
        totalMessages++;
        profile.save();
    }

    public int getLevel() {
        for (int i = THRESHOLDS.length - 1; i >= 0; i--) {
            if (totalMessages >= THRESHOLDS[i]) return i;
        }
        return 0;
    }

    public String getLevelName()  { return LEVEL_NAMES[getLevel()];  }
    public String getLevelEmoji() { return LEVEL_EMOJIS[getLevel()]; }
    public int    getMessages()   { return totalMessages; }

    public int getNextThreshold() {
        int lvl = getLevel();
        return lvl < THRESHOLDS.length - 1 ? THRESHOLDS[lvl + 1] : -1;
    }

    public int getProgressToNext() {
        int lvl = getLevel();
        if (lvl >= THRESHOLDS.length - 1) return 100;
        int from = THRESHOLDS[lvl];
        int to   = THRESHOLDS[lvl + 1];
        return (int)(((double)(totalMessages - from) / (to - from)) * 100);
    }

    /** True if user just levelled up this message */
    public boolean justLevelledUp() {
        for (int t : THRESHOLDS) {
            if (totalMessages == t && t > 0) return true;
        }
        return false;
    }

    /** Get a level-up celebration message */
    public String getLevelUpMessage() {
        int lvl = getLevel();
        String name = LEVEL_NAMES[lvl];
        return switch (lvl) {
            case 1 -> "🤝 We're getting to know each other! You're now an **Acquaintance** — I'll start remembering more about you.";
            case 2 -> "😊 We've talked enough to call each other **Friends**! I'll be a bit more personal from now on.";
            case 3 -> "🌟 **Companion** level reached! I genuinely look forward to our conversations now.";
            case 4 -> "💫 You're a **Confidant** now — one of very few people ORYN has talked this much with. That means something.";
            case 5 -> "✨ **Trusted Soul** — the highest bond level. ORYN is truly honoured to know you this well.";
            default -> "Bond level up! Now: " + name;
        };
    }

    /**
     * Returns a personalised greeting modifier based on bond level.
     * Higher levels = warmer, more personal.
     */
    public String getGreetingFlavour(String userName) {
        String name = userName.isEmpty() ? "" : ", " + capitalise(userName);
        return switch (getLevel()) {
            case 0 -> "Hello" + name + "!";
            case 1 -> "Hey" + name + "! Good to see you again.";
            case 2 -> "Hey" + name + "! Glad you're back 😊";
            case 3 -> "Welcome back" + name + "! I missed our chats 🌟";
            case 4 -> (userName.isEmpty() ? "There you are!" : "There you are, " + capitalise(userName) + "!") + " 💫";
            case 5 -> (userName.isEmpty() ? "My favourite person" : capitalise(userName)) + " ✨ — ORYN is always happier when you're here.";
            default -> "Hello" + name + "!";
        };
    }

    /** Should ORYN use the user's name in this message? (more often at higher levels) */
    public boolean shouldUseName() {
        int lvl = getLevel();
        double chance = switch (lvl) {
            case 0 -> 0.1;
            case 1 -> 0.2;
            case 2 -> 0.35;
            case 3 -> 0.5;
            case 4 -> 0.65;
            case 5 -> 0.8;
            default -> 0.1;
        };
        return Math.random() < chance;
    }

    private String capitalise(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
