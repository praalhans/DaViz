package com.aexiz.daviz.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import com.aexiz.daviz.ui.ExecutionModel.DecideEventType;
import com.aexiz.daviz.ui.ExecutionModel.EventModel;
import com.aexiz.daviz.ui.ExecutionModel.InternalEventType;
import com.aexiz.daviz.ui.ExecutionModel.MessageModel;
import com.aexiz.daviz.ui.ExecutionModel.PendingMessageModel;
import com.aexiz.daviz.ui.ExecutionModel.ReceiveEventType;
import com.aexiz.daviz.ui.ExecutionModel.ReorderEvent;
import com.aexiz.daviz.ui.ExecutionModel.ReorderEventListener;
import com.aexiz.daviz.ui.ExecutionModel.SendEventType;
import com.aexiz.daviz.ui.ExecutionModel.TerminateEventType;
import com.aexiz.daviz.ui.ExecutionModel.TimeEventListener;
import com.aexiz.daviz.ui.plaf.TimelineUI;
import com.aexiz.daviz.ui.plaf.basic.BasicTimelineUI;

/**
 * A timeline visually represents a particular execution model.
 */
public class JTimeline extends JComponent {

	private static final long serialVersionUID = 1947693107247971077L;
	
	static final String UICLASSID = "TimelineUI";
	
	protected ExecutionModel model;
	protected ObjectSelectionModel selectionModel;
	
	private Handler handler;
	
	protected ChangeListener changeListener;
	protected TimeEventListener timeEventListener;
	protected ReorderEventListener reorderEventListener;
	
	protected transient ChangeEvent changeEvent;
	
	static {
		UIDefaults def = UIManager.getDefaults();
		if (def.get(UICLASSID) == null)
			def.put(UICLASSID, BasicTimelineUI.class.getName());
	}
	
	public JTimeline() {
		setOpaque(true);
		setFocusable(true);
		setModel(new DefaultExecutionModel());
		setSelectionModel(new DefaultObjectSelectionModel());
		updateUI();
	}
	
	private Color alternateBackground;
	
	public void setAlternateBackground(Color c) {
		Color old = alternateBackground;
		alternateBackground = c;
		firePropertyChange("alternateBackground", old, c);
		repaint();
	}
	
	public Color getAlternateBackground() {
		return alternateBackground;
	}
	
	private Color innerBackground;
	
	public void setInnerBackground(Color c) {
		Color old = innerBackground;
		innerBackground = c;
		firePropertyChange("innerBackground", old, c);
		repaint();
	}
	
	public Color getInnerBackground() {
		return innerBackground;
	}
	
	private Border innerBorder;
	
	public void setInnerBorder(Border b) {
		Border old = innerBorder;
		innerBorder = b;
		firePropertyChange("innerBorder", old, b);
		repaint();
	}
	
	public Border getInnerBorder() {
		return innerBorder;
	}
	
	public Insets getInnerInsets() {
		if (innerBorder != null)
			return innerBorder.getBorderInsets(this);
		return new Insets(0, 0, 0, 0);
	}
	
	public Insets getInnerInsets(Insets i) {
		if (i == null)
			return getInnerInsets();
		if (innerBorder instanceof AbstractBorder)
			return ((AbstractBorder) innerBorder).getBorderInsets(this, i);
		if (innerBorder != null)
			return getInnerInsets();
		i.top = i.left = i.right = i.bottom = 0;
		return i;
	}
	
	public static class JEvent extends JKnob {

		private static final long serialVersionUID = 1985826609656196598L;
		
		private EventModel model;
		
