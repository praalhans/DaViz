package com.aexiz.daviz.ui;

import java.util.IdentityHashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultObjectSelectionModel implements ObjectSelectionModel {
	
	private EventListenerList listenerList = new EventListenerList();
	
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	
	private boolean multiSelection = true;
	
	protected transient ChangeEvent changeEvent;
	
	protected void fireStateChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((ChangeListener) listeners[i+1]).stateChanged(changeEvent);
			}
		}
	}
	
	private IdentityHashMap<Object,Object> set = new IdentityHashMap<Object,Object>();
	private static Object SENTINEL = new Object();

	public void clearSelection() {
		set.clear();
		fireStateChanged();
	}

	public boolean isSelected(Object o) {
		return set.containsKey(o);
	}

	public boolean isSelectionEmpty() {
		return set.isEmpty();
	}

	public boolean addSelection(Object o) {
		if (!multiSelection && set.size() == 1)
			return false;
		boolean result = set.put(o, SENTINEL) == null;
		if (result) {
			fireStateChanged();
		}
		return result;
	}

	public void removeSelection(Object o) {
		if (set.remove(o) != null) {
			fireStateChanged();
		}
	}

	public Object[] getSelection() {
		return set.keySet().toArray(new Object[0]);
	}
	
	public void setMultiSelection(boolean multi) {
		throw new Error("Not implemented yed");
	}
	
	public boolean isMultiSelection() {
		return multiSelection;
	}

	public void refreshSelection() {
		fireStateChanged();
	}
	
}
