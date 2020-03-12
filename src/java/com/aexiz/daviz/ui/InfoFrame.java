package com.aexiz.daviz.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

import com.aexiz.daviz.images.ImageRoot;
import com.aexiz.daviz.ui.swing.JInfoTable;

class InfoFrame extends JDialog {

	private static final long serialVersionUID = -4023604933733179384L;
	
	JInfoTable table;
	
	public InfoFrame(Window owner) {
		super(owner, "Information");
		setAutoRequestFocus(false);
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon(ImageRoot.class.getResource("d16/preferences.png")).getImage());
		icons.add(new ImageIcon(ImageRoot.class.getResource("d32/preferences.png")).getImage());
		setIconImages(icons);
		setType(Window.Type.UTILITY);
		
		table = new JInfoTable();
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		Container contentPane = getContentPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}
	
	// Called by Controller to create actions
	void registerActions(Controller controller) {
	}
	
	// Called  by Controller to refresh registered actions
	void refreshActions(Controller controller) {
	}
	
	void populateMenuBar(Controller controller, JMenuBar menubar) {
		menubar.setPreferredSize(new Dimension(0, 0));
		menubar.setFocusable(false);
	}
	
}
