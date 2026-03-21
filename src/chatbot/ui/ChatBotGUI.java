package chatbot.ui;

import chatbot.model.Message;
import chatbot.service.ChatService;
import chatbot.service.SmartChatBot;
import chatbot.util.*;
import chatbot.intelligence.*;
import chatbot.ui.ORYNIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ORYN Rose Noir Glassmorphism UI v4.1
 * Complete aesthetic redesign — frosted glass, rose gold, premium feel.
 */
public class ChatBotGUI extends JFrame {

    private final ChatService   bot     = new SmartChatBot();
    private final List<Message> history = new ArrayList<>();
    private UserProfile profile;

    private JPanel             chatPanel;
    private JScrollPane        scrollPane;
    private JTextField         inputField;
    private RoseButton         sendButton;
    private TypingIndicator    typingIndicator;
    private JLabel             statusLabel;
    private JPanel             searchBar;
    private JTextField         searchField;
    private JPanel             headerPanel;
    private JLabel             themeToggleBtn;
    private ParticleBackground particleBg;
    private JPanel             suggestionBar;

    public ChatBotGUI() {
        profile = UserProfile.load();
        AppConfig.setDark(profile.darkMode);
        AppConfig.setFontSize(profile.fontSize > 0 ? profile.fontSize : 14);
        SoundManager.setEnabled(profile.soundOn);
        if (!profile.userName.isEmpty()) ((SmartChatBot)bot).setUserName(profile.userName);
        initWindow();
        initComponents();
        showWelcome();
    }

