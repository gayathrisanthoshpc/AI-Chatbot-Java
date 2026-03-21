package chatbot.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

/**
 * ORYN's brain — hybrid rule-based + free web API intelligence.
 * Implements ChatService so the GUI never touches this logic directly.
 */
public class SmartChatBot implements ChatService {

    // ── Conversation Memory ───────────────────────────────────────────────────
    private String userName      = "";
    private String lastTopic     = "";
    private int    messageCount  = 0;
    private final Deque<String>  recentInputs = new ArrayDeque<>(5);
    private final Map<String, String> userFacts = new LinkedHashMap<>();

    // ── Greeting Rotator ─────────────────────────────────────────────────────
    private static final String[] GREETINGS = {
        "Hey! 😊 I'm ORYN — light of knowledge. How can I help?",
        "Hello there! ORYN at your service. What's on your mind?",
        "Hi! Great to see you. Ask me anything ✨",
        "Hey hey! ORYN here. What shall we explore today?"
    };
    private int greetIndex = 0;

    // ── Main Reply Logic ──────────────────────────────────────────────────────

    @Override
    public String getReply(String raw) {
        if (raw == null || raw.isBlank()) return "Go on, I'm listening... 👂";

        messageCount++;
        String input = raw.trim();
        String lower = input.toLowerCase();

        // Track recent context
        if (recentInputs.size() == 5) recentInputs.pollFirst();
        recentInputs.addLast(lower);

        // ── Identity & Name ───────────────────────────────────────────────────
        if (matches(lower, "who are you|what are you|your name|are you a bot|are you ai")) {
            return "I'm ORYN 🌟 — an AI assistant whose name means 'light of knowledge'. " +
                   "I'm here to chat, answer questions, and explore ideas with you!";
        }

        if (matches(lower, "my name is (.+)")) {
            userName = extractGroup(lower, "my name is (.+)");
            userFacts.put("name", userName);
            return "Lovely name, " + capitalize(userName) + "! I'll remember that 😊";
        }

        if (matches(lower, "call me (.+)")) {
            userName = extractGroup(lower, "call me (.+)");
            return "Sure thing, I'll call you " + capitalize(userName) + " from now on! 👋";
        }

        if (matches(lower, "who am i|what is my name|do you know my name")) {
            return userName.isEmpty()
                ? "I don't know your name yet. You can tell me with \"My name is ...\""
                : "You're " + capitalize(userName) + "! I remember 😊";
        }

        if (matches(lower, "i am (.+) years old|i'm (.+) years old")) {
            String age = extractGroup(lower, "(?:i am|i'm) (.+) years old");
            userFacts.put("age", age);
            return "Got it! You're " + age + " years old. I'll keep that in mind 📝";
        }

        // ── Greetings ─────────────────────────────────────────────────────────
        if (matches(lower, "^(hi|hello|hey|howdy|greetings|sup|what'?s up)(\\W.*)?$")) {
            String greeting = GREETINGS[greetIndex % GREETINGS.length];
            greetIndex++;
            return userName.isEmpty() ? greeting : greeting.replace("!", ", " + capitalize(userName) + "!");
        }

        if (matches(lower, "good morning|good afternoon|good evening|good night")) {
            String timeGreet = getTimeGreeting();
            return timeGreet + (userName.isEmpty() ? "!" : ", " + capitalize(userName) + "!");
        }

        // ── How Are You ───────────────────────────────────────────────────────
        if (matches(lower, "how are you|how r u|how do you do|you ok|you good")) {
            return "I'm running at full luminosity today! ✨ How about you?";
        }

        if (matches(lower, "i('?m| am) (fine|good|great|amazing|awesome|ok|okay|not bad|well)")) {
            String mood = extractGroup(lower, "(?:i'm|i am) (\\w+)");
            return "Glad to hear you're " + mood + "! 😊 What can I do for you?";
        }

        if (matches(lower, "i('?m| am) (sad|tired|bored|lonely|stressed|anxious|not good|not okay|bad)")) {
            return "I'm sorry to hear that 💙 Want to talk about it, or shall I tell you a joke to cheer you up?";
        }

        // ── Time & Date ───────────────────────────────────────────────────────
        if (matches(lower, "what.*(time|clock)|current time|tell me the time")) {
            return "🕐 It's currently " +
                   LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")) + ".";
        }

        if (matches(lower, "what.*(date|day)|today.*(date|day)|current date")) {
            return "📅 Today is " +
                   LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + ".";
        }

        // ── Math ──────────────────────────────────────────────────────────────
        if (matches(lower, "what is \\d[\\d\\s+\\-*/^.()]*|calculate|compute|solve|math")) {
            String expr = lower.replaceAll("what is|calculate|compute|solve|math", "").trim();
            String result = evaluateMath(expr);
            if (result != null) return "🧮 " + expr.trim() + " = **" + result + "**";
        }

        // Simple direct math expressions like "5 + 3" or "12 * 4"
        if (lower.matches("[\\d\\s+\\-*/^().]+")) {
            String result = evaluateMath(lower.trim());
            if (result != null) return "🧮 That equals **" + result + "**";
        }

        // ── Weather ───────────────────────────────────────────────────────────
        if (matches(lower, "weather|temperature|forecast|how.*outside|raining|sunny")) {
            String city = extractGroup(lower, "(?:weather|forecast|temperature)\\s+(?:in|at|for)?\\s*(.+)");
            if (city == null || city.isBlank()) {
                return "Which city would you like the weather for? (e.g. \"weather in London\")";
            }
            lastTopic = "weather";
            return WebApiService.getWeather(city.trim());
        }

        // ── Jokes ─────────────────────────────────────────────────────────────
        if (matches(lower, "tell.*joke|joke|make me laugh|funny|humor|laugh")) {
            lastTopic = "joke";
            return WebApiService.getJoke();
        }

        // ── Quotes ───────────────────────────────────────────────────────────
        if (matches(lower, "quote|inspire|motivat|wisdom|something wise")) {
            lastTopic = "quote";
            return WebApiService.getQuote();
        }

        // ── Number Facts ──────────────────────────────────────────────────────
        if (matches(lower, "fact about (\\d+)|number fact|tell me about number (\\d+)|(\\d+) fact")) {
            String numStr = extractGroup(lower, "(\\d+)");
            if (numStr != null) {
                lastTopic = "number";
                return WebApiService.getNumberFact(Integer.parseInt(numStr));
            }
        }

        // ── Remember / Recall ────────────────────────────────────────────────
        if (matches(lower, "remember (that )?(.+)|note (that )?(.+)|don't forget (.+)")) {
            String fact = extractGroup(lower, "(?:remember|note|don't forget)(?: that)?\\s+(.+)");
            if (fact != null) {
                userFacts.put("note_" + userFacts.size(), fact);
                return "Got it, I'll remember: \"" + fact + "\" 📝";
            }
        }

        if (matches(lower, "what do you remember|what did i tell you|my info|show notes")) {
            if (userFacts.isEmpty()) return "I haven't noted anything about you yet.";
            StringBuilder sb = new StringBuilder("Here's what I know about you:\n");
            userFacts.forEach((k, v) -> {
                if (!k.startsWith("note_")) sb.append("• ").append(capitalize(k)).append(": ").append(v).append("\n");
                else sb.append("• Note: ").append(v).append("\n");
            });
            return sb.toString().trim();
        }

        if (matches(lower, "forget (everything|all)|clear my data|reset memory")) {
            userFacts.clear();
            userName = "";
            return "Memory cleared! Starting fresh 🗑️";
        }

        // ── Help ─────────────────────────────────────────────────────────────
        if (matches(lower, "help|what can you do|commands|features|capabilities")) {
            return buildHelp();
        }

        // ── Thanks ───────────────────────────────────────────────────────────
        if (matches(lower, "thank|thanks|thx|ty|appreciate")) {
            return "You're welcome! 😊 Always here if you need me.";
        }

        // ── Bye ───────────────────────────────────────────────────────────────
        if (matches(lower, "bye|goodbye|see you|cya|exit|quit|take care")) {
            return "Goodbye" + (userName.isEmpty() ? "" : ", " + capitalize(userName)) +
                   "! 👋 The light of knowledge is always here when you return 🌟";
        }

        // ── Repeat / Context ─────────────────────────────────────────────────
        if (matches(lower, "repeat|say that again|what did you say")) {
            return "I said: " + (recentInputs.size() > 1 ? "\"" + recentInputs.toArray()[recentInputs.size()-2] + "\"" : "nothing yet!");
        }

        if (matches(lower, "more|tell me more|continue|go on|and\\??")) {
            return continueTopic();
        }

        // ── Milestone messages ────────────────────────────────────────────────
        if (messageCount == 10) return "We've been chatting for 10 messages! You're great company 😊";
        if (messageCount == 25) return "25 messages in — ORYN is fully warmed up! 🔥 Ask me anything.";

        // ── Default ───────────────────────────────────────────────────────────
        return getDefaultReply(lower);
    }

