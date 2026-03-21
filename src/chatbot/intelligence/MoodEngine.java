package chatbot.intelligence;

import java.util.*;

/**
 * ORYN MoodEngine — detects emotional state from user input
 * and provides tone-adapted response prefixes/suffixes.
 *
 * Moods: NEUTRAL, HAPPY, SAD, FRUSTRATED, CURIOUS, PLAYFUL, STRESSED, EXCITED
 */
public class MoodEngine {

    public enum Mood {
        NEUTRAL, HAPPY, SAD, FRUSTRATED, CURIOUS, PLAYFUL, STRESSED, EXCITED
    }

    private Mood currentMood  = Mood.NEUTRAL;
    private Mood previousMood = Mood.NEUTRAL;
    private int  moodStreak   = 0; // how many messages in same mood

    // ── Mood keyword maps ─────────────────────────────────────────────────────

    private static final Map<Mood, String[]> KEYWORDS = new EnumMap<>(Mood.class);
    static {
        KEYWORDS.put(Mood.HAPPY,      new String[]{"happy","great","awesome","amazing","love","wonderful","fantastic","joy","excited","yay","haha","lol","😊","😄","🎉","good","nice","perfect","excellent"});
        KEYWORDS.put(Mood.SAD,        new String[]{"sad","unhappy","depressed","lonely","miss","hurt","cry","crying","tears","heartbroken","down","low","hopeless","😢","😭","💔","disappointed","upset"});
        KEYWORDS.put(Mood.FRUSTRATED, new String[]{"angry","annoyed","frustrated","hate","ugh","stupid","useless","broken","wrong","argh","wtf","seriously","ugh","😤","😠","🤬","terrible","awful","worst"});
        KEYWORDS.put(Mood.CURIOUS,    new String[]{"why","how","what","when","where","who","explain","tell me","curious","wonder","interesting","fascinating","really","hmm","🤔","learn","know","understand"});
        KEYWORDS.put(Mood.PLAYFUL,    new String[]{"haha","lol","lmao","funny","joke","play","fun","silly","laugh","😂","🤣","😜","xd","hehe","prank","game","😝","bored","entertain"});
        KEYWORDS.put(Mood.STRESSED,   new String[]{"stress","stressed","overwhelmed","anxious","worried","nervous","panic","deadline","help","pressure","exam","test","tired","exhausted","cant","can't","😰","😥"});
        KEYWORDS.put(Mood.EXCITED,    new String[]{"wow","omg","incredible","unbelievable","mind blown","cant believe","can't believe","🤩","😲","🔥","epic","insane","whoa","woah","amazing","best","ever"});
    }

    // ── Tone-adapted prefixes per mood ────────────────────────────────────────

    private static final Map<Mood, String[]> PREFIXES = new EnumMap<>(Mood.class);
    static {
        PREFIXES.put(Mood.HAPPY,      new String[]{"That's wonderful to hear! ✨ ", "Love the energy! 🌟 ", "So glad you're in a great mood! 😊 "});
        PREFIXES.put(Mood.SAD,        new String[]{"I'm here for you 💙 ", "That sounds tough — I've got you. ", "I can sense you're going through something. "});
        PREFIXES.put(Mood.FRUSTRATED, new String[]{"I hear you — let's sort this out. ", "Take a breath — ORYN's on it. 💪 ", "Frustration noted! Let me help: "});
        PREFIXES.put(Mood.CURIOUS,    new String[]{"Great question! 🔍 ", "Love the curiosity! ✨ ", "Ooh, let's explore this: "});
        PREFIXES.put(Mood.PLAYFUL,    new String[]{"Haha, I like your energy! 😄 ", "Playful mode activated! 🎮 ", "Oh you're in that mood — let's go! 😜 "});
        PREFIXES.put(Mood.STRESSED,   new String[]{"Hey, breathe — ORYN's here. 🫂 ", "One thing at a time — let me help. ", "It's okay! We'll figure this out together. "});
        PREFIXES.put(Mood.EXCITED,    new String[]{"YES! The energy! 🔥 ", "I feel that excitement too! 🤩 ", "ORYN is excited with you! ⚡ "});
        PREFIXES.put(Mood.NEUTRAL,    new String[]{"", "", ""});
    }

    // ── Detect mood ───────────────────────────────────────────────────────────

    public Mood detect(String input) {
        String lower = input.toLowerCase();
        Map<Mood, Integer> scores = new EnumMap<>(Mood.class);

        for (Map.Entry<Mood, String[]> entry : KEYWORDS.entrySet()) {
            int score = 0;
            for (String kw : entry.getValue()) {
                if (lower.contains(kw)) score++;
            }
            if (score > 0) scores.put(entry.getKey(), score);
        }

        if (scores.isEmpty()) {
            // Gradual return to neutral
            if (moodStreak > 3) {
                previousMood = currentMood;
                currentMood  = Mood.NEUTRAL;
                moodStreak   = 0;
            }
            return currentMood;
        }

        Mood detected = scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .get().getKey();

        previousMood = currentMood;
        if (detected == currentMood) moodStreak++;
        else { currentMood = detected; moodStreak = 1; }

        return currentMood;
    }

    /** Get a tone-adapted prefix for the current mood */
    public String getPrefix() {
        String[] prefixes = PREFIXES.getOrDefault(currentMood, new String[]{""});
        return prefixes[(int)(Math.random() * prefixes.length)];
    }

    /** Only inject prefix if mood changed or it's strong */
    public boolean shouldInjectPrefix() {
        return currentMood != Mood.NEUTRAL &&
               (currentMood != previousMood || moodStreak == 1);
    }

    public Mood getCurrentMood()  { return currentMood; }
    public String getMoodEmoji()  {
        return switch (currentMood) {
            case HAPPY      -> "😊";
            case SAD        -> "💙";
            case FRUSTRATED -> "💪";
            case CURIOUS    -> "🔍";
            case PLAYFUL    -> "😄";
            case STRESSED   -> "🫂";
            case EXCITED    -> "🔥";
            default         -> "✦";
        };
    }

    public void reset() {
        currentMood  = Mood.NEUTRAL;
        previousMood = Mood.NEUTRAL;
        moodStreak   = 0;
    }
}
