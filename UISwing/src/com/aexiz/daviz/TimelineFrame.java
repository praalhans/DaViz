package com.aexiz.daviz;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import com.aexiz.daviz.ui.ExecutionModel.ReorderEvent;
import com.aexiz.daviz.ui.ExecutionModel.ReorderEventListener;
import com.aexiz.daviz.images.ImageRoot;
import com.aexiz.daviz.ui.JCoolBar;
import com.aexiz.daviz.ui.JStatus;
import com.aexiz.daviz.ui.JTimeline;

class TimelineFrame extends JDialog {

	private static final long serialVersionUID = -3706031677602330641L;
	
	ChoiceFrame choice;
	InfoFrame info;
	
	JCoolBar toolbar;
	JTimeline timeline;
	JStatus status;

	public TimelineFrame(Window owner) {
		super(owner, "Timeline");
		setAutoRequestFocus(false);
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon(ImageRoot.class.getResource("d16/node.png")).getImage());
		icons.add(new ImageIcon(ImageRoot.class.getResource("d32/node.png")).getImage());
		setIconImages(icons);
		
		JPanel topPane = new JPanel(new BorderLayout());
		toolbar = new JCoolBar();
		topPane.add(toolbar, BorderLayout.CENTER);
		topPane.add(new JSeparator(), BorderLayout.PAGE_END);
		
		timeline = new JTimeline();
		timeline.addReorderEventListener(new ReorderEventListener() {
			public void reorderStarted(ReorderEvent e) {
				status.setTemporaryStatus("Drag the events over each other to reorder them, release to commit.");
			}
			public void reorderUpdating(ReorderEvent e) {
			}
			public void reorderEnded(ReorderEvent e) {
				status.setTemporaryStatus(null);
			}
		});
		JScrollPane scrollPane = new JScrollPane(timeline);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel bottomPane = new JPanel(new BorderLayout());
		status = new JStatus();
		status.setDefaultStatus("Drag the ruler or use the left and right arrows.");
		bottomPane.add(new JSeparator(), BorderLayout.PAGE_START);
		bottomPane.add(status, BorderLayout.CENTER);
		
		Container contentPane = getContentPane();
		contentPane.add(topPane, BorderLayout.PAGE_START);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(bottomPane, BorderLayout.PAGE_END);
	}
	
	// Called by Controller to create actions
	void registerActions(Controller controller) {
		controller.registerAction("timeline-mode-select", new AbstractAction() {
			private static final long serialVersionUID = -9066116899817193241L;
			{
				putValue(Action.NAME, "Selection");
				putValue(Action.SELECTED_KEY, true);
				putValue(Action.SHORT_DESCRIPTION, "Selection");
				putValue(Action.LONG_DESCRIPTION, "Drag the ruler or use the left and right arrows.");
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/draw_smudge.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/draw_smudge.png")));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
			}
			public void actionPerformed(ActionEvent e) {
				timeline.requestFocusInWindow();
			}
		});
		controller.registerAction("timeline-mode-concurrent", new AbstractAction() {
			private static final long serialVersionUID = -9066116899817193241L;
			{
				putValue(Action.NAME, "Concurrent");
				putValue(Action.SELECTED_KEY, false);
				putValue(Action.SHORT_DESCRIPTION, "Move events concurrently in time");
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/arrow_divide.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/arrow_divide.png")));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
			}
			public void actionPerformed(ActionEvent e) {
				choice.setVisible(false);
				info.setVisible(false);
				timeline.requestFocusInWindow();
				timeline.setEditMode(JTimeline.MODE_SWAP);
				controller.simulationManager.setLinear(false);
			}
		});
		controller.registerAction("timeline-mode-linear", new AbstractAction() {
			private static final long serialVersionUID = -9066116899817193241L;
			{
				putValue(Action.NAME, "Linear");
				putValue(Action.SELECTED_KEY, true);
				putValue(Action.SHORT_DESCRIPTION, "Choose a linear order");
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/arrow_rrr.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/arrow_rrr.png")));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
			}
			public void actionPerformed(ActionEvent e) {
				choice.setVisible(true);
				info.setVisible(true);
				timeline.requestFocusInWindow();
				timeline.setEditMode(JTimeline.MODE_SELECTION);
				controller.simulationManager.setLinear(true);
			}
		});
		controller.registerAction("timeline-show-classes", new AbstractAction() {
			private static final long serialVersionUID = -9066116899817193241L;
			{
				putValue(Action.NAME, "Concurrency classes");
				putValue(Action.SELECTED_KEY, false);
				putValue(Action.SHORT_DESCRIPTION, "Show concurrency classes");
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/categories.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/categories.png")));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
				setEnabled(false); // TODO
			}
			public void actionPerformed(ActionEvent e) {
				timeline.requestFocusInWindow();
			}
		});
		controller.registerAction("timeline-next", new AbstractAction() {
			private static final long serialVersionUID = -9066116899817193241L;
			{
				putValue(Action.NAME, "Next");
				putValue(Action.SHORT_DESCRIPTION, "Next time unit");
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/arrow_right.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/arrow_right.png")));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
			}
			public void actionPerformed(ActionEvent e) {
				timeline.setCurrentTime(timeline.getCurrentTime() + 1.0f);
				timeline.requestFocusInWindow();
			}
		});
		controller.registerAction("timeline-previous", new AbstractAction() {
			private static final long serialVersionUID = -9066116899817193241L;
			{
				putValue(Action.NAME, "Previous");
				putValue(Action.SHORT_DESCRIPTION, "Previous time unit");
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/arrow_left.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/arrow_left.png")));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
			}
			public void actionPerformed(ActionEvent e) {
				timeline.setCurrentTime(timeline.getCurrentTime() - 1.0f);
				timeline.requestFocusInWindow();
			}
		});
	}
	
	// Called  by Controller to refresh registered actions
	void refreshActions(Controller controller) {
		boolean selected = (Boolean) controller.getAction("timeline-mode-linear").getValue(Action.SELECTED_KEY);
		choice.setVisible(selected);
	}
	
	// Called by Controller to change menubar
	void populateMenuBar(Controller controller, JMenuBar menubar) {
		AbstractButton tb;
		ButtonGroup tgrp = new ButtonGroup();
		tb = new JToggleButton(controller.getAction("timeline-mode-select"));
		tb.setHideActionText(true);
		tgrp.add(tb);
		toolbar.add(tb);
		toolbar.addSeparator();
		tgrp = new ButtonGroup();
		tb = new JToggleButton(controller.getAction("timeline-mode-concurrent"));
		tb.setHideActionText(true);
		tgrp.add(tb);
		toolbar.add(tb);
		tb = new JToggleButton(controller.getAction("timeline-mode-linear"));
		tb.setHideActionText(true);
		tgrp.add(tb);
		toolbar.add(tb);
		toolbar.addSeparator();
		tgrp = new ButtonGroup();
		tb = new JToggleButton(controller.getAction("timeline-show-classes"));
		tb.setHideActionText(true);
		tgrp.add(tb);
		toolbar.add(tb);
		toolbar.addHorizontalGlue();
		tb = new JButton(controller.getAction("timeline-previous"));
		tb.setHideActionText(true);
		toolbar.add(tb);
		tb = new JButton(controller.getAction("timeline-next"));
		tb.setHideActionText(true);
		toolbar.add(tb);
	}

}
