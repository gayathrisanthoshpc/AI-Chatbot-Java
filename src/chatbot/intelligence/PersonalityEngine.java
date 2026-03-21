package chatbot.intelligence;

/**
 * ORYN PersonalityEngine — 4 tone modes that change how ORYN speaks.
 * Default, Formal, Casual, Playful
 */
public class PersonalityEngine {

    public enum Mode { DEFAULT, FORMAL, CASUAL, PLAYFUL }

    private static Mode current = Mode.DEFAULT;

    public static Mode  getMode()        { return current; }
    public static void  setMode(Mode m)  { current = m; }
    public static String getModeName()   { return switch(current) {
        case FORMAL  -> "Formal";
        case CASUAL  -> "Casual";
        case PLAYFUL -> "Playful";
        default      -> "Default";
    };}

    /** Adapt a reply string to the current personality */
    public static String adapt(String reply) {
        return switch (current) {
            case FORMAL  -> formalise(reply);
            case CASUAL  -> casualise(reply);
            case PLAYFUL -> playify(reply);
            default      -> reply;
        };
    }

    /** Wrap a greeting to match personality */
    public static String greeting(String base) {
        return switch (current) {
            case FORMAL  -> "Good day. " + base;
            case CASUAL  -> "Hey! " + base;
            case PLAYFUL -> "Yooo! \uD83C\uDF89 " + base;
            default      -> base;
        };
    }

    public static String getModeEmoji() {
        return switch (current) {
            case FORMAL  -> "\uD83D\uDC54";
            case CASUAL  -> "\uD83D\uDE0E";
            case PLAYFUL -> "\uD83C\uDF89";
            default      -> "\u2736";
        };
    }

    // ── Transformers ──────────────────────────────────────────────────────────

    private static String formalise(String r) {
        return r
            .replace("Hey!", "Greetings.")
            .replace("hey!", "Greetings.")
            .replace("Hi!", "Good day.")
            .replace("Haha", "Indeed")
            .replace("haha", "indeed")
            .replace("lol", "")
            .replace("😄", "")
            .replace("😊", "")
            .replace("!", ".")
            .replace("..", ".");
    }

    private static String casualise(String r) {
        return r
            .replace("Greetings", "Hey")
            .replace("Indeed", "Totally")
            .replace("I would suggest", "I'd say")
            .replace("I am ", "I'm ")
            .replace("You are ", "You're ")
            .replace("cannot", "can't")
            .replace("do not", "don't")
            .replace("it is", "it's");
    }

    private static String playify(String r) {
        // Add playful suffixes randomly
        String[] suffixes = {" \uD83C\uDF89", " ✨", " \uD83D\uDE04", " \uD83D\uDD25", " \uD83C\uDF1F"};
        String suffix = suffixes[(int)(Math.random() * suffixes.length)];
        // Only add if reply doesn't already end with emoji-like char
        if (r.length() > 0 && r.charAt(r.length()-1) == '.') {
            return r.substring(0, r.length()-1) + suffix;
        }
        return r + suffix;
    }
}
