package com.aexiz.daviz.ui.plaf;

import com.aexiz.daviz.ui.JInfoTable;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public abstract class InfoTableUI extends ComponentUI {

    public void configureLabelComponent(JLabel c) {
    }

    public void configureSimplePropertyComponent(JTextField c) {
    }

    public void configureNestedPlaceholderComponent(JComponent c) {
    }

    public void configureFillerComponent(JComponent c) {
    }

    public void addComponent(JInfoTable c, JComponent child) {
        c.add(child);
    }

}
