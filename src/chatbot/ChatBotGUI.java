package chatbot;

import javax.swing.*;
import java.awt.*;

public class ChatBotGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("AI ChatBot");
        JTextArea chatArea = new JTextArea();
        JTextField inputField = new JTextField();

        JButton sendButton = new JButton("Send");
        JButton clearButton = new JButton("Clear");

        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);

        // Send button action
        sendButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                chatArea.append("You: " + text + "\n");
                chatArea.append("Bot: " + SmartChatBot.getReply(text) + "\n\n");
                inputField.setText("");
            }
        });

        // Clear button action
        clearButton.addActionListener(e -> chatArea.setText(""));

        frame.setLayout(new BorderLayout());
        frame.add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(sendButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(450, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
