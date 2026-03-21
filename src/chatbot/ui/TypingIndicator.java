package chatbot.ui;

import chatbot.util.AppConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Three animated dots shown while ORYN is "typing".
 */
public class TypingIndicator extends JPanel {

    private static final int DOT_SIZE    = 8;
    private static final int DOT_SPACING = 6;
    private static final int DOT_COUNT   = 3;

    private int activeDot = 0;
    private final Timer timer;

    public TypingIndicator() {
        setOpaque(false);
        setPreferredSize(new Dimension(90, 36));

        timer = new Timer(300, e -> {
            activeDot = (activeDot + 1) % DOT_COUNT;
            repaint();
        });
    }

    public void start() { timer.start(); setVisible(true); }
    public void stop()  { timer.stop();  setVisible(false); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Bubble background
        int bubbleW = DOT_COUNT * (DOT_SIZE + DOT_SPACING) + DOT_SPACING + 8;
        int bubbleH = DOT_SIZE + 18;
        g2.setColor(AppConfig.BG_BOT_BUBBLE());
        g2.fillRoundRect(8, 4, bubbleW, bubbleH, 14, 14);
        g2.setColor(AppConfig.BORDER_SUBTLE());
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(8, 4, bubbleW, bubbleH, 14, 14);

        // Dots
        int startX = 18;
        int dotY = 4 + (bubbleH - DOT_SIZE) / 2;
        for (int i = 0; i < DOT_COUNT; i++) {
            float alpha = (i == activeDot) ? 1.0f : 0.3f;
            g2.setColor(new Color(
                AppConfig.ACCENT_GLOW.getRed(),
                AppConfig.ACCENT_GLOW.getGreen(),
                AppConfig.ACCENT_GLOW.getBlue(),
                (int)(alpha * 255)
            ));
            g2.fill(new Ellipse2D.Float(startX + i * (DOT_SIZE + DOT_SPACING), dotY, DOT_SIZE, DOT_SIZE));
        }
        g2.dispose();
    }
}