    @Override
    public void reset() {
        userName = "";
        lastTopic = "";
        messageCount = 0;
        recentInputs.clear();
        userFacts.clear();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean matches(String input, String pattern) {
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(input).find();
    }

    private String extractGroup(String input, String pattern) {
        Matcher m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(input);
        if (m.find() && m.groupCount() >= 1) {
            for (int i = 1; i <= m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isBlank()) return m.group(i).trim();
            }
        }
        return null;
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    private String getTimeGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good morning! ☀️";
        if (hour < 17) return "Good afternoon! 🌤";
        if (hour < 21) return "Good evening! 🌆";
        return "Good night! 🌙";
    }

    private String continueTopic() {
        return switch (lastTopic) {
            case "joke"    -> "Here's another one! " + WebApiService.getJoke();
            case "quote"   -> "One more for you: " + WebApiService.getQuote();
            case "weather" -> "Ask me about a specific city for weather details!";
            default        -> "Tell me more about what you'd like to know 😊";
        };
    }

    private String buildHelp() {
        return """
                🌟 Here's what ORYN can do:
                
                💬 Chat      — Say hi, tell me how you feel, have a conversation
                🕐 Time/Date — "What time is it?" / "What's today's date?"
                🧮 Math      — "What is 25 * 4?" or just type "12 + 8"
                🌤 Weather   — "Weather in Paris" (needs internet)
                😄 Jokes     — "Tell me a joke"
                💬 Quotes    — "Give me a quote" / "Inspire me"
                🔢 Numbers   — "Fact about 42"
                📝 Memory    — "My name is..." / "Remember that..."
                🗑️ Reset     — "Forget everything"
                ❓ Help      — You're looking at it!""";
    }

