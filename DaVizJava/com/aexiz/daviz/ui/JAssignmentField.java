package com.aexiz.daviz.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.aexiz.daviz.images.ImageRoot;

// Assignment field either pushes or pulls some object to/from selection.
public class JAssignmentField extends JPanel {
	
	private static final long serialVersionUID = -2150341137917631106L;
	
	private int height;
	
	protected CustomDocument customDocument;
	protected JTextField field;
	protected JButton picker;
	
	protected ObjectSelectionModel selectionModel;
	protected Object[] value;
	
	public JAssignmentField() {
		super(null);
		Handler h = new Handler();
		field = new JTextField();
		height = field.getPreferredSize().height;
		field.setPreferredSize(new Dimension(105, height));
		field.setBackground(Color.WHITE);
		customDocument = new CustomDocument();
		field.setDocument(customDocument);
		picker = new JButton(new ImageIcon(ImageRoot.class.getResource("d16/hand_property.png")));
		picker.setToolTipText("Assign from selection");
		picker.setPreferredSize(new Dimension(25, height));
		picker.setFocusPainted(false);
		picker.setOpaque(false);
		picker.addActionListener(h);
		
		add(field, BorderLayout.CENTER);
		add(picker, BorderLayout.LINE_END);
		field.setBounds(0, 0, 105, height);
		int shiftleft = 0;
		picker.setBounds(105 - shiftleft, -1, 25 + shiftleft, height + 2);
	}
	
	protected void updateField() {
		String text = "";
		if (value != null) {
			for (int i = 0; i < value.length; i++) {
				if (i > 0) text += ", ";
				text += value[i].toString();
			}
		}
		if (isEnabled()) {
			customDocument.locked = false;
			field.setText(text);
			customDocument.locked = true;
		}
	}
	
	protected void filterValue() {
	}
	
	public void replayValue() {
		filterValue();
		if (value.length == 0) { getToolkit().beep(); }
		updateField();
	}
	
	public void setValue() {
		if (selectionModel == null) value = null;
		else value = selectionModel.getSelection();
		replayValue();
	}
	
	public void clearValue() {
		value = null;
		updateField();
	}
	
	public Object[] getValue() {
		return value;
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		field.setEnabled(enabled);
		picker.setEnabled(enabled);
		if (!enabled) {
			customDocument.locked = false;
			field.setText("");
			customDocument.locked = true;
		} else {
			updateField();
		}
	}
	
	public void setSelectionModel(ObjectSelectionModel selectionModel) {
		ObjectSelectionModel old = this.selectionModel;
		if (old != null) clearValue();
		this.selectionModel = selectionModel;
		if (selectionModel != old) firePropertyChange("selectionModel", old, selectionModel);
	}
	
	public ObjectSelectionModel getSelectionModel() {
		return selectionModel;
	}
	
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
	
	public Dimension getMinimumSize() {
		return new Dimension(130, height);
	}
	
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	class Handler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			setValue();
			field.requestFocusInWindow();
		}
		
	}
	
	static class CustomDocument extends PlainDocument {
		
		private static final long serialVersionUID = 647932359351911969L;
		
		boolean locked = true;
		
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (locked) return;
			super.insertString(offs, str, a);
		}
		
		public void remove(int offs, int len) throws BadLocationException {
			if (locked) return;
			super.remove(offs, len);
		}
		
		public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			if (locked) return;
			super.replace(offset, length, text, attrs);
		}
		
	}
	
}
