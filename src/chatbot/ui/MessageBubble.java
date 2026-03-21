package chatbot.ui;

import chatbot.model.Message;
import chatbot.util.AppConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A single chat bubble — paints rounded rect with correct alignment,
 * color, text wrapping, and timestamp.
 */
public class MessageBubble extends JPanel {

    private final Message message;
    private final boolean isUser;

    public MessageBubble(Message message) {
        this.message = message;
        this.isUser  = message.getSender() == Message.Sender.USER;

        setOpaque(false);
        setLayout(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 4));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        FontMetrics fmMsg  = g2.getFontMetrics(AppConfig.FONT_MESSAGE);
        FontMetrics fmTime = g2.getFontMetrics(AppConfig.FONT_TIMESTAMP);

        String text      = message.getText();
        String timestamp = message.getTimestamp();

        // Word wrap
        String[] words = text.split(" ");
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String test = line.isEmpty() ? word : line + " " + word;
            if (fmMsg.stringWidth(test) > AppConfig.MAX_BUBBLE_WIDTH) {
                if (!line.isEmpty()) lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (!line.isEmpty()) lines.add(line.toString());

        int lineH    = fmMsg.getHeight();
        int textW    = lines.stream().mapToInt(fmMsg::stringWidth).max().orElse(50);
        int textH    = lines.size() * lineH;
        int timeW    = fmTime.stringWidth(timestamp);
        int bubbleW  = Math.max(textW, timeW) + AppConfig.BUBBLE_PADDING * 2 + 4;
        int bubbleH  = textH + fmTime.getHeight() + AppConfig.BUBBLE_PADDING * 2 + 4;

        int x = isUser ? getWidth() - bubbleW - 8 : 8;
        int y = 4;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fill(new RoundRectangle2D.Float(x + 2, y + 2, bubbleW, bubbleH, AppConfig.BUBBLE_RADIUS, AppConfig.BUBBLE_RADIUS));

        // Bubble background
        Color bgColor = isUser ? AppConfig.BG_USER_BUBBLE() : AppConfig.BG_BOT_BUBBLE();
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(x, y, bubbleW, bubbleH, AppConfig.BUBBLE_RADIUS, AppConfig.BUBBLE_RADIUS));

        // Subtle border
        g2.setColor(isUser ? AppConfig.ACCENT : AppConfig.BORDER_SUBTLE());
        g2.setStroke(new BasicStroke(0.8f));
        g2.draw(new RoundRectangle2D.Float(x, y, bubbleW, bubbleH, AppConfig.BUBBLE_RADIUS, AppConfig.BUBBLE_RADIUS));

        // Text lines
        g2.setFont(AppConfig.FONT_MESSAGE);
        g2.setColor(isUser ? AppConfig.TEXT_USER() : AppConfig.TEXT_PRIMARY());
        int textX = x + AppConfig.BUBBLE_PADDING;
        int textY = y + AppConfig.BUBBLE_PADDING + fmMsg.getAscent();
        for (String l : lines) {
            g2.drawString(l, textX, textY);
            textY += lineH;
        }

        // Timestamp
        g2.setFont(AppConfig.FONT_TIMESTAMP);
        g2.setColor(AppConfig.TEXT_SECONDARY());
        int timeX = x + bubbleW - timeW - AppConfig.BUBBLE_PADDING;
        int timeY = y + bubbleH - fmTime.getDescent() - 4;
        g2.drawString(timestamp, timeX, timeY);

        // Preferred size hint
        setPreferredSize(new Dimension(getWidth(), bubbleH + 8));

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        // Minimum height estimate; actual is computed in paintComponent
        return new Dimension(super.getPreferredSize().width, 60);
    }
}
