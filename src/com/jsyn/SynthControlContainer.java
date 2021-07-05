package com.jsyn;

import javax.swing.*;
import java.awt.*;
import java.beans.VetoableChangeListener;

public class SynthControlContainer extends JPanel {
    private Point mouseClickLocation;
    private Synthesizer synth;
    protected boolean on;
    public SynthControlContainer(Synthesizer synth) {
        this.synth = synth;
    }

    public boolean isOn() {
        return on;
    }
    public void setOn(boolean on) {
        this.on = on;
    }

    public Point getMouseClickLocation() {
        return mouseClickLocation;
    }

    public void setMouseClickLocation(Point mouseClickLocation) {
        this.mouseClickLocation = mouseClickLocation;
    }

    @Override
    public Component add(Component component) {
        component.addKeyListener(synth.getKeyAdapter());
        return super.add(component);
    }

    @Override
    public Component add(String name, Component comp) {
        comp.addKeyListener(synth.getKeyAdapter());
        return super.add(name, comp);
    }

    @Override
    public Component add(Component comp, int index) {
        comp.addKeyListener(synth.getKeyAdapter());
        return super.add(comp, index);
    }

    @Override
    public void add(Component comp, Object constraints) {
        comp.addKeyListener(synth.getKeyAdapter());
        super.add(comp, constraints);
    }

    @Override
    public void add(Component comp, Object constraints, int index) {
        comp.addKeyListener(synth.getKeyAdapter());
        super.add(comp, constraints, index);
    }

}
