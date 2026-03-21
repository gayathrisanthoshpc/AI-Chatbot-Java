package chatbot;

import chatbot.ui.ChatBotGUI;

import javax.swing.*;

/**
 * ORYN — Light of Knowledge
 * Entry point. Launches the GUI on the Swing Event Dispatch Thread.
 */
public class ChatBot {

    public static void main(String[] args) {
        // Set system look for better native rendering, then override with our theme
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            ChatBotGUI gui = new ChatBotGUI();
            gui.setVisible(true);
        });
    }
}
