package com.jsyn;

import com.jsyn.utils.Utils;
import org.lwjgl.system.CallbackI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Oscillator extends SynthControlContainer {
    private Waveform waveform = Waveform.Sine;
    private static final int TONE_OFFSET_LIMIT = 2000;
//    public static final double FREQUENCY = 440;
    private double keyFrequency;
    private double frequency;
    private int toneOffset;
    private int wavePos;
    private final Random random = new Random();
    private int freqMultiplier = 10000;

    public Oscillator(Synthesizer synth) {
        super(synth);
        JComboBox<Waveform> comboBox = new JComboBox<>
                (new Waveform[]{Waveform.Sine, Waveform.Square, Waveform.Saw, Waveform.Triangle, Waveform.Noise});
        comboBox.setSelectedItem(Waveform.Sine);
        comboBox.setBounds(10,10,75,25);
        comboBox.addItemListener(l -> {
            if (l.getStateChange() == ItemEvent.SELECTED) {
                waveform = (Waveform) l.getItem();
            }
        });
        add(comboBox);
        JLabel toneParameter = new JLabel("x0.000");
        toneParameter.setBounds(165, 65, 60, 25);
        toneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        toneParameter.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
                        new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB), new Point(0,0),"blank_cursor");
                setCursor(BLANK_CURSOR);
                mouseClickLocation = e.getLocationOnScreen();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });
        toneParameter.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseClickLocation.y != e.getYOnScreen()) {
                    boolean mouseMovingUp = mouseClickLocation.y - e.getYOnScreen() > 0;
                    if (mouseMovingUp && toneOffset < TONE_OFFSET_LIMIT) {
                        ++toneOffset;
                    }
                    else if (!mouseMovingUp && toneOffset > -TONE_OFFSET_LIMIT) {
                        --toneOffset;
                    }
                    applyToneOffset();
                    toneParameter.setText("x" + String.format("%.3f", getToneOffset()));
                }
                Utils.ParameterHandling.PARAMETER_ROBOT.mouseMove(mouseClickLocation.x, mouseClickLocation.y);
            }
        });
        add(toneParameter);
        JLabel toneText = new JLabel("Tone");
        toneText.setBounds(172, 45, 75,25);
        add(toneText);
        setSize(279, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    public enum Waveform {
        Sine, Square, Saw, Triangle, Noise
    }

    public double getKeyFrequency() {
        return frequency;
    }
    public void setKeyFrequency(double frequency) {
        keyFrequency = this.frequency = frequency;
        applyToneOffset();
    }
    private double getToneOffset()
    {
        return toneOffset / 1000d;
    }

    public double nextSample() {
        double tDivP = (wavePos++ / (double) Synthesizer.AudioInfo.SAMPLE_RATE) / (1d / frequency);
        switch (waveform) {
            case Sine:
                return Math.sin(Utils.Math.frequencyToAngularFrequency(frequency * (wavePos - 1) / Synthesizer.AudioInfo.SAMPLE_RATE));
            case Square:
                return Math.signum(Math.sin(Utils.Math.frequencyToAngularFrequency(frequency * (wavePos - 1) / Synthesizer.AudioInfo.SAMPLE_RATE)));
            case Saw:
                return 2d * (tDivP - Math.floor(0.5 + tDivP));
            case Triangle:
                return 2d * Math.abs(2d * (tDivP - Math.floor(0.5 + tDivP))) - 1;
            case Noise:
                return random.nextDouble();
            default:
                throw new RuntimeException("Oscillator set to unknown waveform");
        }
    }

    private void applyToneOffset() {
        frequency = keyFrequency * Math.pow(2, getToneOffset());
        System.out.println(frequency);
    }
}
