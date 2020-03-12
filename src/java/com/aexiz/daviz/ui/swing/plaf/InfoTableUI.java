package com.aexiz.daviz.ui.swing.plaf;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;

import com.aexiz.daviz.ui.swing.JInfoTable;

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
