package chatbot.service;

public interface ChatService {
    /**
     * Process user input and return ORYN's reply.
     * Implementations can be rule-based, API-backed, or hybrid.
     */
    String getReply(String userInput);

    /** Reset conversation state (memory, context). */
    void reset();
}
