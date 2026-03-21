package chatbot.ui;

import chatbot.model.Message;
import chatbot.service.ChatService;
import chatbot.service.SmartChatBot;
import chatbot.intelligence.*;
import chatbot.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ORYN Lumina v3.1 — premium feel with particle background,
 * ORYN Trademark bubbles, styled dialogs, and full feature set.
 */
public class ChatBotGUI extends JFrame {

    private final ChatService   bot     = new SmartChatBot();
    private final List<Message> history = new ArrayList<>();
    private UserProfile profile;

    private JPanel             chatPanel;
    private JScrollPane        scrollPane;
    private JTextField         inputField;
    private AnimatedSendButton sendButton;
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
        AppConfig.setFontSize(profile.fontSize > 0 ? profile.fontSize : 15);
        SoundManager.setEnabled(profile.soundOn);
        if (!profile.userName.isEmpty()) ((SmartChatBot)bot).setUserName(profile.userName);
        initWindow();
        initComponents();
        showWelcome();
    }

    private void initWindow() {
        setTitle("ORYN — Lumina");
        setSize(AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(460, 540));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppConfig.BG_DARK());
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        headerPanel = buildHeader();
        add(headerPanel,  BorderLayout.NORTH);
        add(buildChat(),  BorderLayout.CENTER);
        add(buildBottom(),BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (AppConfig.isDark()) {
                    GradientPaint bg = new GradientPaint(0,0,new Color(8,18,30),getWidth(),0,new Color(24,14,6));
                    g2.setPaint(bg); g2.fillRect(0,0,getWidth(),getHeight());
                    // teal glow left
                    RadialGradientPaint tg = new RadialGradientPaint(new Point(55,getHeight()/2),90,
                        new float[]{0f,1f},new Color[]{new Color(0,190,165,70),new Color(0,190,165,0)});
                    g2.setPaint(tg); g2.fillOval(-25,-25,170,getHeight()+50);
                    // amber glow right
                    RadialGradientPaint ag = new RadialGradientPaint(new Point(getWidth()-35,getHeight()/2),65,
                        new float[]{0f,1f},new Color[]{new Color(210,135,0,60),new Color(210,135,0,0)});
                    g2.setPaint(ag); g2.fillOval(getWidth()-100,-20,130,getHeight()+40);
                } else {
                    GradientPaint bg = new GradientPaint(0,0,new Color(215,240,237),getWidth(),0,new Color(250,242,222));
                    g2.setPaint(bg); g2.fillRect(0,0,getWidth(),getHeight());
                }
                // bottom glow line
                GradientPaint line = new GradientPaint(0,0,AppConfig.ACCENT,getWidth()/2,0,AppConfig.ACCENT_GLOW,true);
                g2.setPaint(line); g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(11,16,11,16));

        // Left
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));
        left.setOpaque(false);
        left.add(buildOrbAvatar());
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel,BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        JLabel name = new JLabel(AppConfig.BOT_NAME);
        name.setFont(new Font("Segoe UI",Font.BOLD,17));
        name.setForeground(AppConfig.ACCENT_GLOW);
        JLabel tag = new JLabel(AppConfig.BOT_TAGLINE+"  ·  Lumina v3.1");
        tag.setFont(new Font("Segoe UI",Font.PLAIN,10));
        tag.setForeground(new Color(AppConfig.ACCENT_AMBER.getRed(),AppConfig.ACCENT_AMBER.getGreen(),AppConfig.ACCENT_AMBER.getBlue(),190));
        namePanel.add(name); namePanel.add(tag);
        left.add(namePanel);

        // Right icons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,4,0));
        right.setOpaque(false);
        themeToggleBtn = hIcon(AppConfig.isDark()?"☀":"🌙","Toggle Theme");
        JLabel srch  = hIcon("🔍","Search");
        JLabel save  = hIcon("💾","Save Chat");
        JLabel pdf   = hIcon("📄","Export PDF");
        JLabel clr   = hIcon("🗑","Clear Chat");
        JLabel sets  = hIcon("⚙","Settings");
        themeToggleBtn.addMouseListener(click(e->toggleTheme()));
        srch.addMouseListener(click(e->toggleSearch()));
        save.addMouseListener(click(e->saveChat()));
        pdf.addMouseListener(click(e->PdfExporter.export(history,this)));
        clr.addMouseListener(click(e->clearChat()));
        sets.addMouseListener(click(e->openSettings()));
        for(JLabel l:new JLabel[]{themeToggleBtn,srch,save,pdf,clr,sets}) right.add(l);

        content.add(left,BorderLayout.WEST);
        content.add(right,BorderLayout.EAST);

        searchBar = buildSearchBar();

        header.setLayout(new BoxLayout(header,BoxLayout.Y_AXIS));
        content.setAlignmentX(LEFT_ALIGNMENT);
        searchBar.setAlignmentX(LEFT_ALIGNMENT);
        header.add(content);
        header.add(searchBar);
        return header;
    }

    private JLabel buildOrbAvatar() {
        return new JLabel() {
            float p=0f;
            { new Timer(45,e->{p+=0.07f;repaint();}).start(); setPreferredSize(new Dimension(40,40)); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                float glow=(float)(0.5+0.5*Math.sin(p));
                g2.setColor(new Color(0,190,165,(int)(35+35*glow))); g2.fillOval(1,1,38,38);
                RadialGradientPaint c=new RadialGradientPaint(new Point(20,17),14,new float[]{0f,0.55f,1f},
                    new Color[]{new Color(190,255,242),new Color(0,195,168),new Color(0,95,85)});
                g2.setPaint(c); g2.fillOval(6,6,28,28);
                g2.setFont(new Font("Segoe UI",Font.BOLD,13)); g2.setColor(new Color(0,28,22));
                FontMetrics fm=g2.getFontMetrics(); String s="✦";
                g2.drawString(s,20-fm.stringWidth(s)/2,25);
                g2.dispose();
            }
        };
    }

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new BorderLayout(8,0));
        bar.setBackground(AppConfig.BG_PANEL());
        bar.setBorder(new EmptyBorder(6,16,8,16));
        bar.setVisible(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        searchField = new JTextField();
        searchField.setFont(AppConfig.FONT_INPUT);
        searchField.setForeground(AppConfig.TEXT_PRIMARY());
        searchField.setBackground(AppConfig.BG_INPUT());
        searchField.setCaretColor(AppConfig.ACCENT_GLOW);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.BORDER_SUBTLE(),1,true),
            new EmptyBorder(5,10,5,10)));
        JLabel close = hIcon("✕","Close Search");
        close.addMouseListener(click(e->toggleSearch()));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e){doSearch();}
            public void removeUpdate(javax.swing.event.DocumentEvent e){doSearch();}
            public void changedUpdate(javax.swing.event.DocumentEvent e){}
            void doSearch(){performSearch(searchField.getText());}
        });
        bar.add(new JLabel("  "),BorderLayout.WEST);
        bar.add(searchField,BorderLayout.CENTER);
        bar.add(close,BorderLayout.EAST);
        return bar;
    }

    // ── Chat area with particle background ────────────────────────────────────

    private JScrollPane buildChat() {
        // Particle layer
        particleBg = new ParticleBackground();
        particleBg.setLayout(new BorderLayout());

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        chatPanel.setOpaque(false);
        chatPanel.setBorder(new EmptyBorder(16,12,16,12));

        typingIndicator = new TypingIndicator();
        typingIndicator.setVisible(false);
        typingIndicator.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Wrap chatPanel in a plain bg panel
        JPanel chatWrapper = new JPanel(new BorderLayout());
        chatWrapper.setOpaque(false);
        chatWrapper.add(chatPanel,BorderLayout.NORTH);

        particleBg.add(chatWrapper,BorderLayout.CENTER);

        scrollPane = new JScrollPane(particleBg);
        scrollPane.setBackground(AppConfig.BG_DARK());
        scrollPane.getViewport().setBackground(AppConfig.BG_DARK());
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
            @Override protected void configureScrollBarColors(){thumbColor=new Color(0,130,110,100);trackColor=AppConfig.BG_DARK();}
            @Override protected JButton createIncreaseButton(int o){JButton b=new JButton();b.setPreferredSize(new Dimension(4,0));return b;}
            @Override protected JButton createDecreaseButton(int o){JButton b=new JButton();b.setPreferredSize(new Dimension(4,0));return b;}
        });

        particleBg.startAnimation();
        return scrollPane;
    }

    // ── Bottom input ──────────────────────────────────────────────────────────

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setColor(AppConfig.BG_PANEL()); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(0,155,130,55)); g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0,0,getWidth(),0); g2.dispose();
            }
        };
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10,14,16,14));
        statusLabel=new JLabel("  ✦ ORYN Lumina is ready");
        statusLabel.setFont(new Font("Segoe UI",Font.PLAIN,11));
        statusLabel.setForeground(AppConfig.TEXT_SECONDARY());
        JPanel row=new JPanel(new BorderLayout(10,0));
        row.setOpaque(false); row.setBorder(new EmptyBorder(7,0,0,0));
        GlowInputPanel gip=new GlowInputPanel();
        inputField=gip.getField();
        inputField.addActionListener(e->handleSend());
        sendButton=new AnimatedSendButton();
        sendButton.addActionListener(e->handleSend());
        row.add(gip,BorderLayout.CENTER);
        row.add(sendButton,BorderLayout.EAST);
        // Suggestion chips
        suggestionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        suggestionBar.setOpaque(false);
        suggestionBar.setVisible(false);

        bottom.add(statusLabel,BorderLayout.NORTH);
        bottom.add(suggestionBar, BorderLayout.CENTER);
        bottom.add(row,BorderLayout.SOUTH);
        return bottom;
    }

    void showSuggestions(String[] suggestions) {
        suggestionBar.removeAll();
        for (String s : suggestions) {
            JLabel chip = new JLabel(s);
            chip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            chip.setForeground(AppConfig.ACCENT);
            chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(AppConfig.ACCENT.getRed(), AppConfig.ACCENT.getGreen(), AppConfig.ACCENT.getBlue(), 80), 1, true),
                new EmptyBorder(3, 10, 3, 10)
            ));
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.setBackground(new Color(AppConfig.ACCENT.getRed(), AppConfig.ACCENT.getGreen(), AppConfig.ACCENT.getBlue(), 15));
            chip.setOpaque(true);
            chip.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    inputField.setText(s);
                    handleSend();
                    suggestionBar.setVisible(false);
                }
                public void mouseEntered(MouseEvent e) { chip.setForeground(AppConfig.ACCENT_GLOW); }
                public void mouseExited(MouseEvent e)  { chip.setForeground(AppConfig.ACCENT); }
            });
            suggestionBar.add(chip);
        }
        suggestionBar.setVisible(true);
        suggestionBar.revalidate();
        suggestionBar.repaint();
    }

    // ── Chat logic ────────────────────────────────────────────────────────────

    private void showWelcome() {
        SmartChatBot smartBot = (SmartChatBot) bot;
        chatbot.intelligence.LongMemory mem  = smartBot.getMemory();
        chatbot.intelligence.BondSystem  bond = smartBot.getBond();

        String welcomeMsg;
        if (mem.isFirstTodaySession()) {
            // Daily digest on first open of the day
            welcomeMsg = chatbot.intelligence.DailyDigest.generate(profile.userName, mem, bond);
        } else {
            // Return greeting
            welcomeMsg = bond.getGreetingFlavour(profile.userName) +
                "\nType **help** to see what I can do, or just say hi!";
        }
        addMsg(new Message(welcomeMsg, Message.Sender.BOT), true);
        // Show smart suggestions after welcome
        SwingUtilities.invokeLater(this::showWelcomeSuggestions);
    }

    private void showWelcomeSuggestions() {
        showSuggestions(new String[]{"Tell me something interesting", "What can you do?", "Give me a quote"});
    }

    private void handleSend() {
        String text=inputField.getText().trim();
        if(text.isBlank()) return;
        inputField.setText(""); inputField.setEnabled(false); sendButton.setEnabled(false);
        setStatus("✦ ORYN is thinking...");
        SoundManager.playSend();
        Message um=new Message(text,Message.Sender.USER);
        history.add(um); addMsg(um,true); showTyping(true);
        new SwingWorker<String,Void>(){
            @Override protected String doInBackground() throws Exception {
                Thread.sleep(AppConfig.TYPING_DELAY_MS); return bot.getReply(text);
            }
            @Override protected void done(){
                try{
                    String r=get(); showTyping(false);
                    Message bm=new Message(r,Message.Sender.BOT);
                    history.add(bm); addMsg(bm,true); SoundManager.playChime();
                    // Smart suggestions based on reply content
                    SwingUtilities.invokeLater(()->showSmartSuggestions(r, text));
                    if(text.equalsIgnoreCase("bye")||text.equalsIgnoreCase("exit")){
                        Timer t=new Timer(1200,ev->System.exit(0));t.setRepeats(false);t.start();
                    }
                }catch(Exception ex){showTyping(false);addMsg(new Message("⚠ Error.",Message.Sender.BOT),true);}
                finally{inputField.setEnabled(true);sendButton.setEnabled(true);inputField.requestFocus();setStatus("  ✦ ORYN Lumina is ready");}
            }
        }.execute();
    }

    private void addMsg(Message msg, boolean animate) {
        chatPanel.remove(typingIndicator);

        // Reaction row wrapper
        JPanel rowWrap = new JPanel();
        rowWrap.setLayout(new BoxLayout(rowWrap,BoxLayout.Y_AXIS));
        rowWrap.setOpaque(false);
        rowWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));

        boolean isUser = msg.getSender()==Message.Sender.USER;
        ORYNBubble bubble = new ORYNBubble(msg);

        // Bubble row
        JPanel bubbleRow = new JPanel(new BorderLayout());
        bubbleRow.setOpaque(false);
        if(isUser){ bubbleRow.add(Box.createHorizontalGlue(),BorderLayout.WEST); bubbleRow.add(bubble,BorderLayout.EAST); }
        else       { bubbleRow.add(bubble,BorderLayout.WEST); bubbleRow.add(Box.createHorizontalGlue(),BorderLayout.EAST); }

        // Reaction row
        JPanel reactions = buildReactionRow(isUser);
        JPanel reactAlign = new JPanel(new BorderLayout());
        reactAlign.setOpaque(false);
        if(isUser){ reactAlign.add(Box.createHorizontalGlue(),BorderLayout.WEST); reactAlign.add(reactions,BorderLayout.EAST); }
        else       { reactAlign.add(reactions,BorderLayout.WEST); reactAlign.add(Box.createHorizontalGlue(),BorderLayout.EAST); }

        // Right-click copy
        JPopupMenu pop = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("Copy message");
        copyItem.addActionListener(e->{
            java.awt.datatransfer.StringSelection s = new java.awt.datatransfer.StringSelection(msg.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s,null);
        });
        pop.add(copyItem);
        bubble.setComponentPopupMenu(pop);

        rowWrap.add(bubbleRow);
        rowWrap.add(reactAlign);

        // Hover to show reactions
        MouseAdapter hov = new MouseAdapter(){
            public void mouseEntered(MouseEvent e){reactions.setVisible(true);}
            public void mouseExited(MouseEvent e){if(!rowWrap.contains(e.getPoint()))reactions.setVisible(false);}
        };
        bubble.addMouseListener(hov);
        rowWrap.addMouseListener(hov);

        chatPanel.add(rowWrap);
        chatPanel.add(Box.createVerticalStrut(8));
        chatPanel.add(typingIndicator);
        chatPanel.revalidate(); chatPanel.repaint(); scrollToBottom();

        if(animate){
            // Fade in
            rowWrap.putClientProperty("alpha",0f);
            Timer ft=new Timer(16,null);
            ft.addActionListener(e->{
                Float a=(Float)rowWrap.getClientProperty("alpha");
                a=Math.min(1f,a+0.09f);
                rowWrap.putClientProperty("alpha",a);
                rowWrap.repaint();
                if(a>=1f) ft.stop();
            });
            ft.start();
        }
    }

    private JPanel buildReactionRow(boolean isUser) {
        JPanel panel = new JPanel(new FlowLayout(isUser?FlowLayout.RIGHT:FlowLayout.LEFT,3,0));
        panel.setOpaque(false);
        panel.setVisible(false);
        String[] emojis={"👍","❤","😄"};
        for(String em:emojis){
            JLabel l=new JLabel(em);
            l.setFont(new Font("Segoe UI Emoji",Font.PLAIN,13));
            l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            l.setBorder(new EmptyBorder(1,3,1,3));
            l.addMouseListener(new MouseAdapter(){
                boolean on=false;
                public void mouseClicked(MouseEvent e){
                    on=!on;
                    l.setFont(new Font("Segoe UI Emoji",Font.PLAIN,on?16:13));
                    panel.revalidate();
                }
            });
            panel.add(l);
        }
        return panel;
    }

    private void showTyping(boolean show){
        if(show) typingIndicator.start(); else typingIndicator.stop();
        chatPanel.revalidate(); chatPanel.repaint(); scrollToBottom();
    }

    private void scrollToBottom(){
        SwingUtilities.invokeLater(()->{JScrollBar b=scrollPane.getVerticalScrollBar();b.setValue(b.getMaximum());});
    }

    private void setStatus(String t){statusLabel.setText("  "+t);}

    // ── Actions ───────────────────────────────────────────────────────────────

    private void showSmartSuggestions(String botReply, String userInput) {
        String lower = userInput.toLowerCase();
        String[] chips;
        if (lower.contains("joke"))         chips = new String[]{"Tell me another joke", "Give me a quote", "Trivia question"};
        else if (lower.contains("wiki") || lower.contains("tell me about"))
                                            chips = new String[]{"Tell me more", "Related topic?", "Give me a trivia"};
        else if (lower.contains("trivia"))  chips = new String[]{"Another trivia", "Tell me the answer", "Different topic"};
        else if (lower.contains("weather")) chips = new String[]{"Weather tomorrow?", "Tell me a fact", "What else can you do?"};
        else if (lower.contains("help"))    chips = new String[]{"Tell me about black holes", "Give me a joke", "What's today's date?"};
        else if (lower.contains("hello") || lower.contains("hi"))
                                            chips = new String[]{"What can you do?", "Tell me something cool", "Give me a quote"};
        else if (lower.contains("debate"))  chips = new String[]{"Debate another topic", "I agree with FOR", "I agree with AGAINST"};
        else if (lower.contains("score"))   chips = new String[]{"How do I level up?", "Show my interests", "Tell me a trivia"};
        else                                chips = new String[]{"Tell me more", "Give me a quote", "Trivia question"};
        showSuggestions(chips);
    }

    private void toggleTheme(){
        AppConfig.setDark(!AppConfig.isDark());
        profile.darkMode=AppConfig.isDark(); profile.save();
        themeToggleBtn.setText(AppConfig.isDark()?"☀":"🌙");
        repaintAll();
    }

    private void repaintAll(){
        getContentPane().setBackground(AppConfig.BG_DARK());
        chatPanel.setBackground(AppConfig.BG_DARK());
        scrollPane.setBackground(AppConfig.BG_DARK());
        scrollPane.getViewport().setBackground(AppConfig.BG_DARK());
        SwingUtilities.updateComponentTreeUI(this); repaint();
    }

    private void toggleSearch(){
        searchBar.setVisible(!searchBar.isVisible());
        headerPanel.revalidate(); headerPanel.repaint();
        if(searchBar.isVisible()) searchField.requestFocus();
        else{ searchField.setText(""); clearHighlight(); }
    }

    private void performSearch(String q){
        clearHighlight();
        if(q.isBlank()) return;
        String lq=q.toLowerCase();
        for(Component c:chatPanel.getComponents())
            if(c instanceof JPanel p) highlightPanel(p,lq);
        chatPanel.repaint();
    }

    private void highlightPanel(JPanel p, String q){
        for(Component c:p.getComponents()){
            if(c instanceof ORYNBubble b && b.getMsg().getText().toLowerCase().contains(q))
                b.setHighlighted(true);
            if(c instanceof JPanel sub) highlightPanel(sub,q);
        }
    }

    private void clearHighlight(){
        for(Component c:chatPanel.getComponents())
            if(c instanceof JPanel p) clearPanel(p);
        chatPanel.repaint();
    }

    private void clearPanel(JPanel p){
        for(Component c:p.getComponents()){
            if(c instanceof ORYNBubble b) b.setHighlighted(false);
            if(c instanceof JPanel sub) clearPanel(sub);
        }
    }

    private void saveChat(){
        if(history.isEmpty()){AetherDialog.showInfo(this,"Save Chat","No messages to save yet.");return;}
        try{String p=ChatHistory.save(history);AetherDialog.showInfo(this,"Saved \u2713","Chat saved to:\n"+p);}
        catch(IOException ex){AetherDialog.showError(this,"Save Failed",ex.getMessage());}
    }

    private void clearChat(){
        if(AetherDialog.showConfirm(this,"Clear Chat","Clear all messages? This cannot be undone.")){
            history.clear(); chatPanel.removeAll(); chatPanel.add(typingIndicator);
            chatPanel.revalidate(); chatPanel.repaint(); showWelcome();
        }
    }

    private void openSettings(){
        new SettingsPanel(this,profile,v->{AppConfig.refreshFonts();repaintAll();
            if(!profile.userName.isEmpty())((SmartChatBot)bot).setUserName(profile.userName);
        }).setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel hIcon(String icon, String tip){
        JLabel l=new JLabel(icon);
        l.setFont(new Font("Segoe UI Emoji",Font.PLAIN,15));
        l.setToolTipText(tip); l.setForeground(AppConfig.TEXT_SECONDARY());
        l.setBorder(new EmptyBorder(4,7,4,7));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){l.setForeground(AppConfig.ACCENT_GLOW);}
            public void mouseExited(MouseEvent e){l.setForeground(AppConfig.TEXT_SECONDARY());}
        });
        return l;
    }

    private MouseAdapter click(java.util.function.Consumer<MouseEvent> h){
        return new MouseAdapter(){public void mouseClicked(MouseEvent e){h.accept(e);}};
    }

    // ── Inner: GlowInputPanel ─────────────────────────────────────────────────

    static class GlowInputPanel extends JPanel {
        private final JTextField field;
        private boolean focused=false;
        private float gp=0f;
        private final Timer pt;

        GlowInputPanel(){
            setOpaque(false); setLayout(new BorderLayout());
            field=new JTextField();
            field.setFont(AppConfig.FONT_INPUT); field.setForeground(AppConfig.TEXT_PRIMARY());
            field.setBackground(AppConfig.BG_INPUT()); field.setCaretColor(AppConfig.ACCENT_GLOW);
            field.setBorder(new EmptyBorder(10,14,10,14)); field.setOpaque(false);
            add(field,BorderLayout.CENTER); setBorder(new EmptyBorder(2,0,2,0));
            pt=new Timer(40,e->{gp+=0.1f;repaint();});
            field.addFocusListener(new FocusAdapter(){
                public void focusGained(FocusEvent e){focused=true;pt.start();}
                public void focusLost(FocusEvent e){focused=false;pt.stop();repaint();}
            });
        }

        JTextField getField(){return field;}

        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int r=24;
            g2.setColor(AppConfig.BG_INPUT()); g2.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
            if(focused){
                float gl=(float)(0.4+0.6*Math.abs(Math.sin(gp)));
                g2.setColor(new Color(0,195,168,(int)(75+105*gl)));
                g2.setStroke(new BasicStroke(2f)); g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,r,r);
                g2.setColor(new Color(0,195,168,(int)(18*gl)));
                g2.setStroke(new BasicStroke(5f)); g2.drawRoundRect(-1,-1,getWidth()+1,getHeight()+1,r+3,r+3);
            }else{
                g2.setColor(AppConfig.BORDER_SUBTLE()); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,r,r);
            }
            g2.dispose(); super.paintComponent(g);
        }

        @Override public Dimension getPreferredSize(){return new Dimension(super.getPreferredSize().width,46);}
    }

    // ── Inner: AnimatedSendButton ─────────────────────────────────────────────

    static class AnimatedSendButton extends JButton {
        private float ha=0f; private boolean hov=false; private Timer at;

        AnimatedSendButton(){
            super("Send \u27A4");
            setFont(AppConfig.FONT_BUTTON); setForeground(Color.WHITE);
            setContentAreaFilled(false); setFocusPainted(false);
            setBorder(new EmptyBorder(10,22,10,22));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){hov=true;anim();}
                public void mouseExited(MouseEvent e){hov=false;anim();}
            });
        }

        private void anim(){
            if(at!=null)at.stop();
            at=new Timer(16,e->{
                ha+=hov?0.1f:-0.1f; ha=Math.max(0f,Math.min(1f,ha)); repaint();
                if((!hov&&ha<=0)||(hov&&ha>=1))at.stop();
            });
            at.start();
        }

        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int r=24,w=getWidth(),h=getHeight();
            Color c=new Color(Math.min(255,(int)(0+ha*195)),Math.max(0,(int)(168+ha*(128-168))),Math.max(0,(int)(138+ha*(0-138))));
            g2.setColor(new Color(0,0,0,55)); g2.fillRoundRect(2,3,w-2,h-2,r,r);
            g2.setPaint(new GradientPaint(0,0,c.brighter(),w,h,c.darker()));
            g2.fillRoundRect(0,0,w-1,h-1,r,r);
            if(ha>0.05f){g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(55*ha)));g2.setStroke(new BasicStroke(3f));g2.drawRoundRect(-1,-1,w+1,h+1,r+2,r+2);}
            g2.setColor(new Color(255,255,255,28)); g2.fillRoundRect(3,3,w-6,h/2-3,r-4,r-4);
            g2.dispose(); super.paintComponent(g);
        }
    }
}
