package com.aexiz.daviz.ui;

import javax.swing.event.ChangeListener;

public interface ObjectSelectionModel {
	
	public void clearSelection();
	
	public boolean isSelected(Object o);
	
	public boolean isSelectionEmpty();
	
	public boolean addSelection(Object o);
	
	public void removeSelection(Object o);
	
	public Object[] getSelection();
	
	public void addChangeListener(ChangeListener l);
	
	public void removeChangeListener(ChangeListener l);
	
}
