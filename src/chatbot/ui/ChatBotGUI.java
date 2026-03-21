package chatbot.ui;

import chatbot.model.Message;
import chatbot.service.ChatService;
import chatbot.service.SmartChatBot;
import chatbot.util.AppConfig;
import chatbot.util.ChatHistory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ORYN main window — dark-theme chat UI with bubbles, typing indicator,
 * timestamps, save history, and smooth scrolling.
 */
public class ChatBotGUI extends JFrame {

    // ── Services ──────────────────────────────────────────────────────────────
    private final ChatService    bot      = new SmartChatBot();
    private final List<Message>  history  = new ArrayList<>();

    // ── UI Components ─────────────────────────────────────────────────────────
    private JPanel         chatPanel;
    private JScrollPane    scrollPane;
    private JTextField     inputField;
    private JButton        sendButton;
    private TypingIndicator typingIndicator;
    private JLabel         statusLabel;

    // ─────────────────────────────────────────────────────────────────────────

    public ChatBotGUI() {
        initWindow();
        initComponents();
        showWelcome();
    }

    // ── Window Setup ──────────────────────────────────────────────────────────

    private void initWindow() {
        setTitle("ORYN — Light of Knowledge");
        setSize(AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(420, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppConfig.BG_DARK);
        setLayout(new BorderLayout(0, 0));
    }

    // ── Component Init ────────────────────────────────────────────────────────

    private void initComponents() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildChat(),    BorderLayout.CENTER);
        add(buildBottom(),  BorderLayout.SOUTH);
    }

    /** Top header bar with ORYN branding and action buttons */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppConfig.BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppConfig.BORDER_SUBTLE),
            new EmptyBorder(12, 16, 12, 16)
        ));

        // Left: avatar + name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel avatar = new JLabel("✦") {{
            setFont(new Font("Segoe UI", Font.BOLD, 22));
            setForeground(AppConfig.ACCENT);
        }};

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(AppConfig.BOT_NAME);
        nameLabel.setFont(AppConfig.FONT_HEADER);
        nameLabel.setForeground(AppConfig.TEXT_PRIMARY);

        JLabel tagLabel = new JLabel(AppConfig.BOT_TAGLINE);
        tagLabel.setFont(AppConfig.FONT_TIMESTAMP);
        tagLabel.setForeground(AppConfig.ACCENT_GLOW);

        namePanel.add(nameLabel);
        namePanel.add(tagLabel);
        left.add(avatar);
        left.add(namePanel);

        // Right: action buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);

        JButton saveBtn  = headerButton("💾", "Save Chat");
        JButton clearBtn = headerButton("🗑", "Clear Chat");
        JButton resetBtn = headerButton("↺",  "Reset Memory");

        saveBtn.addActionListener(e -> saveChat());
        clearBtn.addActionListener(e -> clearChat());
        resetBtn.addActionListener(e -> resetBot());

        right.add(saveBtn);
        right.add(clearBtn);
        right.add(resetBtn);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    /** Scrollable chat message area */
    private JScrollPane buildChat() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(AppConfig.BG_DARK);
        chatPanel.setBorder(new EmptyBorder(12, 8, 12, 8));

        // Typing indicator (hidden by default)
        typingIndicator = new TypingIndicator();
        typingIndicator.setVisible(false);
        typingIndicator.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBackground(AppConfig.BG_DARK);
        scrollPane.getViewport().setBackground(AppConfig.BG_DARK);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Custom scrollbar style
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = AppConfig.BORDER_SUBTLE;
                this.trackColor = AppConfig.BG_DARK;
            }
            @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
            @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
            private JButton zeroButton() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b;
            }
        });

        return scrollPane;
    }

    /** Bottom input panel with text field and send button */
    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout(0, 0));
        bottom.setBackground(AppConfig.BG_PANEL);
        bottom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppConfig.BORDER_SUBTLE),
            new EmptyBorder(10, 12, 10, 12)
        ));

        // Status bar
        statusLabel = new JLabel("  ORYN is ready ✦");
        statusLabel.setFont(AppConfig.FONT_TIMESTAMP);
        statusLabel.setForeground(AppConfig.TEXT_SECONDARY);

        // Input row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(new EmptyBorder(6, 0, 0, 0));

        inputField = new JTextField();
        inputField.setFont(AppConfig.FONT_INPUT);
        inputField.setForeground(AppConfig.TEXT_PRIMARY);
        inputField.setBackground(AppConfig.BG_INPUT);
        inputField.setCaretColor(AppConfig.ACCENT_GLOW);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.BORDER_SUBTLE, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        sendButton = new JButton("Send ➤");
        sendButton.setFont(AppConfig.FONT_BUTTON);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(AppConfig.BG_USER_BUBBLE);
        sendButton.setBorder(new EmptyBorder(9, 18, 9, 18));
        sendButton.setFocusPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setOpaque(true);

        // Hover effect
        sendButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { sendButton.setBackground(AppConfig.ACCENT); }
            public void mouseExited (MouseEvent e) { sendButton.setBackground(AppConfig.BG_USER_BUBBLE); }
        });

        // Actions
        sendButton.addActionListener(e -> handleSend());
        inputField.addActionListener(e -> handleSend()); // Enter key

        inputRow.add(inputField,  BorderLayout.CENTER);
        inputRow.add(sendButton,  BorderLayout.EAST);

        bottom.add(statusLabel, BorderLayout.NORTH);
        bottom.add(inputRow,    BorderLayout.CENTER);
        return bottom;
    }

    // ── Chat Logic ────────────────────────────────────────────────────────────

    private void showWelcome() {
        String welcome = "Hello! I'm " + AppConfig.BOT_NAME + " 🌟\n" +
                         "I'm your intelligent assistant — light of knowledge.\n" +
                         "Type 'help' to see what I can do, or just say hi!";
        appendMessage(new Message(welcome, Message.Sender.BOT));
    }

    private void handleSend() {
        String text = inputField.getText().trim();
        if (text.isBlank()) return;

        inputField.setText("");
        inputField.setEnabled(false);
        sendButton.setEnabled(false);
        setStatus("ORYN is thinking...");

        // Add user message instantly
        Message userMsg = new Message(text, Message.Sender.USER);
        history.add(userMsg);
        appendMessage(userMsg);

        // Show typing indicator
        showTyping(true);

        // Bot replies after a short delay (non-blocking)
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() throws Exception {
                Thread.sleep(AppConfig.TYPING_DELAY_MS);
                return bot.getReply(text);
            }
            @Override protected void done() {
                try {
                    String reply = get();
                    showTyping(false);
                    Message botMsg = new Message(reply, Message.Sender.BOT);
                    history.add(botMsg);
                    appendMessage(botMsg);

                    // Check exit
                    if (text.equalsIgnoreCase("bye") || text.equalsIgnoreCase("exit")) {
                        Timer t = new Timer(1200, ev -> System.exit(0));
                        t.setRepeats(false); t.start();
                    }
                } catch (Exception ex) {
                    showTyping(false);
                    appendMessage(new Message("⚠ Something went wrong. Try again.", Message.Sender.BOT));
                } finally {
                    inputField.setEnabled(true);
                    sendButton.setEnabled(true);
                    inputField.requestFocus();
                    setStatus("ORYN is ready ✦");
                }
            }
        };
        worker.execute();
    }

    private void appendMessage(Message msg) {
        // Remove typing indicator if present, then re-add at bottom
        chatPanel.remove(typingIndicator);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        MessageBubble bubble = new MessageBubble(msg);
        bubble.setPreferredSize(new Dimension(AppConfig.WINDOW_WIDTH - 40, 70));

        row.add(bubble);
        chatPanel.add(row);
        chatPanel.add(Box.createVerticalStrut(4));
        chatPanel.add(typingIndicator);

        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    private void showTyping(boolean show) {
        if (show) typingIndicator.start();
        else      typingIndicator.stop();
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private void setStatus(String text) {
        statusLabel.setText("  " + text);
    }

    // ── Header Actions ────────────────────────────────────────────────────────

    private void saveChat() {
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No messages to save yet.", "Save Chat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            String path = ChatHistory.save(history);
            JOptionPane.showMessageDialog(this,
                "Chat saved to:\n" + path, "Saved ✓", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Could not save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearChat() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Clear all messages?", "Clear Chat", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            history.clear();
            chatPanel.removeAll();
            chatPanel.add(typingIndicator);
            chatPanel.revalidate();
            chatPanel.repaint();
            showWelcome();
        }
    }

    private void resetBot() {
        bot.reset();
        appendMessage(new Message("Memory cleared! I've forgotten everything — fresh start 🌟", Message.Sender.BOT));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton headerButton(String icon, String tooltip) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btn.setToolTipText(tooltip);
        btn.setForeground(AppConfig.TEXT_SECONDARY);
        btn.setBackground(AppConfig.BG_PANEL);
        btn.setBorder(new EmptyBorder(5, 8, 5, 8));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(AppConfig.ACCENT_GLOW); }
            public void mouseExited (MouseEvent e) { btn.setForeground(AppConfig.TEXT_SECONDARY); }
        });
        return btn;
    }
}
