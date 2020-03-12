package com.aexiz.daviz.ui.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.aexiz.daviz.ui.swing.JInfoTable;
import com.aexiz.daviz.ui.swing.InfoModel.PropertyModel;
import com.aexiz.daviz.ui.swing.plaf.InfoTableUI;

public class BasicInfoTableUI extends InfoTableUI {
	
	static final int DEPTH_SIZE = 12;
	
	GridBagLayout layout = new GridBagLayout();
	
	// Called by reflection code
	public static BasicInfoTableUI createUI(JComponent c) {
		return new BasicInfoTableUI();
	}
	
	public void configureLabelComponent(JLabel c) {
	}
	
	public void configureSimplePropertyComponent(JTextField c) {
		c.setMinimumSize(new Dimension(20, 20));
		c.setPreferredSize(new Dimension(20, 20));
	}
	
	public void configureNestedPlaceholderComponent(JComponent c) {
		c.setMinimumSize(new Dimension(20, 20));
		c.setPreferredSize(new Dimension(20, 20));
	}
	
	public void installUI(JComponent c) {
		c.setOpaque(false);
		c.setLayout(layout);
		c.setBackground(UIManager.getColor("Control.background"));
		c.setFont(UIManager.getFont("Tree.font"));
		c.setBorder(BorderFactory.createEmptyBorder(3, 3, 4, 2));
	}
	
	public void uninstallUI(JComponent c) {
		
	}
	
	private int computeDepth(PropertyModel model) {
		int result = 0;
		Object parent = model.getParent();
		while (parent instanceof PropertyModel) {
			result++;
			parent = ((PropertyModel) parent).getParent();
		}
		return result;
	}
	
	public void addComponent(JInfoTable c, JComponent child) {
		String kind = child.getClientProperty(JInfoTable.CLIENT_PROPERTY_KIND).toString();
		GridBagConstraints con = new GridBagConstraints();
		con.insets.top = 1;
		con.insets.left = 1;
		con.insets.right = 1;
		if (kind.equals(JInfoTable.KIND_LABEL)) {
			PropertyModel model = (PropertyModel) child.getClientProperty(JInfoTable.CLIENT_PROPERTY_MODEL);
			int y = c.getVisibleIndex(model);
			con.fill = GridBagConstraints.HORIZONTAL;
			con.anchor = GridBagConstraints.LINE_START;
			con.weightx = 0.1;
			con.gridy = y;
			con.gridx = 0;
			con.insets.left += computeDepth(model) * DEPTH_SIZE;
		} else if (kind.equals(JInfoTable.KIND_VALUE)) {
			PropertyModel model = (PropertyModel) child.getClientProperty(JInfoTable.CLIENT_PROPERTY_MODEL);
			int y = c.getVisibleIndex(model);
			con.fill = GridBagConstraints.HORIZONTAL;
			con.weightx = 1.0;
			con.gridy = y;
			con.gridx = 1;
		} else if (kind.equals(JInfoTable.KIND_FILLER)) {
			con.gridx = 0;
			con.gridy = c.getVisibleIndexCount();
			con.gridwidth = 2;
			con.weightx = 1.0;
			con.weighty = 1.0;
		} else throw new Error();
		c.add(child, con);
	}
	
}
