package com.aexiz.daviz.ui;

import javax.swing.*;
import java.awt.*;

public class JCoolBar extends JToolBar {

    private static final long serialVersionUID = -1881213485624315909L;

    protected Color backgroundBottom;

    public JCoolBar() {
        backgroundBottom = getBackground().brighter();
        setFloatable(false);
        setRollover(true);
    }

    public void addHorizontalGlue() {
        Component box = Box.createHorizontalGlue();
        box.setFocusable(false);
        add(box);
    }

    public Color getBackgroundBottom() {
        return backgroundBottom;
    }

    public void setBackgroundBottom(Color color) {
        Color old = backgroundBottom;
        backgroundBottom = color;
        firePropertyChange("backgroundBottom", old, color);
    }

    public Color getBackgroundTop() {
        return getBackground();
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Color top = getBackgroundTop(), bottom = getBackgroundBottom();
        g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof JComponent) {
            ((JComponent) comp).setOpaque(false);
        }
        if (comp instanceof AbstractButton) {
            ((AbstractButton) comp).setFocusPainted(false);
        }
        super.addImpl(comp, constraints, index);
    }

}
