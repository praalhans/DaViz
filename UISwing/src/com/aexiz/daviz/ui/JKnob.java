package com.aexiz.daviz.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

// Simple button-ish component, used by the Nodes of JGraph and Events of JTimeline
// The component still depends on an external UI that renders it.
public class JKnob extends JComponent {

    private static final long serialVersionUID = -2307896553409109184L;
    protected transient ChangeEvent changeEvent;
    protected boolean rollover;
    protected boolean pressed;
    protected boolean selected;
    private EventListenerList listenerList = new EventListenerList();
    private Color rolloverBackground;

    // State
    private Color rolloverForeground;
    private Color pressedBackground;
    private Color pressedForeground;
    private Color selectionBackground;
    private Color selectionForeground;
    private Color selectionRolloverBackground;
    private Color selectionRolloverForeground;
    private Color selectionPressedBackground;
    private Color selectionPressedForeground;

    JKnob() {
    }

    static Color blend(Color a, Color b) {
        int red = (int) (a.getRed() * 0.5f + b.getRed() * 0.5f);
        int green = (int) (a.getGreen() * 0.5f + b.getGreen() * 0.5f);
        int blue = (int) (a.getBlue() * 0.5f + b.getBlue() * 0.5f);
        return new Color(red, green, blue);
    }

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    // Colors

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    public boolean isRollover() {
        return rollover;
    }

    public void setRollover(boolean b) {
        if (rollover != b) {
            rollover = b;
            fireStateChanged();
        }
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean b) {
        if (pressed != b) {
            pressed = b;
            fireStateChanged();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean b) {
        if (selected != b) {
            selected = b;
            fireStateChanged();
        }
    }

    public void requestClearSelection() {
        setSelected(false);
    }

    public void requestSingleSelected() {
        setSelected(true);
    }

    public void requestAllSelected() {
        setSelected(true);
    }

    public Color getRolloverBackground() {
        return rolloverBackground;
    }

    public void setRolloverBackground(Color color) {
        Color old = rolloverBackground;
        rolloverBackground = color;
        firePropertyChange("rolloverBackground", old, color);
        repaint();
    }

    public Color getRolloverForeground() {
        return rolloverForeground;
    }

    public void setRolloverForeground(Color color) {
        Color old = rolloverForeground;
        rolloverForeground = color;
        firePropertyChange("rolloverForeground", old, color);
        repaint();
    }

    public Color getPressedBackground() {
        return pressedBackground;
    }

    public void setPressedBackground(Color color) {
        Color old = pressedBackground;
        pressedBackground = color;
        firePropertyChange("pressedBackground", old, color);
        repaint();
    }

    public Color getPressedForeground() {
        return pressedForeground;
    }

    public void setPressedForeground(Color color) {
        Color old = pressedForeground;
        pressedForeground = color;
        firePropertyChange("pressedForeground", old, color);
        repaint();
    }

    public Color getSelectionBackground() {
        return selectionBackground;
    }

    public void setSelectionBackground(Color color) {
        Color old = selectionBackground;
        selectionBackground = color;
        firePropertyChange("selectionBackground", old, color);
        repaint();
    }

    public Color getSelectionForeground() {
        return selectionForeground;
    }

    public void setSelectionForeground(Color color) {
        Color old = selectionForeground;
        selectionForeground = color;
        firePropertyChange("selectionForeground", old, color);
        repaint();
    }

    public Color getSelectionRolloverBackground() {
        if (selectionRolloverBackground == null && selectionBackground != null && rolloverBackground != null)
            return blend(selectionBackground, rolloverBackground);
        return selectionRolloverBackground;
    }

    public void setSelectionRolloverBackground(Color color) {
        Color old = selectionRolloverBackground;
        selectionRolloverBackground = color;
        firePropertyChange("selectionRolloverBackground", old, color);
        repaint();
    }

    public Color getSelectionRolloverForeground() {
        if (selectionRolloverForeground == null && selectionForeground != null && rolloverForeground != null)
            return blend(selectionForeground, rolloverForeground);
        return selectionRolloverForeground;
    }

    public void setSelectionRolloverForeground(Color color) {
        Color old = selectionRolloverForeground;
        selectionRolloverForeground = color;
        firePropertyChange("selectionRolloverForeground", old, color);
        repaint();
    }

    public Color getSelectionPressedBackground() {
        if (selectionPressedBackground == null && selectionBackground != null && pressedBackground != null)
            return blend(selectionBackground, pressedBackground);
        return selectionPressedBackground;
    }

    public void setSelectionPressedBackground(Color color) {
        Color old = selectionPressedBackground;
        selectionPressedBackground = color;
        firePropertyChange("selectionPressedBackground", old, color);
        repaint();
    }

    public Color getSelectionPressedForeground() {
        if (selectionPressedForeground == null && selectionForeground != null && pressedForeground != null)
            return blend(selectionForeground, pressedForeground);
        return selectionPressedForeground;
    }

    public void setSelectionPressedForeground(Color color) {
        Color old = selectionPressedForeground;
        selectionPressedForeground = color;
        firePropertyChange("selectionPressedForeground", old, color);
        repaint();
    }

    public ComponentUI getUI() {
        return ui;
    }

    public void setUI(ComponentUI ui) {
        super.setUI(ui);
    }

}
