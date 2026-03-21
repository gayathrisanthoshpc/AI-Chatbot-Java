package chatbot.util;

import chatbot.model.Message;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Saves and loads chat history as plain .txt files.
 */
public class ChatHistory {

    private static final String HISTORY_DIR = System.getProperty("user.home") + "/ORYN_Chats";

    public static String save(List<Message> messages) throws IOException {
        Files.createDirectories(Paths.get(HISTORY_DIR));
        String filename = "oryn_chat_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt";
        Path path = Paths.get(HISTORY_DIR, filename);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("═══════════════════════════════════════════");
            writer.newLine();
            writer.write("  ORYN — Light of Knowledge | Chat History");
            writer.newLine();
            writer.write("  Saved: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm")));
            writer.newLine();
            writer.write("═══════════════════════════════════════════");
            writer.newLine();
            writer.newLine();

            for (Message msg : messages) {
                writer.write(msg.toString());
                writer.newLine();
            }
        }
        return path.toString();
    }
}