    private void initWindow() {
        setTitle("ORYN");
        setSize(AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(480, 560));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppConfig.BG_DARK());
        setLayout(new BorderLayout());
        // Subtle window border
        getRootPane().setBorder(BorderFactory.createLineBorder(
            new Color(AppConfig.ACCENT.getRed(), AppConfig.ACCENT.getGreen(), AppConfig.ACCENT.getBlue(), 40), 1));
    }

    private void initComponents() {
        headerPanel = buildHeader();
        add(headerPanel,   BorderLayout.NORTH);
        add(buildChat(),   BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
    }

    // ── Rose Noir Header ──────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (AppConfig.isDark()) {
                    g2.setColor(new Color(12, 8, 16));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    // Rose glow left
                    RadialGradientPaint rg = new RadialGradientPaint(new Point(70, getHeight()/2), 90,
                        new float[]{0f,1f}, new Color[]{new Color(212,100,150,55), new Color(212,100,150,0)});
                    g2.setPaint(rg); g2.fillOval(-20,-30,180,getHeight()+60);
                    // Gold glow right
                    RadialGradientPaint gg = new RadialGradientPaint(new Point(getWidth()-50, getHeight()/2), 75,
                        new float[]{0f,1f}, new Color[]{new Color(230,170,100,45), new Color(230,170,100,0)});
                    g2.setPaint(gg); g2.fillOval(getWidth()-125,-20,150,getHeight()+40);
                } else {
                    // Light mode: warm rose-cream gradient header
                    GradientPaint lightBg = new GradientPaint(
                        0,0, new Color(255,228,230),
                        getWidth(),0, new Color(255,240,220));
                    g2.setPaint(lightBg); g2.fillRect(0,0,getWidth(),getHeight());
                    // Soft rose left glow
                    RadialGradientPaint rg = new RadialGradientPaint(new Point(60, getHeight()/2), 80,
                        new float[]{0f,1f}, new Color[]{new Color(200,80,120,60), new Color(200,80,120,0)});
                    g2.setPaint(rg); g2.fillOval(-15,-20,160,getHeight()+40);
                    // Gold right glow
                    RadialGradientPaint gg = new RadialGradientPaint(new Point(getWidth()-40, getHeight()/2), 70,
                        new float[]{0f,1f}, new Color[]{new Color(220,150,80,50), new Color(220,150,80,0)});
                    g2.setPaint(gg); g2.fillOval(getWidth()-110,-15,140,getHeight()+30);
                }

                // Bottom border — rose gold gradient line
                GradientPaint borderLine = new GradientPaint(
                    0, 0, new Color(160,60,100,180),
                    getWidth()/2f, 0, new Color(230,150,180,220),
                    true);
                g2.setPaint(borderLine);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);

                g2.dispose();
            }
        };
        header.setOpaque(false);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(13, 18, 13, 18));

        // Left — avatar + name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(buildRoseOrb());

        JPanel nameCol = new JPanel();
        nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS));
        nameCol.setOpaque(false);

        JLabel nameLabel = new JLabel(AppConfig.BOT_NAME) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                // Rose gold gradient text
                GradientPaint gp = new GradientPaint(0,0,AppConfig.ACCENT_GLOW,getWidth(),0,AppConfig.ACCENT_GOLD_SOFT);
                g2.setPaint(gp);
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        nameLabel.setFont(AppConfig.FONT_HEADER);
        nameLabel.setForeground(AppConfig.ACCENT_GLOW);

        JLabel tagLabel = new JLabel(AppConfig.BOT_TAGLINE + "  \u00b7  v" + AppConfig.VERSION);
        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        tagLabel.setForeground(new Color(AppConfig.ACCENT_GOLD.getRed(), AppConfig.ACCENT_GOLD.getGreen(),
                                         AppConfig.ACCENT_GOLD.getBlue(), 170));
        nameCol.add(nameLabel);
        nameCol.add(tagLabel);
        left.add(nameCol);

        // Right — icon buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        right.setOpaque(false);

        themeToggleBtn = iconBtn(AppConfig.isDark() ? "\u2600" : "\uD83C\uDF19", "Toggle Theme");
        JLabel srchBtn = iconBtn("\uD83D\uDD0D", "Search Chat");
        JLabel saveBtn = iconBtn("\uD83D\uDCBE", "Save Chat");
        JLabel pdfBtn  = iconBtn("\uD83D\uDCC4", "Export PDF");
        JLabel clrBtn  = iconBtn("\uD83D\uDDD1", "Clear Chat");
        JLabel setBtn  = iconBtn("\u2699", "Settings");

        themeToggleBtn.addMouseListener(click(e -> toggleTheme()));
        srchBtn.addMouseListener(click(e -> toggleSearch()));
        saveBtn.addMouseListener(click(e -> saveChat()));
        pdfBtn.addMouseListener(click(e  -> PdfExporter.export(history, this)));
        clrBtn.addMouseListener(click(e  -> clearChat()));
        setBtn.addMouseListener(click(e  -> openSettings()));

        for (JLabel l : new JLabel[]{themeToggleBtn, srchBtn, saveBtn, pdfBtn, clrBtn, setBtn})
            right.add(l);

        content.add(left,  BorderLayout.WEST);
        content.add(right, BorderLayout.EAST);

        searchBar = buildSearchBar();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        content.setAlignmentX(LEFT_ALIGNMENT);
        searchBar.setAlignmentX(LEFT_ALIGNMENT);
        header.add(content);
        header.add(searchBar);
        return header;
    }

    private JLabel buildRoseOrb() {
        // Use the proper ORYN logo
        java.awt.image.BufferedImage logo = ORYNIcon.generate(44);
        return new JLabel() {
            float p = 0f;
            { new Timer(60, e -> { p += 0.05f; repaint(); }).start();
              setPreferredSize(new Dimension(44, 44)); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle pulse glow behind logo
                float glow = (float)(0.5 + 0.5 * Math.sin(p));
                g2.setColor(new Color(212, 100, 150, (int)(15 + 20*glow)));
                g2.fillOval(-3, -3, 50, 50);
                // Draw the logo
                g2.drawImage(logo, 0, 0, 44, 44, null);
                g2.dispose();
            }
        };
    }

    private JLabel iconBtn(String icon, String tooltip) {
        JLabel lbl = new JLabel(icon) {
            boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov=true; repaint(); }
                public void mouseExited(MouseEvent e)  { hov=false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                if (hov) {
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Rose glow pill on hover
                    g2.setColor(new Color(212,100,150,35));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                    g2.setColor(new Color(212,100,150,60));
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        lbl.setToolTipText(tooltip);
        lbl.setForeground(new Color(AppConfig.ACCENT.getRed(), AppConfig.ACCENT.getGreen(),
                                     AppConfig.ACCENT.getBlue(), 180));
        lbl.setBorder(new EmptyBorder(5, 8, 5, 8));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { lbl.setForeground(AppConfig.ACCENT_GLOW); }
            public void mouseExited(MouseEvent e)  {
                lbl.setForeground(new Color(AppConfig.ACCENT.getRed(), AppConfig.ACCENT.getGreen(),
                                            AppConfig.ACCENT.getBlue(), 180));
            }
        });
        return lbl;
    }

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setColor(AppConfig.BG_PANEL()); g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(6, 18, 8, 18));
        bar.setVisible(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        searchField = new JTextField();
        searchField.setFont(AppConfig.FONT_INPUT);
        searchField.setForeground(AppConfig.TEXT_PRIMARY());
        searchField.setBackground(AppConfig.BG_INPUT());
        searchField.setCaretColor(AppConfig.ACCENT_GLOW);
        searchField.setOpaque(true);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.BORDER_SUBTLE(), 1, true),
            new EmptyBorder(4, 10, 4, 10)));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { performSearch(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { performSearch(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        JLabel close = iconBtn("\u2715", "Close");
        close.addMouseListener(click(e -> toggleSearch()));
        bar.add(searchField, BorderLayout.CENTER);
        bar.add(close, BorderLayout.EAST);
        return bar;
    }

    // ── Chat area ─────────────────────────────────────────────────────────────

    private JScrollPane buildChat() {
        particleBg = new ParticleBackground();
        particleBg.setLayout(new BorderLayout());

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setOpaque(false);
        chatPanel.setBorder(new EmptyBorder(16, 14, 16, 14));

        typingIndicator = new TypingIndicator();
        typingIndicator.setVisible(false);
        typingIndicator.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(chatPanel, BorderLayout.NORTH);
        particleBg.add(wrapper, BorderLayout.CENTER);

        scrollPane = new JScrollPane(particleBg);
        scrollPane.setBackground(AppConfig.BG_DARK());
        scrollPane.getViewport().setBackground(AppConfig.BG_DARK());
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(AppConfig.ACCENT.getRed(), AppConfig.ACCENT.getGreen(),
                                       AppConfig.ACCENT.getBlue(), 80);
                trackColor = AppConfig.BG_DARK();
            }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            private JButton zeroBtn() { JButton b=new JButton(); b.setPreferredSize(new Dimension(5,0)); return b; }
        });
        particleBg.startAnimation();
        return scrollPane;
    }

    // ── Bottom panel ──────────────────────────────────────────────────────────

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setColor(AppConfig.BG_PANEL()); g2.fillRect(0,0,getWidth(),getHeight());
                // Top separator line — rose gold
                GradientPaint sep = new GradientPaint(0,0,
                    new Color(AppConfig.ACCENT.getRed(),AppConfig.ACCENT.getGreen(),AppConfig.ACCENT.getBlue(),80),
                    getWidth()/2,0,
                    new Color(AppConfig.ACCENT.getRed(),AppConfig.ACCENT.getGreen(),AppConfig.ACCENT.getBlue(),30),
                    true);
                g2.setPaint(sep); g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0,0,getWidth(),0);
                g2.dispose();
            }
        };
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 16, 16, 16));

        statusLabel = new JLabel("  \u2736 ORYN Rose Noir");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setForeground(new Color(AppConfig.ACCENT.getRed(), AppConfig.ACCENT.getGreen(),
                                            AppConfig.ACCENT.getBlue(), 140));

        // Suggestion chips - horizontal scroll to prevent overflow
        suggestionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        suggestionBar.setOpaque(false);
        suggestionBar.setVisible(false);
        suggestionBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(new EmptyBorder(8, 0, 0, 0));

        GlassInputPanel glassInput = new GlassInputPanel();
        inputField = glassInput.getField();
        inputField.addActionListener(e -> handleSend());

        sendButton = new RoseButton();
        sendButton.addActionListener(e -> handleSend());

        VoiceInput voiceBtn = new VoiceInput(text -> {
            SwingUtilities.invokeLater(() -> {
                inputField.setText(text);
                handleSend();
            });
        });

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        rightBtns.setOpaque(false);
        rightBtns.add(voiceBtn);
        rightBtns.add(sendButton);

        inputRow.add(glassInput,  BorderLayout.CENTER);
        inputRow.add(rightBtns,   BorderLayout.EAST);

        bottom.add(statusLabel,    BorderLayout.NORTH);
        bottom.add(suggestionBar,  BorderLayout.CENTER);
        bottom.add(inputRow,       BorderLayout.SOUTH);
        return bottom;
    }

    // ── Chat logic ────────────────────────────────────────────────────────────

    private void showWelcome() {
        SmartChatBot sb = (SmartChatBot) bot;
        LongMemory mem  = sb.getMemory();
        BondSystem bond = sb.getBond();
        String msg = mem.isFirstTodaySession()
            ? DailyDigest.generate(profile.userName, mem, bond)
            : bond.getGreetingFlavour(profile.userName) + "\nType **help** to see what I can do!";
        addMsg(new Message(msg, Message.Sender.BOT), true);
        SwingUtilities.invokeLater(() -> showSuggestions(
            new String[]{"What can you do?", "Tell me something cool", "Give me a quote"}));
    }

    private void handleSend() {
        String text = inputField.getText().trim();
        if (text.isBlank()) return;
        inputField.setText("");
        inputField.setEnabled(false);
        sendButton.setEnabled(false);
        setStatus("\u2736 ORYN is thinking...");
        SoundManager.playSend();
        Message um = new Message(text, Message.Sender.USER);
        history.add(um); addMsg(um, true); showTyping(true);
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception {
                Thread.sleep(AppConfig.TYPING_DELAY_MS);
                return bot.getReply(text);
            }
            @Override protected void done() {
                try {
                    String r = get(); showTyping(false);
                    Message bm = new Message(r, Message.Sender.BOT);
                    history.add(bm); addMsg(bm, true); SoundManager.playChime();
                    SwingUtilities.invokeLater(() -> showSmartSuggestions(r, text));
                    if (text.equalsIgnoreCase("bye")||text.equalsIgnoreCase("exit")) {
                        new Timer(1200, ev -> System.exit(0)) {{ setRepeats(false); start(); }};
                    }
                } catch (Exception ex) {
                    showTyping(false);
                    addMsg(new Message("\u26a0 Something went wrong.", Message.Sender.BOT), true);
                } finally {
                    inputField.setEnabled(true); sendButton.setEnabled(true);
                    inputField.requestFocus(); setStatus("  \u2736 ORYN Rose Noir");
                }
            }
        }.execute();
    }

    private void addMsg(Message msg, boolean animate) {
        chatPanel.remove(typingIndicator);
        boolean isUser = msg.getSender() == Message.Sender.USER;

        JPanel rowWrap = new JPanel();
        rowWrap.setLayout(new BoxLayout(rowWrap, BoxLayout.Y_AXIS));
        rowWrap.setOpaque(false);
        rowWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        ORYNBubble bubble = new ORYNBubble(msg);

        // Right-click copy
        JPopupMenu pop = new JPopupMenu();
        JMenuItem ci = new JMenuItem("Copy message");
        ci.addActionListener(e -> {
            java.awt.datatransfer.StringSelection sel =
                new java.awt.datatransfer.StringSelection(msg.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
        });
        pop.add(ci);
        bubble.setComponentPopupMenu(pop);

        // Bubble row
        JPanel bubbleRow = new JPanel(new BorderLayout());
        bubbleRow.setOpaque(false);
        if (isUser) { bubbleRow.add(Box.createHorizontalGlue(), BorderLayout.WEST); bubbleRow.add(bubble, BorderLayout.EAST); }
        else        { bubbleRow.add(bubble, BorderLayout.WEST); bubbleRow.add(Box.createHorizontalGlue(), BorderLayout.EAST); }

        // Reaction chips
        JPanel reactions = buildReactionRow(isUser);
        JPanel reactAlign = new JPanel(new BorderLayout());
        reactAlign.setOpaque(false);
        if (isUser) { reactAlign.add(Box.createHorizontalGlue(), BorderLayout.WEST); reactAlign.add(reactions, BorderLayout.EAST); }
        else        { reactAlign.add(reactions, BorderLayout.WEST); reactAlign.add(Box.createHorizontalGlue(), BorderLayout.EAST); }

        MouseAdapter hov = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { reactions.setVisible(true); }
            public void mouseExited(MouseEvent e)  { if (!rowWrap.contains(e.getPoint())) reactions.setVisible(false); }
        };
        bubble.addMouseListener(hov);
        rowWrap.addMouseListener(hov);

        rowWrap.add(bubbleRow);
        rowWrap.add(reactAlign);

        // Fade in
        final float[] alpha = {0f};
        chatPanel.add(rowWrap);
        chatPanel.add(Box.createVerticalStrut(10));
        chatPanel.add(typingIndicator);
        chatPanel.revalidate(); chatPanel.repaint(); scrollToBottom();

        if (animate) {
            // Slide + fade animation
            boolean slideRight = msg.getSender() == Message.Sender.USER;
            rowWrap.putClientProperty("slideX", slideRight ? 30 : -30);
            rowWrap.putClientProperty("fadeAlpha", 0f);
            Timer ft = new Timer(16, null);
            ft.addActionListener(e -> {
                float a = (Float) rowWrap.getClientProperty("fadeAlpha");
                int  sx = (Integer) rowWrap.getClientProperty("slideX");
                a  = Math.min(1f, a + 0.1f);
                sx = (int)(sx * 0.75f); // ease out
                rowWrap.putClientProperty("fadeAlpha", a);
                rowWrap.putClientProperty("slideX", sx);
                rowWrap.repaint();
                if (a >= 1f) ft.stop();
            });
            ft.start();
        }
    }

    private JPanel buildReactionRow(boolean isUser) {
        JPanel p = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 4, 0));
        p.setOpaque(false); p.setVisible(false);
        for (String em : new String[]{"\uD83D\uDC4D", "\u2764", "\uD83D\uDE04"}) {
            JLabel l = new JLabel(em);
            l.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            l.setBorder(new EmptyBorder(2, 4, 2, 4));
            l.addMouseListener(new MouseAdapter() {
                boolean on = false;
                public void mouseClicked(MouseEvent e) {
                    on = !on;
                    l.setFont(new Font("Segoe UI Emoji", Font.PLAIN, on ? 16 : 13));
                    p.revalidate();
                }
            });
            p.add(l);
        }
        return p;
    }

    void showSuggestions(String[] chips) {
        suggestionBar.removeAll();
        for (String s : chips) {
            JLabel chip = new JLabel(s) {
                boolean hov = false;
                { addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov=true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hov=false; repaint(); }
                }); }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = hov ? new Color(212,100,150,50) : new Color(212,100,150,20);
                    g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                    g2.setColor(new Color(212,100,150, hov ? 120 : 70));
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                    g2.dispose(); super.paintComponent(g);
                }
            };
            chip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            chip.setForeground(AppConfig.ACCENT_BRIGHT);
            chip.setBorder(new EmptyBorder(4, 12, 4, 12));
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    inputField.setText(s); handleSend();
                    suggestionBar.setVisible(false);
                }
                public void mouseEntered(MouseEvent e) { chip.setForeground(AppConfig.ACCENT_GLOW); }
                public void mouseExited(MouseEvent e)  { chip.setForeground(AppConfig.ACCENT_BRIGHT); }
            });
            suggestionBar.add(chip);
        }
        suggestionBar.setVisible(true);
        suggestionBar.revalidate(); suggestionBar.repaint();
    }

    private void showSmartSuggestions(String reply, String input) {
        String lo = input.toLowerCase();
        String[] chips;
        if (lo.contains("joke"))          chips = new String[]{"Another joke", "Give me a quote", "Trivia!"};
        else if (lo.contains("wiki")||lo.contains("tell me about"))
                                          chips = new String[]{"Tell me more", "Related trivia", "Another topic"};
        else if (lo.contains("trivia"))   chips = new String[]{"Another trivia", "Wikipedia search", "Give me a fact"};
        else if (lo.contains("weather"))  chips = new String[]{"Another city?", "Tell me a fact", "Inspire me"};
        else if (lo.contains("debate"))   chips = new String[]{"Debate another topic", "Give me a quote", "Trivia time"};
        else if (lo.contains("score")||lo.contains("bond"))
                                          chips = new String[]{"How do I level up?", "My interests", "Trivia challenge"};
        else if (lo.contains("help"))     chips = new String[]{"Tell me about AI", "Debate free will", "Knock knock"};
        else                              chips = new String[]{"Tell me more", "Give me a quote", "Surprise me"};
        showSuggestions(chips);
    }

    private void showTyping(boolean show) {
        if (show) typingIndicator.start(); else typingIndicator.stop();
        chatPanel.revalidate(); chatPanel.repaint(); scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar b = scrollPane.getVerticalScrollBar(); b.setValue(b.getMaximum());
        });
    }

    private void setStatus(String t) { statusLabel.setText("  " + t); }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void toggleTheme() {
        AppConfig.setDark(!AppConfig.isDark());
        profile.darkMode = AppConfig.isDark(); profile.save();
        themeToggleBtn.setText(AppConfig.isDark() ? "\u2600" : "\uD83C\uDF19");
        repaintAll();
    }

    private void repaintAll() {
        getContentPane().setBackground(AppConfig.BG_DARK());
        chatPanel.setBackground(AppConfig.BG_DARK());
        scrollPane.setBackground(AppConfig.BG_DARK());
        scrollPane.getViewport().setBackground(AppConfig.BG_DARK());
        if (particleBg != null) particleBg.setBackground(AppConfig.BG_DARK());
        // Repaint all components recursively
        refreshComponent(getContentPane());
        SwingUtilities.updateComponentTreeUI(this);
        repaint(); revalidate();
    }

    private void refreshComponent(Component c) {
        if (c instanceof ORYNBubble) { c.repaint(); return; }
        if (c instanceof JPanel p) {
            p.setBackground(new Color(0,0,0,0));
            for (Component child : p.getComponents()) refreshComponent(child);
        }
        c.repaint();
    }

    private void toggleSearch() {
        searchBar.setVisible(!searchBar.isVisible());
        headerPanel.revalidate(); headerPanel.repaint();
        if (searchBar.isVisible()) searchField.requestFocus();
        else { searchField.setText(""); clearHighlight(); }
    }

    private void performSearch(String q) {
        clearHighlight();
        if (q.isBlank()) return;
        String lq = q.toLowerCase();
        for (Component c : chatPanel.getComponents())
            if (c instanceof JPanel p) findBubbles(p, lq, true);
        chatPanel.repaint();
    }

    private void clearHighlight() {
        for (Component c : chatPanel.getComponents())
            if (c instanceof JPanel p) findBubbles(p, null, false);
        chatPanel.repaint();
    }

    private void findBubbles(JPanel p, String q, boolean highlight) {
        for (Component c : p.getComponents()) {
            if (c instanceof ORYNBubble b)
                b.setHighlighted(highlight && q != null && b.getMsg().getText().toLowerCase().contains(q));
            if (c instanceof JPanel sub) findBubbles(sub, q, highlight);
        }
    }

    private void saveChat() {
        if (history.isEmpty()) { AetherDialog.showInfo(this, "Save Chat", "No messages yet."); return; }
        try { AetherDialog.showInfo(this, "Saved \u2713", "Chat saved to:\n" + ChatHistory.save(history)); }
        catch (IOException ex) { AetherDialog.showError(this, "Save Failed", ex.getMessage()); }
    }

    private void clearChat() {
        if (AetherDialog.showConfirm(this, "Clear Chat", "Clear all messages?")) {
            history.clear(); chatPanel.removeAll(); chatPanel.add(typingIndicator);
            chatPanel.revalidate(); chatPanel.repaint(); showWelcome();
        }
    }

    private void openSettings() {
        new SettingsPanel(this, profile, v -> {
            AppConfig.refreshFonts(); repaintAll();
            if (!profile.userName.isEmpty()) ((SmartChatBot)bot).setUserName(profile.userName);
        }).setVisible(true);
    }

    private MouseAdapter click(java.util.function.Consumer<MouseEvent> h) {
        return new MouseAdapter() { public void mouseClicked(MouseEvent e) { h.accept(e); } };
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Inner components
    // ══════════════════════════════════════════════════════════════════════════

    /** Rose Noir glassmorphism input field */
    static class GlassInputPanel extends JPanel {
        private final JTextField field;
        private boolean focused = false;
        private float gp = 0f;
        private final Timer pt;

        GlassInputPanel() {
            setOpaque(false); setLayout(new BorderLayout());
            field = new JTextField();
            field.setFont(AppConfig.FONT_INPUT);
            field.setForeground(AppConfig.TEXT_PRIMARY());
            field.setBackground(new Color(0,0,0,0));
            field.setCaretColor(AppConfig.ACCENT_GLOW);
            field.setBorder(new EmptyBorder(10, 16, 10, 16));
            field.setOpaque(false);
            add(field, BorderLayout.CENTER);
            setBorder(new EmptyBorder(2,0,2,0));
            pt = new Timer(40, e -> { gp += 0.1f; repaint(); });
            field.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { focused=true; pt.start(); }
                public void focusLost(FocusEvent e)   { focused=false; pt.stop(); repaint(); }
            });
        }

        JTextField getField() { return field; }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int r = 26;
            int w = getWidth(), h = getHeight();

            // Glass base
            g2.setColor(AppConfig.BG_INPUT());
            g2.fillRoundRect(0, 0, w, h, r, r);

            // Top shimmer
            GradientPaint sh = new GradientPaint(0,0,new Color(255,200,220,20),0,h/2,new Color(0,0,0,0));
            g2.setPaint(sh); g2.fillRoundRect(0,0,w,h,r,r);

            if (focused) {
                float gl = (float)(0.4 + 0.6 * Math.abs(Math.sin(gp)));
                // Inner rose glow border
                g2.setColor(new Color(212, 100, 150, (int)(90 + 110*gl)));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1,1,w-2,h-2,r,r);
                // Outer soft glow
                g2.setColor(new Color(212, 100, 150, (int)(15*gl)));
                g2.setStroke(new BasicStroke(5f));
                g2.drawRoundRect(-1,-1,w+1,h+1,r+3,r+3);
            } else {
                g2.setColor(AppConfig.BORDER_SUBTLE());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,w-1,h-1,r,r);
            }
            g2.dispose(); super.paintComponent(g);
        }

        @Override public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 48);
        }
    }

    /** Rose gold animated send button */
    static class RoseButton extends JButton {
        private float ha = 0f;
        private boolean hov = false;
        private Timer at;

        RoseButton() {
            super("Send");
            setFont(AppConfig.FONT_BUTTON);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(10, 24, 10, 24));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov=true; anim(); }
                public void mouseExited(MouseEvent e)  { hov=false; anim(); }
            });
        }

        private void anim() {
            if (at != null) at.stop();
            at = new Timer(16, e -> {
                ha += hov ? 0.1f : -0.1f;
                ha = Math.max(0f, Math.min(1f, ha));
                repaint();
                if ((!hov && ha <= 0) || (hov && ha >= 1)) at.stop();
            });
            at.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int r = 26, w = getWidth(), h = getHeight();

            // Interpolate rose → gold on hover
            int red   = (int)(180 + ha * 50);
            int green = (int)(80  + ha * 100);
            int blue  = (int)(120 + ha * (-40));
            Color c = new Color(Math.min(255,red), Math.min(255,Math.max(0,green)), Math.max(0,blue));

            // Shadow
            g2.setColor(new Color(0,0,0,60)); g2.fillRoundRect(2,3,w-2,h-2,r,r);

            // Glass gradient fill
            GradientPaint fill = new GradientPaint(0,0,c.brighter(),w,h,c.darker());
            g2.setPaint(fill); g2.fillRoundRect(0,0,w-1,h-1,r,r);

            // Top shimmer
            g2.setColor(new Color(255,255,255,35)); g2.fillRoundRect(3,3,w-6,h/2-3,r-4,r-4);

            // Glow ring on hover
            if (ha > 0.05f) {
                g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(50*ha)));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(-1,-1,w+1,h+1,r+2,r+2);
            }
            g2.dispose(); super.paintComponent(g);
        }
    }
}
