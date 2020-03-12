package com.aexiz.daviz.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import com.aexiz.daviz.images.ImageRoot;
import com.aexiz.daviz.ui.swing.JCoolBar;
import com.aexiz.daviz.ui.swing.JGraph;
import com.aexiz.daviz.ui.swing.JStatus;

class NetworkFrame extends JDialog {

	private static final long serialVersionUID = -3706031677602330641L;
	
	JCoolBar toolbar;
	JGraph graph;
	JStatus status;
	
	NetworkFrame(Window owner) {
		super(owner, "Network");
		setAutoRequestFocus(false);
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon(ImageRoot.class.getResource("d16/molecule.png")).getImage());
		icons.add(new ImageIcon(ImageRoot.class.getResource("d32/molecule.png")).getImage());
		setIconImages(icons);
		
		JPanel topPane = new JPanel(new BorderLayout());
		toolbar = new JCoolBar();
		topPane.add(toolbar, BorderLayout.CENTER);
		
		graph = new JGraph();
		JScrollPane scrollPane = new JScrollPane(graph);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel bottomPane = new JPanel(new BorderLayout());
		status = new JStatus();
		bottomPane.add(new JSeparator(), BorderLayout.PAGE_START);
		bottomPane.add(status, BorderLayout.CENTER);
		
