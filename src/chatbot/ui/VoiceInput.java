package chatbot.ui;

import chatbot.util.AppConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.function.Consumer;

/**
 * ORYN Voice Input — uses Windows Speech Recognition via PowerShell.
 * No external libraries required.
 * Click mic button → speak → text appears in input field.
 */
public class VoiceInput extends JLabel {

    private boolean listening   = false;
    private float   pulseAnim   = 0f;
    private Timer   pulseTimer;
    private final Consumer<String> onResult;

    public VoiceInput(Consumer<String> onResult) {
        this.onResult = onResult;
        setPreferredSize(new Dimension(42, 42));
        setToolTipText("Click to speak to ORYN");
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        pulseTimer = new Timer(40, e -> { pulseAnim += 0.12f; repaint(); });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { toggleListening(); }
        });
    }

    private void toggleListening() {
        if (listening) {
            stopListening();
        } else {
            startListening();
        }
    }

    private void startListening() {
        listening = true;
        pulseTimer.start();
        repaint();

        // Run speech recognition in background
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return recognizeSpeech();
            }

            @Override
            protected void done() {
                stopListening();
                try {
                    String result = get();
                    if (result != null && !result.isBlank()) {
                        onResult.accept(result.trim());
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    private void stopListening() {
        listening = false;
        pulseTimer.stop();
        pulseAnim = 0f;
        repaint();
    }

    /**
     * Uses PowerShell's System.Speech to capture voice input.
     * Creates a temp PS1 script, runs it, reads the result.
     */
    private String recognizeSpeech() {
        try {
            // Create temp PowerShell script
            String psScript = """
                Add-Type -AssemblyName System.Speech
                $recognizer = New-Object System.Speech.Recognition.SpeechRecognitionEngine
                $recognizer.SetInputToDefaultAudioDevice()
                $grammar = New-Object System.Speech.Recognition.DictationGrammar
                $recognizer.LoadGrammar($grammar)
                $result = $recognizer.Recognize([System.TimeSpan]::FromSeconds(5))
                if ($result) { Write-Output $result.Text }
                $recognizer.Dispose()
                """;

            Path tempScript = Files.createTempFile("oryn_voice_", ".ps1");
            Files.writeString(tempScript, psScript);

            Path tempOutput = Files.createTempFile("oryn_voice_out_", ".txt");

            ProcessBuilder pb = new ProcessBuilder(
                "powershell.exe", "-ExecutionPolicy", "Bypass",
                "-File", tempScript.toString()
            );
            pb.redirectOutput(tempOutput.toFile());
            pb.redirectErrorStream(true);

            Process p = pb.start();
            p.waitFor();

            String result = Files.readString(tempOutput).trim();
            Files.deleteIfExists(tempScript);
            Files.deleteIfExists(tempOutput);

            return result.isEmpty() ? null : result;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int cx = w / 2, cy = h / 2;
        int r = Math.min(w, h) / 2 - 2;

        if (listening) {
            // Pulsing ring animation
            float pulse = (float)(0.5 + 0.5 * Math.sin(pulseAnim));
            g2.setColor(new Color(212, 80, 120, (int)(40 + 50*pulse)));
            g2.fillOval(cx-r-4, cy-r-4, (r+4)*2, (r+4)*2);
            g2.setColor(new Color(212, 80, 120, (int)(20 + 30*pulse)));
            g2.fillOval(cx-r-8, cy-r-8, (r+8)*2, (r+8)*2);

            // Active mic — red fill
            RadialGradientPaint fill = new RadialGradientPaint(
                new Point(cx, cy-3), r,
                new float[]{0f, 0.6f, 1f},
                new Color[]{new Color(255,120,150), new Color(200,60,100), new Color(140,20,60)}
            );
            g2.setPaint(fill);
        } else {
            // Idle mic — rose glass
            RadialGradientPaint fill = new RadialGradientPaint(
                new Point(cx, cy-3), r,
                new float[]{0f, 0.6f, 1f},
                new Color[]{new Color(255,200,220), new Color(190,100,140), new Color(130,50,90)}
            );
            g2.setPaint(fill);
        }
        g2.fillOval(cx-r, cy-r, r*2, r*2);

        // Top shimmer
        g2.setColor(new Color(255, 255, 255, 45));
        g2.fillOval(cx-r+4, cy-r+3, r-4, r/2);

        // Border
        g2.setColor(new Color(255, 160, 190, 120));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(cx-r, cy-r, r*2, r*2);

        // Mic icon
        int ms = r/2;
        g2.setColor(new Color(255, 240, 248, 220));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Mic body
        g2.fillRoundRect(cx-ms/2, cy-ms, ms, (int)(ms*1.5), ms/2, ms/2);
        // Mic stand arc
        g2.drawArc(cx-ms, cy-(int)(ms*0.2), ms*2, ms, 0, -180);
        // Mic stand line
        g2.drawLine(cx, cy+(ms/2), cx, cy+ms);
        // Base
        g2.drawLine(cx-ms/2, cy+ms, cx+ms/2, cy+ms);

        g2.dispose();
    }

    public boolean isListening() { return listening; }
}
