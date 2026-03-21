package chatbot.intelligence;

import java.util.Random;

/**
 * ORYN MiniGames — playable directly in chat.
 * Games: Word Guess, Number Game, Riddles
 */
public class MiniGames {

    public enum GameState { NONE, NUMBER_GUESS, WORD_GUESS, RIDDLE }

    private static GameState state       = GameState.NONE;
    private static int        secretNum  = 0;
    private static int        numAttempts = 0;
    private static int        numMin      = 1;
    private static int        numMax      = 100;
    private static String     secretWord  = "";
    private static String     wordHint    = "";
    private static int        wordAttempts = 0;
    private static int        riddleIndex = -1;
    private static final Random rng = new Random();

    public static boolean isPlaying() { return state != GameState.NONE; }
    public static GameState getState() { return state; }

    // ── Word list ─────────────────────────────────────────────────────────────
    private static final String[][] WORDS = {
        {"python",   "A programming language named after a snake"},
        {"galaxy",   "A massive system of stars in space"},
        {"keyboard", "You use this to type messages"},
        {"ocean",    "The largest body of water on Earth"},
        {"thunder",  "The loud sound during a storm"},
        {"library",  "A place where books are stored"},
        {"compass",  "A navigation tool that points north"},
        {"volcano",  "A mountain that can erupt with lava"},
        {"diamond",  "The hardest natural substance on Earth"},
        {"gravity",  "The force that keeps you on the ground"}
    };

    // ── Riddles ───────────────────────────────────────────────────────────────
    private static final String[][] RIDDLES = {
        {"I speak without a mouth and hear without ears. I have no body, but I come alive with the wind. What am I?", "echo"},
        {"The more you take, the more you leave behind. What am I?", "footsteps"},
        {"I have cities, but no houses live there. I have mountains, but no trees grow there. I have water, but no fish swim. What am I?", "map"},
        {"I can fly without wings. I can cry without eyes. Wherever I go, darkness follows me. What am I?", "cloud"},
        {"The person who makes it doesn't need it. The person who buys it doesn't use it. The person who uses it doesn't know it. What is it?", "coffin"},
        {"I have keys but no locks. I have space but no room. You can enter but can't go inside. What am I?", "keyboard"},
        {"What has hands but can't clap?", "clock"},
        {"What gets wetter the more it dries?", "towel"}
    };

    // ── Start games ───────────────────────────────────────────────────────────

    public static String startNumberGame() {
        state       = GameState.NUMBER_GUESS;
        numMin      = 1;
        numMax      = 100;
        secretNum   = rng.nextInt(100) + 1;
        numAttempts = 0;
        return "\uD83C\uDFAF **Number Guessing Game!**\n\n" +
               "I'm thinking of a number between **1 and 100**.\n" +
               "Can you guess it? Type any number!";
    }

    public static String startWordGame() {
        state = GameState.WORD_GUESS;
        int idx = rng.nextInt(WORDS.length);
        secretWord   = WORDS[idx][0];
        wordHint     = WORDS[idx][1];
        wordAttempts = 0;
        String blanks = "_".repeat(secretWord.length());
        return "\uD83D\uDD24 **Word Guessing Game!**\n\n" +
               "Hint: **" + wordHint + "**\n" +
               "Word: **" + blanks + "** (" + secretWord.length() + " letters)\n\n" +
               "Type your guess! (the full word)";
    }

    public static String startRiddle() {
        state = GameState.RIDDLE;
        riddleIndex = rng.nextInt(RIDDLES.length);
        return "\uD83E\uDDE0 **Riddle Time!**\n\n" +
               RIDDLES[riddleIndex][0] + "\n\n" +
               "_Type your answer..._";
    }

    // ── Handle game input ─────────────────────────────────────────────────────

    public static String handleInput(String input) {
        return switch (state) {
            case NUMBER_GUESS -> handleNumber(input);
            case WORD_GUESS   -> handleWord(input);
            case RIDDLE       -> handleRiddle(input);
            default           -> null;
        };
    }