    private String getDefaultReply(String lower) {
        // Detect questions
        if (lower.contains("?") || lower.startsWith("what") || lower.startsWith("why")
                || lower.startsWith("how") || lower.startsWith("when") || lower.startsWith("who")) {
            String[] questionReplies = {
                "That's a great question! I'm still learning, but let's think about it together 🤔",
                "Hmm, that's deep. I don't have a definitive answer, but it's worth exploring!",
                "I'm not sure about that one yet. Try asking me something else or rephrase?"
            };
            return questionReplies[(int)(Math.random() * questionReplies.length)];
        }

        String[] defaults = {
            "Interesting... tell me more 🙂",
            "I'm listening! Can you elaborate?",
            "That's thought-provoking. What made you think of that?",
            "I see! Keep going, I'm all ears 👂",
            "Noted! Anything specific you'd like to know?"
        };
        return defaults[(int)(Math.random() * defaults.length)];
    }

    /** Very basic math evaluator — handles +, -, *, /, ^ without external libs */
    private String evaluateMath(String expr) {
        try {
            expr = expr.replaceAll("[^\\d+\\-*/^(). ]", "").trim();
            if (expr.isBlank()) return null;
            double result = new MathParser(expr).parse();
            if (result == Math.floor(result) && !Double.isInfinite(result))
                return String.valueOf((long) result);
            return String.format("%.4f", result).replaceAll("0+$", "").replaceAll("\\.$", "");
        } catch (Exception e) {
            return null;
        }
    }

    // ── Minimal recursive-descent math parser ────────────────────────────────

    private static class MathParser {
        private final String expr;
        private int pos = 0;

        MathParser(String expr) { this.expr = expr.replaceAll("\\s", ""); }

        double parse() {
            double result = parseExpr();
            if (pos < expr.length()) throw new RuntimeException("Unexpected: " + expr.charAt(pos));
            return result;
        }

        private double parseExpr() {
            double x = parseTerm();
            while (pos < expr.length() && (expr.charAt(pos) == '+' || expr.charAt(pos) == '-')) {
                char op = expr.charAt(pos++);
                double y = parseTerm();
                x = op == '+' ? x + y : x - y;
            }
            return x;
        }

        private double parseTerm() {
            double x = parsePower();
            while (pos < expr.length() && (expr.charAt(pos) == '*' || expr.charAt(pos) == '/')) {
                char op = expr.charAt(pos++);
                double y = parsePower();
                x = op == '*' ? x * y : x / y;
            }
            return x;
        }

        private double parsePower() {
            double x = parseUnary();
            if (pos < expr.length() && expr.charAt(pos) == '^') {
                pos++;
                x = Math.pow(x, parsePower());
            }
            return x;
        }

        private double parseUnary() {
            if (pos < expr.length() && expr.charAt(pos) == '-') { pos++; return -parseAtom(); }
            if (pos < expr.length() && expr.charAt(pos) == '+') { pos++; }
            return parseAtom();
        }

        private double parseAtom() {
            if (pos < expr.length() && expr.charAt(pos) == '(') {
                pos++;
                double x = parseExpr();
                if (pos < expr.length() && expr.charAt(pos) == ')') pos++;
                return x;
            }
            int start = pos;
            while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.')) pos++;
            return Double.parseDouble(expr.substring(start, pos));
        }
    }
}
