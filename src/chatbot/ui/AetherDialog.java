package chatbot.ui;

import chatbot.util.AppConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Styled dialog that matches the ORYN Aether theme.
 * Replaces plain JOptionPane for confirm/info/error messages.
 */
public class AetherDialog extends JDialog {

    public enum Type { INFO, CONFIRM, ERROR }

    private boolean confirmed = false;

    private AetherDialog(JFrame parent, String title, String message, Type type) {
        super(parent, title, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        build(title, message, type);
        pack();
        setLocationRelativeTo(parent);
    }

    private void build(String title, String message, Type type) {
        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, 20, 20));
                // Background
                GradientPaint bg = new GradientPaint(0, 0, AppConfig.BG_PANEL(), getWidth(), getHeight(),
                    AppConfig.isDark() ? new Color(8, 20, 28) : new Color(240, 248, 246));
                g2.setPaint(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));
                // Border glow
                g2.setColor(new Color(AppConfig.ACCENT.getRed(), AppConfig.ACCENT.getGreen(), AppConfig.ACCENT.getBlue(), 120));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, 20, 20));
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Icon + Title row
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);

        String icon = switch (type) {
            case INFO    -> "ℹ";
            case CONFIRM -> "⚠";
            case ERROR   -> "✕";
        };
        Color iconColor = switch (type) {
            case INFO    -> AppConfig.ACCENT;
            case CONFIRM -> AppConfig.ACCENT_GOLD;
            case ERROR   -> new Color(220, 80, 80);
        };

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLabel.setForeground(iconColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppConfig.FONT_HEADER);
        titleLabel.setForeground(AppConfig.TEXT_PRIMARY());

        titleRow.add(iconLabel);
        titleRow.add(titleLabel);

        // Message
        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(AppConfig.FONT_MESSAGE);
        msgArea.setForeground(AppConfig.TEXT_PRIMARY());
        msgArea.setBackground(new Color(0, 0, 0, 0));
        msgArea.setOpaque(false);
        msgArea.setEditable(false);
        msgArea.setFocusable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setPreferredSize(new Dimension(300, -1));
        msgArea.setBorder(new EmptyBorder(10, 0, 16, 0));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        if (type == Type.CONFIRM) {
            JButton noBtn = outlineBtn("No");
            JButton yesBtn = filledBtn("Yes", iconColor);
            noBtn.addActionListener(e  -> { confirmed = false; dispose(); });
            yesBtn.addActionListener(e -> { confirmed = true;  dispose(); });
            // ESC = No
            getRootPane().registerKeyboardAction(e -> { confirmed = false; dispose(); },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
            btnRow.add(noBtn);
            btnRow.add(yesBtn);
        } else {
            JButton okBtn = filledBtn("OK", iconColor);
            okBtn.addActionListener(e -> { confirmed = true; dispose(); });
            getRootPane().registerKeyboardAction(e -> { confirmed = true; dispose(); },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
            getRootPane().registerKeyboardAction(e -> { confirmed = true; dispose(); },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
            btnRow.add(okBtn);
        }

        root.add(titleRow, BorderLayout.NORTH);
        root.add(msgArea,  BorderLayout.CENTER);
        root.add(btnRow,   BorderLayout.SOUTH);
        setContentPane(root);
    }

    // ── Static helpers ────────────────────────────────────────────────────────

    public static void showInfo(JFrame parent, String title, String message) {
        new AetherDialog(parent, title, message, Type.INFO).setVisible(true);
    }

    public static void showError(JFrame parent, String title, String message) {
        new AetherDialog(parent, title, message, Type.ERROR).setVisible(true);
    }

    public static boolean showConfirm(JFrame parent, String title, String message) {
        AetherDialog d = new AetherDialog(parent, title, message, Type.CONFIRM);
        d.setVisible(true);
        return d.confirmed;
    }

    // ── Button styles ─────────────────────────────────────────────────────────

    private JButton filledBtn(String text, Color color) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(AppConfig.FONT_BUTTON);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(7, 20, 7, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton outlineBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(AppConfig.FONT_BUTTON);
        b.setForeground(AppConfig.TEXT_SECONDARY());
        b.setBackground(new Color(0,0,0,0));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.BORDER_SUBTLE(), 1, true),
            new EmptyBorder(6, 16, 6, 16)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
