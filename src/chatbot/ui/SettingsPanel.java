package chatbot.ui;

import chatbot.util.AppConfig;
import chatbot.util.SoundManager;
import chatbot.util.UserProfile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * ORYN Settings Panel — font size, sound, theme, username.
 */
public class SettingsPanel extends JDialog {

    private final UserProfile    profile;
    private final Consumer<Void> onApply;

    private JTextField nameField;
    private JSlider    fontSlider;
    private JCheckBox  soundCheck;
    private JCheckBox  darkCheck;

    public SettingsPanel(JFrame parent, UserProfile profile, Consumer<Void> onApply) {
        super(parent, "ORYN Settings", true);
        this.profile = profile;
        this.onApply = onApply;
        build();
        pack();
        setLocationRelativeTo(parent);
    }

    private void build() {
        setResizable(false);
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(AppConfig.BG_PANEL());
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Title
        JLabel title = new JLabel("⚙  Settings");
        title.setFont(AppConfig.FONT_HEADER);
        title.setForeground(AppConfig.ACCENT_GLOW);
        title.setAlignmentX(LEFT_ALIGNMENT);
        root.add(title);
        root.add(Box.createVerticalStrut(18));

        // Username
        root.add(label("Your Name"));
        nameField = styledField(profile.userName);
        root.add(nameField);
        root.add(Box.createVerticalStrut(14));

        // Font size
        root.add(label("Font Size: " + AppConfig.getFontSize()));
        fontSlider = new JSlider(11, 20, AppConfig.getFontSize());
        fontSlider.setBackground(AppConfig.BG_PANEL());
        fontSlider.setForeground(AppConfig.ACCENT);
        fontSlider.setMajorTickSpacing(3);
        fontSlider.setPaintTicks(true);
        fontSlider.setPaintLabels(true);
        fontSlider.setAlignmentX(LEFT_ALIGNMENT);
        fontSlider.setMaximumSize(new Dimension(300, 50));
        JLabel fontLabel = (JLabel) root.getComponent(root.getComponentCount() - 2);
        fontSlider.addChangeListener(e -> fontLabel.setText("Font Size: " + fontSlider.getValue()));
        root.add(fontSlider);
        root.add(Box.createVerticalStrut(14));

        // Sound toggle
        soundCheck = styledCheck("Enable notification sounds", profile.soundOn);
        root.add(soundCheck);
        root.add(Box.createVerticalStrut(8));

        // Dark mode toggle
        darkCheck = styledCheck("Dark mode (Aether theme)", profile.darkMode);
        root.add(darkCheck);
        root.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(LEFT_ALIGNMENT);

        JButton cancel = outlineBtn("Cancel");
        JButton apply  = filledBtn("Apply");

        cancel.addActionListener(e -> dispose());
        apply.addActionListener(e  -> applySettings());

        btns.add(cancel);
        btns.add(apply);
        root.add(btns);

        setContentPane(root);
        getContentPane().setBackground(AppConfig.BG_PANEL());
    }

    private void applySettings() {
        profile.userName = nameField.getText().trim();
        profile.soundOn  = soundCheck.isSelected();
        profile.darkMode = darkCheck.isSelected();
        profile.fontSize = fontSlider.getValue();

        AppConfig.setDark(profile.darkMode);
        AppConfig.setFontSize(profile.fontSize);
        SoundManager.setEnabled(profile.soundOn);
        profile.save();

        if (onApply != null) onApply.accept(null);
        dispose();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppConfig.FONT_TIMESTAMP);
        l.setForeground(AppConfig.TEXT_SECONDARY());
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledField(String value) {
        JTextField f = new JTextField(value, 22);
        f.setFont(AppConfig.FONT_INPUT);
        f.setForeground(AppConfig.TEXT_PRIMARY());
        f.setBackground(AppConfig.BG_INPUT());
        f.setCaretColor(AppConfig.ACCENT_GLOW);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.BORDER_SUBTLE(), 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(320, 36));
        return f;
    }

    private JCheckBox styledCheck(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(AppConfig.FONT_MESSAGE);
        cb.setForeground(AppConfig.TEXT_PRIMARY());
        cb.setBackground(AppConfig.BG_PANEL());
        cb.setAlignmentX(LEFT_ALIGNMENT);
        return cb;
    }

    private JButton filledBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(AppConfig.FONT_BUTTON);
        b.setForeground(Color.WHITE);
        b.setBackground(AppConfig.ACCENT);
        b.setBorder(new EmptyBorder(7, 18, 7, 18));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton outlineBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(AppConfig.FONT_BUTTON);
        b.setForeground(AppConfig.TEXT_SECONDARY());
        b.setBackground(AppConfig.BG_PANEL());
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.BORDER_SUBTLE(), 1, true),
            new EmptyBorder(6, 14, 6, 14)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
