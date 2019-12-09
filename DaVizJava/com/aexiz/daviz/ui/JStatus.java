package com.aexiz.daviz.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.aexiz.daviz.images.ImageRoot;

public class JStatus extends JComponent {

	private static final long serialVersionUID = 5290034646898044837L;
	
	private static final int TEMPORARY_EXPIRE_TIME = 2000; // 2 seconds
	
	ImageIcon infoIcon;
	ImageIcon warningIcon;
	
	JLabel label;
	String defaultStatus;
	String temporaryStatus;
	
	Timer timer;
	
	public JStatus() {
		infoIcon = new ImageIcon(ImageRoot.class.getResource("d32/lightbulb_off.png"));
		warningIcon = new ImageIcon(ImageRoot.class.getResource("d32/lightbulb.png"));
		label = new JLabel();
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		label.setBackground(Color.WHITE);
		label.setOpaque(true);
		setLayout(new BorderLayout());
		add(label, BorderLayout.CENTER);
		timer = new Timer(TEMPORARY_EXPIRE_TIME, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTemporaryStatus(null);
				timer.stop();
			}
		});
		timer.setRepeats(false);
	}
	
	private void updateLabel() {
		if (temporaryStatus == null) {
			label.setIcon(infoIcon);
			label.setText(defaultStatus);
			label.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		} else {
			label.setIcon(warningIcon);
			label.setText(temporaryStatus);
			label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
	}
	
	public void setDefaultStatus(String message) {
		defaultStatus = message;
		updateLabel();
	}
	
	public String getDefaultStatus() {
		return defaultStatus;
	}
	
	public void setTemporaryStatus(String message) {
		temporaryStatus = message;
		updateLabel();
		timer.stop();
		if (message != null) {
			timer.start();
		}
	}
	
	public String getTemporaryStatus() {
		return temporaryStatus;
	}
	
	public static void setTemporaryStatus(Component comp, String message) {
		// Traverse all back to root pane
		Container root = SwingUtilities.getRootPane(comp);
		// Perform depth first search for outer-most left-most JStatus
		JStatus status = dfs(root);
		if (status != null) {
			status.setTemporaryStatus(message);
		}
	}
	
	private static JStatus dfs(Container root) {
		for (int i = 0, s = root.getComponentCount(); i < s; i++) {
			Component c = root.getComponent(i);
			if (c instanceof JStatus) {
				return (JStatus) c;
			}
			if (c instanceof Container) {
				JStatus result = dfs((Container) c);
				if (result != null)
					return result;
			}
		}
		return null;
	}
	
}
