package chatbot;

import javax.swing.*;
import java.awt.*;

public class ChatBotGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("AI ChatBot");
        JTextArea chatArea = new JTextArea();
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);

        sendButton.addActionListener(e -> {
            String text = inputField.getText();
            chatArea.append("You: " + text + "\n");
            chatArea.append("Bot: Hello! You said \"" + text + "\"\n\n");
            inputField.setText("");
        });

        frame.setLayout(new BorderLayout());
        frame.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        frame.add(bottom, BorderLayout.SOUTH);

        frame.setSize(400, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
