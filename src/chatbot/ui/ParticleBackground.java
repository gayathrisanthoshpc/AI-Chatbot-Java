package chatbot.ui;

import chatbot.util.AppConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ORYN Lumina — animated floating particle background.
 * Renders subtle glowing dots that drift slowly upward.
 * Uses a transparent overlay so chat content shows through.
 */
public class ParticleBackground extends JPanel {

    private static final int PARTICLE_COUNT = 22;
    private final List<Particle> particles = new ArrayList<>();
    private final Timer animTimer;
    private final Random rng = new Random();

    public ParticleBackground() {
        setOpaque(false);
        setLayout(new BorderLayout());

        // Initialize particles at random positions
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(rng));
        }

        // Animate at ~40fps
        animTimer = new Timer(25, e -> {
            for (Particle p : particles) p.update(getWidth(), getHeight(), rng);
            repaint();
        });
    }

    public void startAnimation() { animTimer.start(); }
    public void stopAnimation()  { animTimer.stop(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getWidth() == 0 || getHeight() == 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Particle p : particles) {
            p.draw(g2);
        }
        g2.dispose();
    }

    // ── Particle ──────────────────────────────────────────────────────────────

    private static class Particle {
        float x, y, size, speed, alpha, alphaDir;
        Color color;

        Particle(Random rng) {
            reset(rng, 800, 700, true);
        }

        void reset(Random rng, int w, int h, boolean randomY) {
            x        = rng.nextFloat() * Math.max(w, 100);
            y        = randomY ? rng.nextFloat() * Math.max(h, 100) : Math.max(h, 100) + 10;
            size     = 1.5f + rng.nextFloat() * 3.5f;
            speed    = 0.2f + rng.nextFloat() * 0.5f;
            alpha    = 0.05f + rng.nextFloat() * 0.18f;
            alphaDir = rng.nextBoolean() ? 0.002f : -0.002f;

            // Teal or amber particles to match Aether palette
            int pick = rng.nextInt(3);
            if (pick == 0) {
                color = new Color(212, 100, 150); // rose
            } else if (pick == 1) {
                color = new Color(230, 170, 100); // gold
            } else {
                color = new Color(180, 80, 130);  // deep rose
            }
        }

        void update(int w, int h, Random rng) {
            // Drift upward with slight horizontal wobble
            y     -= speed;
            x     += (float) Math.sin(y * 0.02) * 0.3f;
            alpha += alphaDir;

            // Clamp alpha and reverse direction
            if (alpha > 0.22f) { alpha = 0.22f; alphaDir = -alphaDir; }
            if (alpha < 0.02f) { alpha = 0.02f; alphaDir = -alphaDir; }

            // Reset when off screen top
            if (y < -10) reset(rng, w, h, false);
        }

        void draw(Graphics2D g2) {
            // Outer soft glow
            float glowSize = size * 3.5f;
            RadialGradientPaint glow = new RadialGradientPaint(
                new Point((int)x, (int)y),
                glowSize,
                new float[]{0f, 1f},
                new Color[]{
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 80)),
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                }
            );
            g2.setPaint(glow);
            g2.fill(new Ellipse2D.Float(x - glowSize, y - glowSize, glowSize*2, glowSize*2));

            // Core bright dot
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255)));
            g2.fill(new Ellipse2D.Float(x - size/2, y - size/2, size, size));
        }
    }
}
