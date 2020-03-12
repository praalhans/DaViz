package com.aexiz.daviz.ui.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;

public class JCoolBar extends JToolBar {

	private static final long serialVersionUID = -1881213485624315909L;

	public JCoolBar() {
		setFloatable(false);
		setRollover(true);
	}

	public void addHorizontalGlue() {
		Component box = Box.createHorizontalGlue();
		box.setFocusable(false);
		add(box);
	}

	protected void addImpl(Component comp, Object constraints, int index) {
		if (comp instanceof JComponent) {
			((JComponent) comp).setOpaque(false);
		}
		if (comp instanceof AbstractButton) {
			// Change background, so button is rendered differently
			// Since the component is not opaque, we do not render this color
			((AbstractButton) comp).setBackground(Color.BLACK);
			// We do allow the focus to be painted, for keyboard users
			((AbstractButton) comp).setFocusPainted(true);
			// But the focus is not captured after a click
			((AbstractButton) comp).setRequestFocusEnabled(false);
		}
		super.addImpl(comp, constraints, index);
	}

}
