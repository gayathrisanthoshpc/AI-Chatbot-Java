package chatbot;

import java.util.Random;
import java.time.LocalTime;

public class SmartChatBot {

    static String lastMessage = "";
    static String userName = "";

    public static String getReply(String input) {
        input = input.toLowerCase();

        // Greetings
        if (input.contains("hi") || input.contains("hello")) {
            String[] replies = {
                    "Hey! 😊",
                    "Hello! How can I help you?",
                    "Hi there! Nice to meet you."
            };
            return replies[new Random().nextInt(replies.length)];
        }

        // Name memory
        if (input.startsWith("my name is")) {
            userName = input.replace("my name is", "").trim();
            return "Nice to meet you, " + userName + " 😊";
        }

        if (input.contains("who am i")) {
            return userName.isEmpty()
                    ? "I don't know your name yet."
                    : "You are " + userName;
        }

        // Time
        if (input.contains("time")) {
            return "Current time is " + LocalTime.now();
        }

        // Remember last message
        if (input.contains("remember")) {
            return lastMessage.isEmpty()
                    ? "I don't remember anything yet."
                    : "You previously said: " + lastMessage;
        }

        // Help
        if (input.contains("help")) {
            return "You can ask me: hi, my name is, who am I, time, remember, bye";
        }

        // Exit
        if (input.contains("bye")) {
            return "Goodbye! 👋 Hope to chat again soon.";
        }

        lastMessage = input;
        return "Interesting... tell me more 🙂";
    }
}
