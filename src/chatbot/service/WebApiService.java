package chatbot.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Calls free public APIs. All methods return a fallback string if offline or API fails.
 */
public class WebApiService {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();

    // ── Weather ───────────────────────────────────────────────────────────────

    public static String getWeather(String city) {
        try {
            String url = "https://wttr.in/" + city.replace(" ", "+") + "?format=3";
            String body = get(url);
            if (body != null && !body.isBlank()) return "🌤 " + body.trim();
        } catch (Exception ignored) {}
        return "⚠ Couldn't fetch weather right now. Check your connection.";
    }

    // ── Joke ──────────────────────────────────────────────────────────────────

    public static String getJoke() {
        try {
            String body = get("https://official-joke-api.appspot.com/random_joke");
            if (body != null) {
                // Parse manually — no external JSON lib
                String setup   = extract(body, "\"setup\":\"",   "\"");
                String punchline = extract(body, "\"punchline\":\"", "\"");
                if (setup != null && punchline != null)
                    return "😄 " + setup + "\n👉 " + punchline;
            }
        } catch (Exception ignored) {}
        // Offline fallback jokes
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
        return "🔢 " + number + " is a perfectly respectable number! (Offline — no fact available)";
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
        // Offline fallback quotes
        String[] fallback = {
            "\"The only way to do great work is to love what you do.\" — Steve Jobs",
            "\"In the middle of difficulty lies opportunity.\" — Albert Einstein",
            "\"Knowledge is the light that never dims.\" — ORYN"
        };
        return "💬 " + fallback[(int)(Math.random() * fallback.length)];
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(4))
                .GET()
                .build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 200 ? res.body() : null;
    }

    /** Minimal JSON/text field extractor — no external library needed. */
    private static String extract(String json, String startKey, String endChar) {
        int start = json.indexOf(startKey);
        if (start == -1) return null;
        start += startKey.length();
        int end = json.indexOf(endChar, start);
        if (end == -1) return null;
        return json.substring(start, end);
    }
}
