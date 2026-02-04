package chatbot;

import java.util.Scanner;

public class ChatBot {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("🤖 Smart Console ChatBot (type bye to exit)");

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();

            System.out.println("Bot: " + SmartChatBot.getReply(input));

            if (input.equalsIgnoreCase("bye")) {
                break;
            }
        }
        scanner.close();
    }
}
