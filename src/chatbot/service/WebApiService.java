package chatbot.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Free public API integrations — all with offline fallback.
 * APIs: wttr.in, official-joke-api, numbersapi, quotable, wikipedia, opentdb
 */
public class WebApiService {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();

    // ── Weather ───────────────────────────────────────────────────────────────
    public static String getWeather(String city) {
        try {
            String body = get("https://wttr.in/" + city.replace(" ", "+") + "?format=3");
            if (body != null && !body.isBlank()) return "🌤 " + body.trim();
        } catch (Exception ignored) {}
        return "⚠ Couldn't fetch weather right now. Check your connection.";
    }

    // ── Joke ──────────────────────────────────────────────────────────────────
    public static String getJoke() {
        try {
            String body = get("https://official-joke-api.appspot.com/random_joke");
            if (body != null) {
                String setup     = extract(body, "\"setup\":\"",     "\"");
                String punchline = extract(body, "\"punchline\":\"", "\"");
                if (setup != null && punchline != null)
                    return "😄 " + setup + "\n👉 " + punchline;
            }
        } catch (Exception ignored) {}
        String[] fallback = {
            "Why do programmers prefer dark mode? Because light attracts bugs! 🐛",
            "I told my computer I needed a break. Now it won't stop sending me Kit-Kat ads.",
            "Why did the developer go broke? Because he used up all his cache! 💸"
        };
        return "😄 " + fallback[(int)(Math.random() * fallback.length)];
    }

    // ── Number Fact ───────────────────────────────────────────────────────────
    public static String getNumberFact(int number) {
        try {
            String body = get("http://numbersapi.com/" + number + "/trivia");
            if (body != null && !body.isBlank()) return "🔢 " + body.trim();
        } catch (Exception ignored) {}
        return "🔢 " + number + " is a perfectly respectable number!";
    }

    // ── Quote ─────────────────────────────────────────────────────────────────
    public static String getQuote() {
        try {
            String body = get("https://api.quotable.io/random");
            if (body != null) {
                String content = extract(body, "\"content\":\"", "\"");
                String author  = extract(body, "\"author\":\"",  "\"");
                if (content != null && author != null)
                    return "💬 \"" + content + "\"\n  — " + author;
            }
        } catch (Exception ignored) {}
        String[] fallback = {
            "\"The only way to do great work is to love what you do.\" — Steve Jobs",
            "\"In the middle of difficulty lies opportunity.\" — Albert Einstein",
            "\"Knowledge is the light that never dims.\" — ORYN"
        };
        return "💬 " + fallback[(int)(Math.random() * fallback.length)];
    }

    // ── Wikipedia Summary ─────────────────────────────────────────────────────
    public static String getWikipedia(String topic) {
        try {
            // Wikipedia REST API uses underscores, not %20
            String encoded = topic.trim().replace(" ", "_");
            encoded = URLEncoder.encode(encoded, StandardCharsets.UTF_8).replace("%5F","_");
            String body    = get("https://en.wikipedia.org/api/rest_v1/page/summary/" + encoded);
            if (body != null) {
                String extract = extract(body, "\"extract\":\"", "\"");
                String title   = extract(body, "\"title\":\"",   "\"");
                if (extract != null && !extract.isBlank()) {
                    // Unescape basic JSON escapes
                    extract = extract.replace("\\n", " ").replace("\\\"", "\"");
                    String shortSummary = extract.length() > 400
                        ? extract.substring(0, 400) + "..."
                        : extract;
                    return "📖 **" + (title != null ? title : topic) + "**\n" + shortSummary;
                }
            }
        } catch (Exception ignored) {}
        return "📖 I couldn't find a Wikipedia article on \"" + topic + "\". Try rephrasing?";
    }

    // ── Trivia ────────────────────────────────────────────────────────────────
    private static String pendingTriviaAnswer = null;
    private static String pendingTriviaQuestion = null;

    public static String getTrivia() {
        try {
            String body = get("https://opentdb.com/api.php?amount=1&type=multiple");
            if (body != null) {
                String question   = extract(body, "\"question\":\"",         "\"");
                String answer     = extract(body, "\"correct_answer\":\"",   "\"");
                String difficulty = extract(body, "\"difficulty\":\"",       "\"");
                if (question != null && answer != null) {
                    question = unescapeHtml(question);
                    answer   = unescapeHtml(answer);
                    pendingTriviaAnswer   = answer;
                    pendingTriviaQuestion = question;
                    String diff = difficulty != null ? " [" + difficulty + "]" : "";
                    return "🧠 Trivia" + diff + ":\n" + question + "\n\nType your answer — I'll check it!";
                }
            }
        } catch (Exception ignored) {}
        // Fallback trivia
        pendingTriviaAnswer   = "Paris";
        pendingTriviaQuestion = "What is the capital of France?";
        return "🧠 Trivia: What is the capital of France?\n\nType your answer!";
    }

    public static String checkTriviaAnswer(String userAnswer) {
        if (pendingTriviaAnswer == null) return null;
        boolean correct = pendingTriviaAnswer.equalsIgnoreCase(userAnswer.trim());
        String result;
        if (correct) {
            result = "✅ Correct! \"" + pendingTriviaAnswer + "\" is right! 🎉 Want another? Say 'trivia'!";
        } else {
            result = "❌ Not quite! The answer was: **" + pendingTriviaAnswer + "**\nWant to try another? Say 'trivia'!";
        }
        pendingTriviaAnswer   = null;
        pendingTriviaQuestion = null;
        return result;
    }

    public static boolean hasPendingTrivia() { return pendingTriviaAnswer != null; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(4))
                .GET().build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 200 ? res.body() : null;
    }

    private static String extract(String json, String startKey, String endChar) {
        int start = json.indexOf(startKey);
        if (start == -1) return null;
        start += startKey.length();
        int end = json.indexOf(endChar, start);
        return end == -1 ? null : json.substring(start, end);
    }

    private static String unescapeHtml(String s) {
        return s.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
                .replace("&quot;", "\"").replace("&#039;", "'").replace("&eacute;", "é")
                .replace("&rsquo;", "'").replace("&ldquo;", "\"").replace("&rdquo;", "\"");
    }
}