		private ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireStateChanged();
				revalidate();
			}
		};
		
		protected JEvent() {
		}
		
		public void setModel(EventModel model) {
			EventModel oldModel = this.model;
			if (oldModel != null) {
				oldModel.removeChangeListener(changeListener);
			}
			this.model = model;
			if (model != null) {
				model.addChangeListener(changeListener);
			}
			firePropertyChange("model", oldModel, model);
		}
		
		public EventModel getModel() {
			return model;
		}
		
		public JTimeline getTimeline() {
			Component c = getParent();
			if (c instanceof JTimeline)
				return (JTimeline) c;
			return null;
		}
		
		public void setRollover(boolean b) {
			if (model == null) return;
			model.setRollover(b);
		}
		
		public boolean isRollover() {
			if (model == null) return false;
			return model.isRollover();
		}
		
		public void setPressed(boolean b) {
			if (model == null || !model.isLeader()) return;
			boolean old = model.isPressed();
			model.setPressed(b);
			if (!(old && !b)) return;
			model.validate();
		}
		
		public boolean isPressed() {
			if (model == null) return false;
			return model.isPressed();
		}
		
		public boolean isTerminateEvent() {
			return model != null && model.getEventType() instanceof TerminateEventType;
		}
		
		public boolean isDecideEvent() {
			return model != null && model.getEventType() instanceof DecideEventType;
		}
		
		public boolean isInternalEvent() {
			return model != null && model.getEventType() instanceof InternalEventType;
		}
		
		public boolean isSendEvent() {
			return model != null && model.getEventType() instanceof SendEventType;
		}
		
		public boolean isReceiveEvent() {
			return model != null && model.getEventType() instanceof ReceiveEventType;
		}
		
		public void setDelta(float delta) {
			if (model == null || !model.isLeader()) return;
			model.setDelta(delta);
		}
		
		public int getProcessIndex() {
			if (model == null) return 0;
			return model.getProcessIndex();
		}
		
		public float getTime() {
			if (model == null) return 0.0f;
			return model.getTime();
		}
		
		public float getAscention() {
			if (model == null) return 0.0f;
			return model.getAscention();
		}
		
		public boolean isSelected() {
			JTimeline g = getTimeline();
			if (g == null)
				return false;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				return selModel.isSelected(model);
			} else {
				return false;
			}
		}
		
		public void setSelected(boolean sel) {
			if (isSelected() == sel)
				return;
			JTimeline g = getTimeline();
			if (g == null)
				return;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				if (sel) {
					selModel.addSelection(model);
				} else {
					selModel.removeSelection(model);
				}
			}
		}
		
		public void requestSingleSelected() {
			JTimeline g = getTimeline();
			if (g == null)
				return;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				selModel.clearSelection();
				selModel.addSelection(model);
			}
		}
		
		public void requestClearSelection() {
			JTimeline g = getTimeline();
			if (g == null)
				return;
			g.clearSelection();
		}
		
		public void requestAllSelected() {
			JTimeline g = getTimeline();
			if (g == null)
				return;
			g.selectAllEvents();
		}
		
		// Utilities for scrolling
		
		public JViewport getViewport() {
			JViewport viewport;
			Container p = getParent();
			while (p != null && !(p instanceof JViewport)) p = p.getParent();
			viewport = (JViewport) p;
			return viewport;
		}
		
		public Point scrollRectToVisibleWithEffect(Rectangle rect) {
			// A simple utility for UIs to find out the effect in pixels of scrolling,
			// to adjust the tracking of the mouse
			JViewport viewport = getViewport();
			if (viewport == null) return new Point(0, 0); // No effect
			Point old = viewport.getViewPosition();
			super.scrollRectToVisible(rect);
			Point next = viewport.getViewPosition();
			return new Point(old.x - next.x, old.y - next.y);
		}
		
	}
	
	public static class JMessage extends JKnob {

		private static final long serialVersionUID = -1075183018340778712L;
		
		public static final int DIR_NORTH_EAST = 1;
		public static final int DIR_SOUTH_EAST = 2;
		public static final int DIR_SOUTH_WEST = 3;
		public static final int DIR_NORTH_WEST = 4;
		
		// Either MessageModel or PendingMessageModel
		protected Object model;
		
		protected int direction;
		
		protected JMessage() {
		}
		
		public void setModel(MessageModel model) {
			Object oldModel = this.model;
			this.model = model;
			firePropertyChange("model", oldModel, model);
		}
		
		public void setModel(PendingMessageModel model) {
			Object oldModel = this.model;
			this.model = model;
			firePropertyChange("model", oldModel, model);
		}
		
		public Object getModel() {
			return model;
		}
		
		public MessageModel getMessageModel() {
			return (MessageModel) model;
		}
		
		public PendingMessageModel getPendingMessageModel() {
			return (PendingMessageModel) model;
		}
		
		public JTimeline getTimeline() {
			Component c = getParent();
			if (c instanceof JTimeline)
				return (JTimeline) c;
			return null;
		}
		
		private Color errorColor;
		
		public void setErrorColor(Color color) {
			Color old = errorColor;
			errorColor = color;
			firePropertyChange("errorColor", old, color);
			repaint();
		}
		
		public Color getErrorColor() {
			return errorColor;
		}
		
		private EventModel modelGetFrom() {
			if (model instanceof MessageModel) {
				return ((MessageModel) model).getFrom();
			} else if (model instanceof PendingMessageModel) {
				return ((PendingMessageModel) model).getFrom();
			} else {
				return null;
			}
		}
		
		public JEvent getFromEvent() {
			JTimeline t = getTimeline();
			EventModel from = modelGetFrom();
			if (t != null && from != null) {
				return t.findEventComponent(from);
			} else {
				return null;
			}
		}
		
		public Integer getFromProcessIndex() {
			EventModel from = modelGetFrom();
			if (from == null) return null;
			return from.getProcessIndex();
		}
		
		public JEvent getToEvent() {
			JTimeline t = getTimeline();
			if (t != null && model instanceof MessageModel) {
				return t.findEventComponent(((MessageModel) model).getTo());
			} else {
				return null;
			}
		}
		
		public Integer getToProcessIndex() {
			if (model instanceof MessageModel) {
				return ((MessageModel) model).getTo().getProcessIndex();
			} else if (model instanceof PendingMessageModel) {
				return ((PendingMessageModel) model).getTo();
			} else {
				return null;
			}
		}
		
		public boolean isConflicting() {
			if (model instanceof MessageModel) {
				return ((MessageModel) model).isConflicting();
			} else {
				return false;
			}
		}
		
		public boolean isPending() {
			return model instanceof PendingMessageModel;
		}
		
		public boolean isSelected() {
			JTimeline g = getTimeline();
			if (g == null)
				return false;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				return selModel.isSelected(model);
			} else {
				return false;
			}
		}
		
		public void setSelected(boolean sel) {
			if (isSelected() == sel)
				return;
			JTimeline g = getTimeline();
			if (g == null)
				return;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				if (sel) {
					selModel.addSelection(model);
				} else {
					selModel.removeSelection(model);
				}
			}
		}
		
		public void requestSingleSelected() {
			JTimeline g = getTimeline();
			if (g == null)
				return;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				selModel.clearSelection();
				selModel.addSelection(model);
			}
		}
		
		public void requestClearSelection() {
			JTimeline g = getTimeline();
			if (g == null)
				return;
			g.clearSelection();
		}
		
		public void requestAllSelected() {
			JTimeline g = getTimeline();
			if (g == null)
				return;
			g.selectAllMessages();
		}
		
		// Updated by L&F
		
		public void setDirection(int direction) {
			if (direction < 0 || direction > DIR_NORTH_WEST)
				throw new IllegalArgumentException();
			this.direction = direction;
		}
		
		public int getDirection() {
			return direction;
		}
		
	}
	
	public static class JTimeRuler extends JComponent {

		private static final long serialVersionUID = 7409778629836110204L;
		
		protected ExecutionModel model;
		protected float time;
		protected boolean pressed;
		
		private Handler handler;
		
		protected TimeEventListener timeEventListener;
		
		protected transient EventObject timeEvent;
		
		protected JTimeRuler() {
			setAutoscrolls(true);
		}
		
		public void addTimeEventListener(TimeEventListener listener) {
			listenerList.add(TimeEventListener.class, listener);
		}
		
		public void removeTimeEventListener(TimeEventListener listener) {
			listenerList.remove(TimeEventListener.class, listener);
		}
		
		protected void fireTimeChanged() {
			if (timeEvent == null)
				timeEvent = new EventObject(this);
			Object[] listeners = listenerList.getListenerList();
			for (int i = listeners.length - 2; i >= 0; i -= 2)
				if (listeners[i] == TimeEventListener.class)
					((TimeEventListener) listeners[i+1]).timeChanged(timeEvent);
		}
		
		public void setModel(ExecutionModel model) {
			ExecutionModel oldModel = this.model;
			if (oldModel != null) {
				oldModel.removeTimeEventListener(timeEventListener);
				timeEventListener = null;
			}
			this.model = model;
			if (model != null) {
				timeEventListener = createTimeEventListener();
				model.addTimeEventListener(timeEventListener);
			}
			firePropertyChange("model", oldModel, model);
			fireTimeChanged();
		}
		
		public ExecutionModel getModel() {
			return model;
		}
		
		public void setUI(ComponentUI ui) {
			super.setUI(ui);
		}
		
		public ComponentUI getUI() {
			return ui;
		}
		
		public void setPressed(boolean b) {
			pressed = b;
			if (!(pressed && model != null)) model.validateTime();
		}
		
		public boolean isPressed() {
			return pressed;
		}
		
		public void setDelta(float d) {
			if (model == null) return;
			model.setCurrentTimeDelta(d);
		}
		
		public JViewport getViewport() {
			JViewport viewport;
			Container p = getParent();
			while (p != null && !(p instanceof JViewport)) p = p.getParent();
			viewport = (JViewport) p;
			return viewport;
		}
		
		public Point scrollRectToVisibleWithEffect(Rectangle rect) {
			// A simple utility for UIs to find out the effect in pixels of scrolling,
			// to adjust the tracking of the mouse
			JViewport viewport = getViewport();
			if (viewport == null) return new Point(0, 0); // No effect
			Point old = viewport.getViewPosition();
			super.scrollRectToVisible(rect);
			Point next = viewport.getViewPosition();
			return new Point(old.x - next.x, old.y - next.y);
		}
		
		protected TimeEventListener createTimeEventListener() {
			return getHandler();
		}
		
		private Handler getHandler() {
			if (handler == null) {
				handler = new Handler();
			}
			return handler;
		}
		
		class Handler implements TimeEventListener {
			public void timeChanged(EventObject e) {
				fireTimeChanged();
			}
		}
		
	}
	
	public void setModel(ExecutionModel newModel) {
		ExecutionModel oldModel = getModel();
		removeSubComponents();
		if (oldModel != null) {
			oldModel.removeChangeListener(changeListener);
			changeListener = null;
			oldModel.removeTimeEventListener(timeEventListener);
			timeEventListener = null;
			oldModel.removeReorderEventListener(reorderEventListener);
			reorderEventListener = null;
		}
		model = newModel;
		if (newModel != null) {
			changeListener = createChangeListener();
			newModel.addChangeListener(changeListener);
			timeEventListener = createTimeEventListener();
			newModel.addTimeEventListener(timeEventListener);
			reorderEventListener = createReorderEventListener();
			newModel.addReorderEventListener(reorderEventListener);
		}
		firePropertyChange("model", oldModel, newModel);
		if (newModel != oldModel) {
			updateSubComponents();
		}
	}
	
	public ExecutionModel getModel() {
		return model;
	}
	
	public void setSelectionModel(ObjectSelectionModel selModel) {
		ObjectSelectionModel oldModel = getSelectionModel();
		if (oldModel != null) {
			oldModel.removeChangeListener(changeListener);
			changeListener = null;
		}
		selectionModel = selModel;
		if (selModel != null) {
			changeListener = createChangeListener();
			selModel.addChangeListener(changeListener);
		}
		firePropertyChange("selectionModel", oldModel, selModel);
		if (selModel != oldModel) {
			repaint();
		}
	}
	
	public ObjectSelectionModel getSelectionModel() {
		return selectionModel;
	}
	
	public void clearSelection() {
		if (selectionModel != null)
			selectionModel.clearSelection();
	}
	
	public void selectAllEvents() {
		if (selectionModel != null && model != null) {
			for (EventModel event : model.getValidEvent()) {
				selectionModel.addSelection(event);
			}
		}
	}
	
	public void selectAllMessages() {
		if (selectionModel != null && model != null) {
			for (MessageModel msg : model.getValidMessage()) {
				selectionModel.addSelection(msg);
			}
			for (PendingMessageModel msg : model.getValidPendingMessage()) {
				selectionModel.addSelection(msg);
			}
		}
	}
	
	public float getCurrentTime() {
		if (model == null) return 0.0f;
		return model.getCurrentTime();
	}
	
	public void setCurrentTime(float time) {
		if (model == null) return;
		model.setCurrentTime(time);
	}
	
	public void updateUI() {
		setUI((TimelineUI) UIManager.getUI(this));
	}
	
	public void setUI(TimelineUI ui) {
		removeSubComponents();
		super.setUI(ui);
		updateSubComponents();
	}
	
	public TimelineUI getUI() {
		return (TimelineUI) ui;
	}
	
	public String getUIClassID() {
		return UICLASSID;
	}
	
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
	
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	
	public ChangeListener[] getChangeListeners() {
		return listenerList.getListeners(ChangeListener.class);
	}
	
	protected void fireStateChanged() {
		if (changeEvent == null)
			changeEvent = new ChangeEvent(this);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == ChangeListener.class)
				((ChangeListener) listeners[i+1]).stateChanged(changeEvent);
	}
	
	public void addReorderEventListener(ReorderEventListener l) {
		listenerList.add(ReorderEventListener.class, l);
	}
	
	public void removeReorderEventListener(ReorderEventListener l) {
		listenerList.remove(ReorderEventListener.class, l);
	}
	
	protected void fireReorderStarted(ReorderEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == ReorderEventListener.class)
				((ReorderEventListener) listeners[i+1]).reorderStarted(e);
	}
	
	protected void fireReorderEnded(ReorderEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == ReorderEventListener.class)
				((ReorderEventListener) listeners[i+1]).reorderEnded(e);
	}
	
	protected void fireReorderUpdating(ReorderEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == ReorderEventListener.class)
				((ReorderEventListener) listeners[i+1]).reorderUpdating(e);
	}
	
	private JTimeRuler ruler;
	
	private void removeSubComponents() {
		int n = getComponentCount();
		for (int i = 0; i < n; i++) {
			Component c = getComponent(i);
			if (c instanceof JEvent) {
				((JEvent) c).setModel(null);
				remove(i--); n--;
			} else if (c instanceof JMessage) {
				((JMessage) c).setModel((MessageModel) null);
				remove(i--); n--;
			} else if (c instanceof JTimeRuler) {
				remove(i--); n--;
				ruler = null;
			}
		}
	}
	
	private void updateSubComponents() {
		removeSubComponents();
		ExecutionModel model = getModel();
		if (model == null) return;
		TimelineUI ui = getUI();
		for (EventModel event : model.getValidEvent()) {
			JEvent c = createEventComponent(event);
			if (ui != null)
				ui.configureEventComponent(c);
			add(c);
		}
		for (MessageModel message : model.getValidMessage()) {
			JMessage c = createMessageComponent(message);
			if (ui != null)
				ui.configureMessageComponent(c);
			add(c);
		}
		for (PendingMessageModel message : model.getValidPendingMessage()) {
			JMessage c = createMessageComponent(message);
			if (ui != null)
				ui.configureMessageComponent(c);
			add(c);
		}
		{
			JTimeRuler c = createTimeRulerComponent();
			c.setModel(model);
			if (ui != null)
				ui.configureTimeRulerComponent(c);
			add(c); // lowest z-order
			ruler = c;
		}
	}
	
	public JTimeRuler getRuler() {
		return ruler;
	}
	
	public static final int MODE_SELECTION = 0;
	public static final int MODE_SWAP = 1;
	
	public void setEditMode(int mode) {
		if (model == null)
			return;
		model.setEditMode(mode);
	}
	
	public int getEditMode() {
		if (model == null)
			return MODE_SELECTION;
		return model.getEditMode();
	}
	
	public JEvent findEventComponent(EventModel event) {
		int n = getComponentCount();
		for (int i = 0; i < n; i++) {
			Component c = getComponent(i);
			if (c instanceof JEvent) {
				JEvent e = (JEvent) c;
				if (e.model == event)
					return e;
			}
		}
		throw new IllegalArgumentException("Component not found for " + event);
	}
	
	public JMessage findMessageComponent(MessageModel message) {
		int n = getComponentCount();
		for (int i = 0; i < n; i++) {
			Component c = getComponent(i);
			if (c instanceof JMessage) {
				JMessage m = (JMessage) c;
				if (m.model == message)
					return m;
			}
		}
		throw new IllegalArgumentException("Component not found for " + message);
	}
	
	protected JEvent createEventComponent(EventModel event) {
		JEvent result = new JEvent();
		result.setModel(event);
		return result;
	}
	
	protected JMessage createMessageComponent(MessageModel message) {
		JMessage result = new JMessage();
		result.setModel(message);
		return result;
	}
	
	protected JMessage createMessageComponent(PendingMessageModel message) {
		JMessage result = new JMessage();
		result.setModel(message);
		return result;
	}
	
	protected JTimeRuler createTimeRulerComponent() {
		JTimeRuler result = new JTimeRuler();
		return result;
	}
	
	protected ChangeListener createChangeListener() {
		return getHandler();
	}
	
	protected TimeEventListener createTimeEventListener() {
		return getHandler();
	}
	
	protected ReorderEventListener createReorderEventListener() {
		return getHandler();
	}
	
	private Handler getHandler() {
		if (handler == null) {
			handler = new Handler();
		}
		return handler;
	}
	
	class Handler implements ChangeListener, TimeEventListener, ReorderEventListener {

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == model) {
				updateSubComponents();
				revalidate();
				repaint();
				fireStateChanged();
			} else if (e.getSource() == selectionModel) {
				repaint();
			}
		}
		
		public void timeChanged(EventObject e) {
			revalidate();
		}

		public void reorderStarted(ReorderEvent e) {
			fireReorderStarted(ReorderEvent.createFromComponent(e, JTimeline.this));
		}

		public void reorderUpdating(ReorderEvent e) {
			fireReorderUpdating(ReorderEvent.createFromComponent(e, JTimeline.this));	
		}

		public void reorderEnded(ReorderEvent e) {
			fireReorderEnded(ReorderEvent.createFromComponent(e, JTimeline.this));
		}
		
	}
	
}
