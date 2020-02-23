package com.aexiz.daviz.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.IdentityHashMap;

public class DefaultObjectSelectionModel implements ObjectSelectionModel {

    private static Object SENTINEL = new Object();
    protected transient ChangeEvent changeEvent;
    private EventListenerList listenerList = new EventListenerList();
    private boolean multiSelection = true;
    private IdentityHashMap<Object, Object> set = new IdentityHashMap<Object, Object>();

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

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

    public boolean isMultiSelection() {
        return multiSelection;
    }

    public void setMultiSelection(boolean multi) {
        throw new Error("Not implemented yed");
    }

    public void refreshSelection() {
        fireStateChanged();
    }

}
