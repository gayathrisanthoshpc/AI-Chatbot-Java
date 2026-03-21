package chatbot.intelligence;

import chatbot.service.WebApiService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ORYN DailyDigest — generates a personalised daily briefing
 * shown once per day on first open.
 */
public class DailyDigest {

    /**
     * Generates a full morning/day briefing message.
     * Includes: greeting, date, time-aware message, top interest callback,
     * a random fact or quote, and a prompt.
     */
    public static String generate(String userName, LongMemory memory, BondSystem bond) {
        StringBuilder sb = new StringBuilder();

        String name = userName.isEmpty() ? "" : ", **" + capitalise(userName) + "**";
        String timeGreet = getTimeGreeting();
        String dateStr   = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"));

        // Opening
        sb.append(timeGreet).append(name).append("! 🌅\n");
        sb.append("Today is **").append(dateStr).append("**.\n\n");

        // Bond-aware message
        int lvl = bond.getLevel();
        if (lvl >= 2 && !userName.isEmpty()) {
            sb.append("Great to have you back — it's always brighter when you're here. ").append(bond.getLevelEmoji()).append("\n\n");
        } else if (lvl >= 1) {
            sb.append("ORYN has been waiting to chat! ✨\n\n");
        }

        // Interest callback
        String interests = memory.getTopInterests();
        if (interests != null && !interests.isBlank()) {
            sb.append("📌 Last time we explored: **").append(interests).append("**\n");
            sb.append("Want to continue, or discover something new?\n\n");
        }

        // Daily tip based on time
        sb.append(getDailyTip()).append("\n\n");

        // Stats
        if (memory.totalMessages > 0) {
            sb.append("💬 We've shared **").append(memory.totalMessages)
              .append("** messages together");
            if (lvl > 0) sb.append(" — Bond level: **").append(bond.getLevelName()).append("** ").append(bond.getLevelEmoji());
            sb.append(".\n\n");
        }

        sb.append("What shall we explore today? 🌟");

        return sb.toString();
    }

    private static String getTimeGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 5)  return "You're up late 🌙 Good night";
        if (hour < 12) return "Good morning ☀️";
        if (hour < 17) return "Good afternoon 🌤";
        if (hour < 21) return "Good evening 🌆";
        return "Good night 🌙";
    }

    private static String getDailyTip() {
        String[] tips = {
            "💡 **Tip:** Ask me 'tell me about [any topic]' for a Wikipedia summary!",
            "🧠 **Did you know?** Type 'trivia' to test your knowledge!",
            "🌤 **Tip:** Ask 'weather in [your city]' for today's forecast.",
            "💬 **Tip:** Type 'debate [any topic]' and I'll argue both sides!",
            "🏆 **Tip:** Type 'my score' to see your ORYN learning score!",
            "🎯 **Tip:** Say 'focus mode' to enter deep focus — I'll keep you on track.",
            "📖 **Tip:** Type 'remember that [anything]' and I'll save it for next time.",
            "😄 **Tip:** Try saying 'knock knock' — you might be surprised!",
            "🔍 **Tip:** Use the search icon 🔍 in the header to search through our chat.",
            "⚙️ **Tip:** Click the settings gear to customise font size, theme and sounds."
        };
        return tips[(int)(Math.random() * tips.length)];
    }

    private static String capitalise(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
