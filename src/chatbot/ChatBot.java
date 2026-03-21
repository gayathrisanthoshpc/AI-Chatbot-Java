package chatbot;

import chatbot.ui.ChatBotGUI;
import chatbot.ui.ORYNIcon;

import javax.swing.*;

/**
 * ORYN — Light of Knowledge
 * Entry point — sets window icon and launches GUI.
 */
public class ChatBot {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            ChatBotGUI gui = new ChatBotGUI();
            // Set proper ORYN logo as window icon
            gui.setIconImages(ORYNIcon.generateIconList());
            gui.setVisible(true);
        });
    }
}
