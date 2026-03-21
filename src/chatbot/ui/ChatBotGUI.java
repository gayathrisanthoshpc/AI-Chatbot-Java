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
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatBotGUI extends JFrame {

    private final ChatService   bot     = new SmartChatBot();
    private final List<Message> history = new ArrayList<>();

    private JPanel             chatPanel;
    private JScrollPane        scrollPane;
    private JTextField         inputField;
    private AnimatedSendButton sendButton;
    private TypingIndicator    typingIndicator;
    private JLabel             statusLabel;

    public ChatBotGUI() {
        initWindow();
        initComponents();
        showWelcome();
    }

    private void initWindow() {
        setTitle("ORYN — Aether");
        setSize(AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(420, 520));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppConfig.BG_DARK);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        add(buildAuroraHeader(), BorderLayout.NORTH);
        add(buildChat(),         BorderLayout.CENTER);
        add(buildBottom(),       BorderLayout.SOUTH);
    }

    // ── Aurora Gradient Header ────────────────────────────────────────────────

    private JPanel buildAuroraHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint bg = new GradientPaint(0, 0, new Color(0, 30, 40), getWidth(), 0, new Color(30, 15, 5));
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                RadialGradientPaint teal = new RadialGradientPaint(new Point(60, getHeight()/2), 80,
                    new float[]{0f,1f}, new Color[]{new Color(0,200,170,80), new Color(0,200,170,0)});
                g2.setPaint(teal);
                g2.fillOval(-20,-20,160,getHeight()+40);
                RadialGradientPaint amber = new RadialGradientPaint(new Point(getWidth()-40, getHeight()/2), 70,
                    new float[]{0f,1f}, new Color[]{new Color(220,140,0,70), new Color(220,140,0,0)});
                g2.setPaint(amber);
                g2.fillOval(getWidth()-110,-20,140,getHeight()+40);
                GradientPaint line = new GradientPaint(0,0,AppConfig.AURORA_START,getWidth()/2,0,AppConfig.ACCENT_GLOW,true);
                g2.setPaint(line);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 72));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel orb = new JLabel() {
            float pulse = 0f;
            { new Timer(50, e -> { pulse += 0.08f; repaint(); }).start();
              setPreferredSize(new Dimension(42, 42)); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float glow = (float)(0.5 + 0.5 * Math.sin(pulse));
                g2.setColor(new Color(0, 200, 170, (int)(40+40*glow)));
                g2.fillOval(2,2,38,38);
                RadialGradientPaint core = new RadialGradientPaint(new Point(21,18),16,
                    new float[]{0f,0.6f,1f},
                    new Color[]{new Color(180,255,240),new Color(0,200,170),new Color(0,100,90)});
                g2.setPaint(core);
                g2.fillOval(7,7,28,28);
                g2.setFont(new Font("Segoe UI",Font.BOLD,14));
                g2.setColor(new Color(0,30,25));
                FontMetrics fm = g2.getFontMetrics();
                String sym="✦"; g2.drawString(sym,21-fm.stringWidth(sym)/2,26);
                g2.dispose();
            }
        };

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel(AppConfig.BOT_NAME);
        nameLabel.setFont(AppConfig.FONT_HEADER);
        nameLabel.setForeground(AppConfig.ACCENT_GLOW);
        JLabel tagLabel = new JLabel(AppConfig.BOT_TAGLINE + "  ·  Aether v2.0");
        tagLabel.setFont(AppConfig.FONT_TIMESTAMP);
        tagLabel.setForeground(new Color(AppConfig.ACCENT_AMBER.getRed(), AppConfig.ACCENT_AMBER.getGreen(), AppConfig.ACCENT_AMBER.getBlue(), 200));
        namePanel.add(nameLabel); namePanel.add(tagLabel);
        left.add(orb); left.add(namePanel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        JButton saveBtn  = aetherBtn("💾","Save Chat");
        JButton clearBtn = aetherBtn("🗑","Clear Chat");
        JButton resetBtn = aetherBtn("↺","Reset Memory");
        saveBtn.addActionListener(e  -> saveChat());
        clearBtn.addActionListener(e -> clearChat());
        resetBtn.addActionListener(e -> resetBot());
        right.add(saveBtn); right.add(clearBtn); right.add(resetBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Chat Area ─────────────────────────────────────────────────────────────

    private JScrollPane buildChat() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(AppConfig.BG_DARK);
        chatPanel.setBorder(new EmptyBorder(14,10,14,10));
        typingIndicator = new TypingIndicator();
        typingIndicator.setVisible(false);
        typingIndicator.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBackground(AppConfig.BG_DARK);
        scrollPane.getViewport().setBackground(AppConfig.BG_DARK);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(0,120,100,120); trackColor = AppConfig.BG_DARK;
            }
            @Override protected JButton createIncreaseButton(int o) { JButton b=new JButton(); b.setPreferredSize(new Dimension(4,0)); return b; }
            @Override protected JButton createDecreaseButton(int o) { JButton b=new JButton(); b.setPreferredSize(new Dimension(4,0)); return b; }
        });
        return scrollPane;
    }

    // ── Bottom Input Panel ────────────────────────────────────────────────────

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(AppConfig.BG_PANEL); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(0,150,120,60)); g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0,0,getWidth(),0); g2.dispose();
            }
        };
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10,14,14,14));
        statusLabel = new JLabel("  ✦ ORYN Aether is ready");
        statusLabel.setFont(AppConfig.FONT_TIMESTAMP);
        statusLabel.setForeground(AppConfig.TEXT_SECONDARY);
        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(new EmptyBorder(7,0,0,0));
        GlowInputPanel glowInput = new GlowInputPanel();
        inputField = glowInput.getField();
        inputField.addActionListener(e -> handleSend());
        sendButton = new AnimatedSendButton();
        sendButton.addActionListener(e -> handleSend());
        inputRow.add(glowInput,  BorderLayout.CENTER);
        inputRow.add(sendButton, BorderLayout.EAST);
        bottom.add(statusLabel, BorderLayout.NORTH);
        bottom.add(inputRow,    BorderLayout.CENTER);
        return bottom;
    }

    // ── Chat Logic ────────────────────────────────────────────────────────────

    private void showWelcome() {
        appendMessage(new Message("Hello! I'm ORYN \uD83C\uDF1F\nYour intelligent assistant — light of knowledge.\nType 'help' to see what I can do, or just say hi!", Message.Sender.BOT), true);
    }

    private void handleSend() {
        String text = inputField.getText().trim();
        if (text.isBlank()) return;
        inputField.setText(""); inputField.setEnabled(false); sendButton.setEnabled(false);
        setStatus("✦ ORYN is thinking...");
        Message userMsg = new Message(text, Message.Sender.USER);
        history.add(userMsg); appendMessage(userMsg, true); showTyping(true);
        SwingWorker<String,Void> w = new SwingWorker<>() {
            @Override protected String doInBackground() throws Exception {
                Thread.sleep(AppConfig.TYPING_DELAY_MS); return bot.getReply(text);
            }
            @Override protected void done() {
                try {
                    String reply = get(); showTyping(false);
                    Message botMsg = new Message(reply, Message.Sender.BOT);
                    history.add(botMsg); appendMessage(botMsg, true);
                    if (text.equalsIgnoreCase("bye")||text.equalsIgnoreCase("exit")) {
                        Timer t = new Timer(1200, ev -> System.exit(0)); t.setRepeats(false); t.start();
                    }
                } catch (Exception ex) { showTyping(false); appendMessage(new Message("⚠ Error.", Message.Sender.BOT), true); }
                finally { inputField.setEnabled(true); sendButton.setEnabled(true); inputField.requestFocus(); setStatus("  ✦ ORYN Aether is ready"); }
            }
        };
        w.execute();
    }

    private void appendMessage(Message msg, boolean animate) {
        chatPanel.remove(typingIndicator);
        AetherMessageRow row = new AetherMessageRow(msg);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatPanel.add(row);
        chatPanel.add(Box.createVerticalStrut(6));
        chatPanel.add(typingIndicator);
        chatPanel.revalidate(); chatPanel.repaint(); scrollToBottom();
        if (animate) row.fadeIn();
    }

    private void showTyping(boolean show) {
        if (show) typingIndicator.start(); else typingIndicator.stop();
        chatPanel.revalidate(); chatPanel.repaint(); scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> { JScrollBar b = scrollPane.getVerticalScrollBar(); b.setValue(b.getMaximum()); });
    }

    private void setStatus(String t) { statusLabel.setText("  " + t); }

    private void saveChat() {
        if (history.isEmpty()) { JOptionPane.showMessageDialog(this,"No messages to save.","Save",JOptionPane.INFORMATION_MESSAGE); return; }
        try { String p = ChatHistory.save(history); JOptionPane.showMessageDialog(this,"Saved to:\n"+p,"Saved ✓",JOptionPane.INFORMATION_MESSAGE); }
        catch (IOException ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void clearChat() {
        if (JOptionPane.showConfirmDialog(this,"Clear all messages?","Clear",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            history.clear(); chatPanel.removeAll(); chatPanel.add(typingIndicator);
            chatPanel.revalidate(); chatPanel.repaint(); showWelcome();
        }
    }

    private void resetBot() { bot.reset(); appendMessage(new Message("Memory cleared! Fresh start \uD83C\uDF1F", Message.Sender.BOT), true); }

    private JButton aetherBtn(String icon, String tip) {
        JButton btn = new JButton(icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(getModel().isRollover()){g2.setColor(new Color(0,180,150,40));g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);}
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI Emoji",Font.PLAIN,14)); btn.setToolTipText(tip);
        btn.setForeground(AppConfig.TEXT_SECONDARY); btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(5,9,5,9)); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){btn.setForeground(AppConfig.ACCENT_GLOW);}
            public void mouseExited(MouseEvent e){btn.setForeground(AppConfig.TEXT_SECONDARY);}
        });
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Inner components
    // ══════════════════════════════════════════════════════════════════════════

    static class AetherMessageRow extends JPanel {
        private float alpha = 0f;
        private Timer fadeTimer;

        AetherMessageRow(Message msg) {
            boolean isUser = msg.getSender() == Message.Sender.USER;
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            setAlignmentX(LEFT_ALIGNMENT);
            JLabel avatar = buildAvatar(isUser);
            AetherBubble bubble = new AetherBubble(msg);
            if (isUser) { add(Box.createHorizontalGlue()); add(bubble); add(Box.createHorizontalStrut(8)); add(avatar); }
            else         { add(avatar); add(Box.createHorizontalStrut(8)); add(bubble); add(Box.createHorizontalGlue()); }
            setBorder(new EmptyBorder(2,4,2,4));
        }

        private JLabel buildAvatar(boolean isUser) {
            return new JLabel() {
                { setPreferredSize(new Dimension(32,32)); setMinimumSize(new Dimension(32,32)); setMaximumSize(new Dimension(32,32)); }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    if (!isUser) {
                        RadialGradientPaint p=new RadialGradientPaint(new Point(16,14),13,new float[]{0f,0.5f,1f},
                            new Color[]{new Color(180,255,240),new Color(0,200,170),new Color(0,80,70)});
                        g2.setPaint(p); g2.fillOval(3,3,26,26);
                        g2.setColor(new Color(0,200,170,80)); g2.setStroke(new BasicStroke(1.5f)); g2.drawOval(1,1,30,30);
                        g2.setFont(new Font("Segoe UI",Font.BOLD,11)); g2.setColor(new Color(0,30,25)); g2.drawString("O",11,20);
                    } else {
                        RadialGradientPaint p=new RadialGradientPaint(new Point(16,14),13,new float[]{0f,0.5f,1f},
                            new Color[]{new Color(255,230,180),new Color(220,140,40),new Color(120,60,10)});
                        g2.setPaint(p); g2.fillOval(3,3,26,26);
                        g2.setColor(new Color(220,140,40,80)); g2.setStroke(new BasicStroke(1.5f)); g2.drawOval(1,1,30,30);
                        g2.setFont(new Font("Segoe UI",Font.BOLD,11)); g2.setColor(new Color(60,20,0)); g2.drawString("U",11,20);
                    }
                    g2.dispose();
                }
            };
        }

        void fadeIn() {
            alpha = 0f;
            fadeTimer = new Timer(16, e -> {
                alpha = Math.min(1f, alpha + 0.08f);
                repaint();
                if (alpha >= 1f) fadeTimer.stop();
            });
            fadeTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.dispose(); super.paintComponent(g);
        }
    }

    static class AetherBubble extends JPanel {
        private final Message msg;
        private final boolean isUser;

        AetherBubble(Message msg) {
            this.msg=msg; this.isUser=msg.getSender()==Message.Sender.USER;
            setOpaque(false);
            setMaximumSize(new Dimension(AppConfig.MAX_BUBBLE_WIDTH+40,2000));
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            FontMetrics fmMsg=g2.getFontMetrics(AppConfig.FONT_MESSAGE);
            FontMetrics fmTime=g2.getFontMetrics(AppConfig.FONT_TIMESTAMP);
            String text=msg.getText(), time=msg.getTimestamp();
            List<String> lines=new ArrayList<>();
            StringBuilder line=new StringBuilder();
            for (String word : text.split(" ")) {
                String test=line.isEmpty()?word:line+" "+word;
                if (fmMsg.stringWidth(test)>AppConfig.MAX_BUBBLE_WIDTH) { if(!line.isEmpty())lines.add(line.toString()); line=new StringBuilder(word); }
                else line=new StringBuilder(test);
            }
            if (!line.isEmpty()) lines.add(line.toString());
            int lh=fmMsg.getHeight();
            int textW=lines.stream().mapToInt(fmMsg::stringWidth).max().orElse(60);
            int timeW=fmTime.stringWidth(time);
            int bW=Math.max(textW,timeW)+AppConfig.BUBBLE_PADDING*2+6;
            int bH=lines.size()*lh+fmTime.getHeight()+AppConfig.BUBBLE_PADDING*2+6;
            int r=AppConfig.BUBBLE_RADIUS;
            g2.setColor(new Color(0,0,0,50));
            g2.fill(new RoundRectangle2D.Float(3,3,bW,bH,r,r));
            if (isUser) { GradientPaint fill=new GradientPaint(0,0,new Color(160,90,20),bW,bH,new Color(100,50,5)); g2.setPaint(fill); }
            else        { GradientPaint fill=new GradientPaint(0,0,new Color(8,35,45),bW,bH,new Color(5,20,30)); g2.setPaint(fill); }
            g2.fill(new RoundRectangle2D.Float(0,0,bW,bH,r,r));
            g2.setColor(isUser?new Color(220,140,40,100):new Color(0,180,150,100));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0,0,bW,bH,r,r));
            if (!isUser) {
                GradientPaint tl=new GradientPaint(10,1,new Color(0,200,170,60),bW-10,1,new Color(0,200,170,0));
                g2.setPaint(tl); g2.setStroke(new BasicStroke(1f)); g2.drawLine(r/2,1,bW-r/2,1);
            }
            g2.setFont(AppConfig.FONT_MESSAGE);
            g2.setColor(isUser?AppConfig.TEXT_USER:AppConfig.TEXT_PRIMARY);
            int tx=AppConfig.BUBBLE_PADDING, ty=AppConfig.BUBBLE_PADDING+fmMsg.getAscent();
            for (String l:lines){g2.drawString(l,tx,ty);ty+=lh;}
            g2.setFont(AppConfig.FONT_TIMESTAMP);
            g2.setColor(isUser?new Color(255,200,120,160):AppConfig.TEXT_SECONDARY);
            g2.drawString(time,bW-timeW-AppConfig.BUBBLE_PADDING,bH-fmTime.getDescent()-4);
            setPreferredSize(new Dimension(bW+2,bH+4));
            g2.dispose();
        }

        @Override public Dimension getPreferredSize() { return new Dimension(AppConfig.MAX_BUBBLE_WIDTH+10,70); }
    }

    static class GlowInputPanel extends JPanel {
        private final JTextField field;
        private boolean focused=false;
        private float glowPulse=0f;
        private final Timer pulseTimer;

        GlowInputPanel() {
            setOpaque(false); setLayout(new BorderLayout());
            field=new JTextField();
            field.setFont(AppConfig.FONT_INPUT); field.setForeground(AppConfig.TEXT_PRIMARY);
            field.setBackground(AppConfig.BG_INPUT); field.setCaretColor(AppConfig.ACCENT_GLOW);
            field.setBorder(new EmptyBorder(9,14,9,14)); field.setOpaque(false);
            add(field,BorderLayout.CENTER); setBorder(new EmptyBorder(2,0,2,0));
            pulseTimer=new Timer(40,e->{glowPulse+=0.1f;repaint();});
            field.addFocusListener(new FocusAdapter(){
                public void focusGained(FocusEvent e){focused=true;pulseTimer.start();}
                public void focusLost(FocusEvent e){focused=false;pulseTimer.stop();repaint();}
            });
        }

        JTextField getField(){return field;}

        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int r=22;
            g2.setColor(AppConfig.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
            if(focused){
                float glow=(float)(0.4+0.6*Math.abs(Math.sin(glowPulse)));
                g2.setColor(new Color(0,200,170,(int)(80+100*glow)));
                g2.setStroke(new BasicStroke(2f)); g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,r,r);
                g2.setColor(new Color(0,200,170,(int)(20*glow)));
                g2.setStroke(new BasicStroke(4f)); g2.drawRoundRect(0,0,getWidth(),getHeight(),r+2,r+2);
            } else {
                g2.setColor(AppConfig.BORDER_SUBTLE); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,r,r);
            }
            g2.dispose(); super.paintComponent(g);
        }

        @Override public Dimension getPreferredSize(){return new Dimension(super.getPreferredSize().width,44);}
    }

    static class AnimatedSendButton extends JButton {
        private float hoverAnim=0f;
        private boolean hovered=false;
        private Timer animTimer;

        AnimatedSendButton(){
            super("Send \u27A4");
            setFont(AppConfig.FONT_BUTTON); setForeground(Color.WHITE);
            setContentAreaFilled(false); setFocusPainted(false);
            setBorder(new EmptyBorder(9,20,9,20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            animTimer=new Timer(16,e->{
                hoverAnim+=hovered?0.1f:-0.1f;
                hoverAnim=Math.max(0f,Math.min(1f,hoverAnim));
                repaint();
                if((!hovered&&hoverAnim<=0)||(hovered&&hoverAnim>=1))animTimer.stop();
            });
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){hovered=true;animTimer.start();}
                public void mouseExited(MouseEvent e){hovered=false;animTimer.start();}
            });
        }

        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int r=22,w=getWidth(),h=getHeight();
            int red=(int)(0+hoverAnim*200);
            int green=(int)(170+hoverAnim*(130-170));
            int blue=(int)(140+hoverAnim*(0-140));
            Color c=new Color(Math.min(255,red),Math.max(0,green),Math.max(0,blue));
            g2.setColor(new Color(0,0,0,60)); g2.fillRoundRect(2,3,w-2,h-2,r,r);
            GradientPaint fill=new GradientPaint(0,0,c.brighter(),w,h,c.darker());
            g2.setPaint(fill); g2.fillRoundRect(0,0,w-1,h-1,r,r);
            if(hoverAnim>0.1f){
                g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(60*hoverAnim)));
                g2.setStroke(new BasicStroke(3f)); g2.drawRoundRect(-1,-1,w+1,h+1,r+2,r+2);
            }
            g2.setColor(new Color(255,255,255,30)); g2.fillRoundRect(3,3,w-6,h/2-3,r-4,r-4);
            g2.dispose(); super.paintComponent(g);
        }
    }
}
