package chatbot;

import java.util.*;

public class SmartChatBot {

    static String lastMessage = "";

    static String getReply(String input) {
        input = input.toLowerCase();

        if (input.contains("hi")) {
            String[] replies = {
                    "Hi there!",
                    "Hello 👋",
                    "Hey! How can I help?"
            };
            return replies[new Random().nextInt(replies.length)];
        }

        if (input.contains("remember")) {
            return "You previously said: " + lastMessage;
        }

        lastMessage = input;
        return "Interesting... tell me more.";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("🤖 Smart ChatBot Started");

        while (true) {
            System.out.print("You: ");
            String input = sc.nextLine();
            System.out.println("Bot: " + getReply(input));

            if (input.equalsIgnoreCase("bye")) break;
        }
        sc.close();
    }
}