    private static String handleNumber(String input) {
        try {
            int guess = Integer.parseInt(input.trim());
            numAttempts++;

            if (guess == secretNum) {
                state = GameState.NONE;
                String rating = numAttempts <= 5 ? "\uD83C\uDFC6 Outstanding!" :
                                numAttempts <= 8 ? "\uD83D\uDCAA Great job!" :
                                numAttempts <= 12 ? "\uD83D\uDE0A Not bad!" : "\uD83D\uDE05 Better luck next time!";
                return "\u2705 **Correct! The number was " + secretNum + "!**\n" +
                       "You got it in **" + numAttempts + " attempts**. " + rating + "\n\n" +
                       "Play again? Say **number game** anytime!";
            }

            // Narrow the range hint
            if (guess < secretNum) numMin = Math.max(numMin, guess + 1);
            else numMax = Math.min(numMax, guess - 1);

            String hint = guess < secretNum ? "\u2B06\uFE0F Too low!" : "\u2B07\uFE0F Too high!";
            return hint + " Range: **" + numMin + " \u2014 " + numMax + "**\n" +
                   "Attempts: " + numAttempts + " | Keep guessing!";

        } catch (NumberFormatException e) {
            if (input.toLowerCase().contains("quit") || input.toLowerCase().contains("stop")) {
                state = GameState.NONE;
                return "Game ended! The number was **" + secretNum + "**. Play again anytime!";
            }
            return "\uD83D\uDCA1 Please type a **number** between " + numMin + " and " + numMax + "!";
        }
    }

    private static String handleWord(String input) {
        String guess = input.trim().toLowerCase();
        wordAttempts++;

        if (guess.equals("quit") || guess.equals("stop")) {
            state = GameState.NONE;
            return "Game ended! The word was **" + secretWord + "**. Play again anytime!";
        }

        if (guess.equals(secretWord)) {
            state = GameState.NONE;
            String rating = wordAttempts == 1 ? "\uD83C\uDF1F First try!" :
                            wordAttempts <= 3 ? "\uD83C\uDFC6 Excellent!" : "\uD83D\uDCAA Well done!";
            return "\u2705 **Correct! The word was '" + secretWord + "'!**\n" +
                   "Attempts: **" + wordAttempts + "**. " + rating + "\n\n" +
                   "Play again? Say **word game** anytime!";
        }

        // Show matching letters
        StringBuilder revealed = new StringBuilder();
        for (char c : secretWord.toCharArray()) {
            revealed.append(guess.indexOf(c) >= 0 ? c : '_');
            revealed.append(' ');
        }

        if (wordAttempts >= 6) {
            state = GameState.NONE;
            return "\u274C Out of attempts! The word was **" + secretWord + "**.\n" +
                   "Better luck next time! Say **word game** to play again.";
        }

        return "\u274C Not quite! Hint: **" + wordHint + "**\n" +
               "Matching letters: **" + revealed.toString().trim() + "**\n" +
               "Attempts: " + wordAttempts + "/6 | Keep trying!";
    }

    private static String handleRiddle(String input) {
        String answer = input.trim().toLowerCase();

        if (answer.contains("quit") || answer.contains("give up")) {
            state = GameState.NONE;
            return "The answer was: **" + RIDDLES[riddleIndex][1] + "**!\nTry another? Say **riddle** anytime!";
        }

        if (answer.contains(RIDDLES[riddleIndex][1])) {
            state = GameState.NONE;
            return "\uD83E\uDD73 **Brilliant! That's correct!**\n" +
                   "The answer is indeed: **" + RIDDLES[riddleIndex][1] + "**\n\n" +
                   "Want another riddle? Just say **riddle**!";
        }

        return "\uD83E\uDD14 Not quite... think deeper!\n_Hint: It's something very common._\n" +
               "(Say **give up** to reveal the answer)";
    }
}
