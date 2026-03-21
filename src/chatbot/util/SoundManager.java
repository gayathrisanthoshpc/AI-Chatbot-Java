package chatbot.util;

import javax.sound.sampled.*;

/**
 * Plays a soft chime when ORYN replies.
 * Generates a sine wave tone — no audio file needed.
 */
public class SoundManager {

    private static boolean enabled = true;

    public static void setEnabled(boolean on) { enabled = on; }
    public static boolean isEnabled()         { return enabled; }

    /** Play a soft notification chime (non-blocking) */
    public static void playChime() {
        if (!enabled) return;
        new Thread(() -> {
            try {
                // Generate a soft 880Hz sine wave, 180ms duration
                float sampleRate = 44100f;
                int durationMs   = 180;
                int samples      = (int)(sampleRate * durationMs / 1000);
                byte[] buf       = new byte[samples * 2];
                double freq      = 880.0;

                for (int i = 0; i < samples; i++) {
                    // Sine wave with fade-in and fade-out envelope
                    double t       = i / sampleRate;
                    double envelope = Math.sin(Math.PI * i / samples); // smooth fade
                    double wave    = Math.sin(2 * Math.PI * freq * t) * envelope;
                    short  val     = (short)(wave * 4000); // low volume
                    buf[i*2]     = (byte)(val & 0xFF);
                    buf[i*2 + 1] = (byte)((val >> 8) & 0xFF);
                }

                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                if (!AudioSystem.isLineSupported(info)) return;

                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(format, buf, 0, buf.length);
                clip.start();

                // Wait for clip to finish then close
                Thread.sleep(durationMs + 50);
                clip.close();
            } catch (Exception ignored) {}
        }, "oryn-sound").start();
    }

    /** Play a lighter send sound */
    public static void playSend() {
        if (!enabled) return;
        new Thread(() -> {
            try {
                float sampleRate = 44100f;
                int durationMs   = 80;
                int samples      = (int)(sampleRate * durationMs / 1000);
                byte[] buf       = new byte[samples * 2];
                double freq      = 660.0;

                for (int i = 0; i < samples; i++) {
                    double envelope = Math.sin(Math.PI * i / samples);
                    double wave     = Math.sin(2 * Math.PI * freq * (i / sampleRate)) * envelope;
                    short  val      = (short)(wave * 2500);
                    buf[i*2]      = (byte)(val & 0xFF);
                    buf[i*2 + 1]  = (byte)((val >> 8) & 0xFF);
                }

                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                if (!AudioSystem.isLineSupported(info)) return;

                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(format, buf, 0, buf.length);
                clip.start();
                Thread.sleep(durationMs + 50);
                clip.close();
            } catch (Exception ignored) {}
        }, "oryn-sound-send").start();
    }
}
