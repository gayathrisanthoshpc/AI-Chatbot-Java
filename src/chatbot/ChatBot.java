package chatbot;

import java.util.Scanner;

public class ChatBot {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("🤖 ChatBot Started! Type 'bye' to exit.");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().toLowerCase();

            if (userInput.contains("hi") || userInput.contains("hello")) {
                System.out.println("Bot: Hello! Nice to meet you 😊");
            }
            else if (userInput.contains("how are you")) {
                System.out.println("Bot: I'm doing great!");
            }
            else if (userInput.contains("your name")) {
                System.out.println("Bot: My name is JavaBot 🤖");
            }
            else if (userInput.contains("bye")) {
                System.out.println("Bot: Goodbye!");
                break;
            }
            else {
                System.out.println("Bot: Sorry, I didn't understand.");
            }
        }
        scanner.close();
    }
}