		Container contentPane = getContentPane();
		contentPane.add(topPane, BorderLayout.PAGE_START);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(bottomPane, BorderLayout.PAGE_END);
	}
	
	// Called by Controller to create actions
	void registerActions(Controller controller) {
		controller.registerAction("network-mode-select", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Select");
				putValue(Action.SELECTED_KEY, true);
				putValue(Action.SHORT_DESCRIPTION, "Object selection and movement");
				putValue(Action.LONG_DESCRIPTION, "Drag processes around or select processes and channels.");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/draw_smudge.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/draw_smudge.png")));
			}
			public void actionPerformed(ActionEvent e) {
				graph.switchToSelectionMode();
				graph.requestFocusInWindow();
				status.setDefaultStatus(getValue(Action.LONG_DESCRIPTION).toString());
			}
		});
		controller.getAction("network-mode-select").actionPerformed(null);
		controller.registerAction("network-mode-create-node", new AbstractAction() {
			private static final long serialVersionUID = 7169285620597064570L;
			{
				putValue(Action.NAME, "Create process");
				putValue(Action.SELECTED_KEY, false);
				putValue(Action.SHORT_DESCRIPTION, "Create processes");
				putValue(Action.LONG_DESCRIPTION, "Add a process by clicking on an empty space.");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/draw_points.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/draw_points.png")));
			}
			public void actionPerformed(ActionEvent e) {
				graph.switchToVertexMode();
				graph.clearSelection();
				graph.requestFocusInWindow();
				status.setDefaultStatus(getValue(Action.LONG_DESCRIPTION).toString());
			}
		});
		controller.registerAction("network-mode-create-edge", new AbstractAction() {
			private static final long serialVersionUID = 5026362295254309891L;
			{
				putValue(Action.NAME, "Create channel");
				putValue(Action.SELECTED_KEY, false);
				putValue(Action.SHORT_DESCRIPTION, "Create channels");
				putValue(Action.LONG_DESCRIPTION, "Add a channel by start dragging from a process.");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/draw_vertex.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/draw_vertex.png")));
			}
			public void actionPerformed(ActionEvent e) {
				graph.switchToEdgeMode();
				graph.clearSelection();
				graph.requestFocusInWindow();
				status.setDefaultStatus(getValue(Action.LONG_DESCRIPTION).toString());
			}
		});
		controller.registerAction("network-mode-erase", new AbstractAction() {
			private static final long serialVersionUID = 5026362295254309891L;
			{
				putValue(Action.NAME, "Erase objects");
				putValue(Action.SELECTED_KEY, false);
				putValue(Action.SHORT_DESCRIPTION, "Erase processes and channels");
				putValue(Action.LONG_DESCRIPTION, "Click or drag to remove processes and channels.");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/draw_eraser.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/draw_eraser.png")));
			}
			public void actionPerformed(ActionEvent e) {
				graph.switchToEraseMode();
				graph.clearSelection();
				graph.requestFocusInWindow();
				status.setDefaultStatus(getValue(Action.LONG_DESCRIPTION).toString());
			}
		});
		controller.registerAction("network-select-all", new AbstractAction() {
			private static final long serialVersionUID = -9066116899817193241L;
			{
				putValue(Action.NAME, "Select all");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
			}
			public void actionPerformed(ActionEvent e) {
				graph.selectAll();
				graph.requestFocusInWindow();
			}
		});
		controller.registerAction("network-remove-selected", new AbstractAction() {
			private static final long serialVersionUID = -5198464866688780148L;
			{
				putValue(Action.NAME, "Remove");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
			}
			public void actionPerformed(ActionEvent e) {
				graph.removeSelection();
				graph.requestFocusInWindow();
			}
		});
		controller.registerAction("network-zoom-in", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Zoom in");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK));
				putValue(Action.SHORT_DESCRIPTION, "Zoom in");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/zoom_in.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/zoom_in.png")));
			}
			public void actionPerformed(ActionEvent e) {
				graph.zoomIn();
				graph.requestFocusInWindow();
			}
		});
		controller.registerAction("network-zoom-out", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Zoom out");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
				putValue(Action.SHORT_DESCRIPTION, "Zoom out");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/zoom_out.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/zoom_out.png")));
			}
			public void actionPerformed(ActionEvent e) {
				graph.zoomOut();
				graph.requestFocusInWindow();
			}
		});
		controller.registerAction("network-show-grid", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Grid & origin");
				putValue(Action.SELECTED_KEY, false);
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
			}
			public void actionPerformed(ActionEvent e) {
				boolean sel = (Boolean) getValue(Action.SELECTED_KEY);
				graph.setShowGrid(sel);
			}
		});
	}
	
	// Called  by Controller to refresh registered actions
	void refreshActions(Controller controller) {
		if (controller.isSimulationLoaded()) {
			controller.getAction("network-mode-create-node").setEnabled(false);
			controller.getAction("network-mode-create-edge").setEnabled(false);
			controller.getAction("network-mode-erase").setEnabled(false);
			controller.getAction("network-remove-selected").setEnabled(false);
			if (graph.getEditMode() != JGraph.MODE_SELECTION) {
				controller.getAction("network-mode-select").actionPerformed(null);
			}
			graph.setReadOnly(true);
		} else {
			controller.getAction("network-mode-create-node").setEnabled(true);
			controller.getAction("network-mode-create-edge").setEnabled(true);
			controller.getAction("network-mode-erase").setEnabled(true);
			controller.getAction("network-remove-selected").setEnabled(true);
			graph.setReadOnly(false);
		}
	}
	
	// Called by Controller to change menubar
	void populateMenuBar(Controller controller, JMenuBar menubar) {
		JMenu menu = new JMenu("Edit");
		menu.setMnemonic('e');
		ButtonGroup mgrp = new ButtonGroup();
		ButtonGroup tgrp = new ButtonGroup();
		JMenuItem mb;
		AbstractButton tb;
		mb = new JRadioButtonMenuItem(controller.getAction("network-mode-select"));
		mb.setToolTipText(null);
		tb = new JToggleButton(controller.getAction("network-mode-select"));
		tb.setHideActionText(true);
		tgrp.add(tb);
		toolbar.add(tb);
		mgrp.add(mb);
		menu.add(mb);
		mb = new JRadioButtonMenuItem(controller.getAction("network-mode-create-node"));
		mb.setToolTipText(null);
		tb = new JToggleButton(controller.getAction("network-mode-create-node"));
		tb.setHideActionText(true);
		tgrp.add(tb);
		toolbar.add(tb);
		mgrp.add(mb);
		menu.add(mb);
		mb = new JRadioButtonMenuItem(controller.getAction("network-mode-create-edge"));
		mb.setToolTipText(null);
		tb = new JToggleButton(controller.getAction("network-mode-create-edge"));
		tb.setHideActionText(true);
		tgrp.add(tb);
		toolbar.add(tb);
		mgrp.add(mb);
		menu.add(mb);
		mb = new JRadioButtonMenuItem(controller.getAction("network-mode-erase"));
		mb.setToolTipText(null);
		tb = new JToggleButton(controller.getAction("network-mode-erase"));
		tb.setHideActionText(true);
		tgrp.add(tb);
		toolbar.add(tb);
		mgrp.add(mb);
		menu.add(mb);
		menu.addSeparator();
		menu.add(new JMenuItem(controller.getAction("network-select-all")));
		menu.add(new JMenuItem(controller.getAction("network-remove-selected")));
		menubar.add(menu, 0);
		toolbar.addHorizontalGlue();
		menu = new JMenu("View");
		menu.setMnemonic('v');
		mb = new JMenuItem(controller.getAction("network-zoom-in"));
		mb.setToolTipText(null);
		tb = new JButton(controller.getAction("network-zoom-in"));
		tb.setHideActionText(true);
		toolbar.add(tb);
		menu.add(mb);
		mb = new JMenuItem(controller.getAction("network-zoom-out"));
		mb.setToolTipText(null);
		tb = new JButton(controller.getAction("network-zoom-out"));
		tb.setHideActionText(true);
		toolbar.add(tb);
		menu.add(mb);
		menu.addSeparator();
		mb = new JCheckBoxMenuItem(controller.getAction("network-show-grid"));
		mb.setToolTipText(null);
		menu.add(mb);
		menubar.add(menu, 1);
	}

}
