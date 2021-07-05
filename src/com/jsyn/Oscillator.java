package com.jsyn;

import com.jsyn.utils.RefWrapper;
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
    private static final int TONE_OFFSET_LIMIT = 2000;
//    public static final double FREQUENCY = 440;
    private double keyFrequency;
    private int wavetableStepSize;
    private int wavetableIndex;
    private RefWrapper<Integer> toneOffset = new RefWrapper<>(0);
    private RefWrapper<Integer> volume = new RefWrapper<>(100);
    private int freqMultiplier = 10000;
    private Wavetable wavetable = Wavetable.Sine;

    public Oscillator(Synthesizer synth) {
        super(synth);
        JComboBox<Wavetable> comboBox = new JComboBox<>(Wavetable.values());
        comboBox.setSelectedItem(Wavetable.Sine);
        comboBox.setBounds(10,10,75,25);
        comboBox.addItemListener(l -> {
            if (l.getStateChange() == ItemEvent.SELECTED) {
                wavetable = (Wavetable) l.getItem();
            }
            synth.updateWaveViewer();
        });
        add(comboBox);
        JLabel toneParameter = new JLabel("x0.000");
        toneParameter.setBounds(165, 65, 60, 25);
        toneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParameterMouseListeners(toneParameter, this,
                -TONE_OFFSET_LIMIT, TONE_OFFSET_LIMIT, 1, toneOffset, () -> {
            applyToneOffset();
            toneParameter.setText(" x" + String.format("%.3f",getToneOffset()));
            synth.updateWaveViewer();
        });
        add(toneParameter);
        JLabel toneText = new JLabel("Tone");
        toneText.setBounds(172, 45, 75,25);
        add(toneText);
        JLabel volumeParameter = new JLabel(" 100%");
        volumeParameter.setBounds(222, 65, 50, 25);
        volumeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParameterMouseListeners(volumeParameter, this, 0, 100, 1, volume, () -> {
            volumeParameter.setText(" " + volume.val + "% ");
            synth.updateWaveViewer();
        });
        add(volumeParameter);
        JLabel volumeText = new JLabel("Volume");
        volumeText.setBounds(225, 40, 75, 25);
        add(volumeText);
        setSize(279, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    public double getNextSample() {
        double sample = wavetable.getSamples()[wavetableIndex] * getVolumeMultiplier();
        wavetableIndex = (wavetableIndex + wavetableStepSize) % Wavetable.SIZE;
        return sample;
    }



    public enum Waveform {
        Sine, Square, Saw, Triangle, Noise
    }

    public void setKeyFrequency(double frequency) {
        keyFrequency = frequency;
        applyToneOffset();
    }

    public double[] getSampleWaveform(int numSamples) {
        double[] samples = new double[numSamples];
        double frequency = 1.0 / (numSamples / (double) Synthesizer.AudioInfo.SAMPLE_RATE) * 3.0; // 3.0 shows move waveform
        int index = 0;
        int stepSize = (int) (Wavetable.SIZE * Utils.Math.offsetTone(frequency, getToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
        for (int i = 0; i < numSamples; ++i) {
            samples[i] = wavetable.getSamples()[index] * getVolumeMultiplier();
            index = (index + stepSize) % Wavetable.SIZE;
        }
        return samples;
    }

    private double getToneOffset()
    {
        return toneOffset.val / 1000.0;
    }

    private double getVolumeMultiplier() {
        return volume.val / 100.0;
    }


    private void applyToneOffset() {
        wavetableStepSize = (int) (Wavetable.SIZE * Utils.Math.offsetTone(keyFrequency, getToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
    }
}
