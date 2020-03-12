package com.aexiz.daviz.ui.swing;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultInfoModel implements InfoModel {
	
	class DefaultPropertyModel implements PropertyModel {
		
		String title;
		Object value;
		PropertyType type;
		Object parent;
		
		private ArrayList<DefaultPropertyModel> properties;
		private transient boolean added;
		
		DefaultPropertyModel(PropertyModel parent, String title, Object value, PropertyType type) {
			if (title == null || type == null) throw null;
			if (type instanceof InfoModel.SimplePropertyType) {
				if (!(value instanceof String))
					throw new IllegalArgumentException();
			} else if (type instanceof InfoModel.CompoundPropertyType) {
				this.properties = new ArrayList<DefaultPropertyModel>();
				if (value != null) throw new IllegalArgumentException();
			} else throw new Error();
			this.title = title;
			this.value = value;
			this.type = type;
			this.parent = parent == null ? DefaultInfoModel.this : parent;
		}
		
		public String getTitle() {
			return title;
		}

		public Object getValue() {
			if (properties != null)
				return properties.toArray(new PropertyModel[properties.size()]);
			return value;
		}

		public PropertyType getType() {
			return type;
		}

		public Object getParent() {
			return parent;
		}
		
		DefaultInfoModel getRoot() {
			return DefaultInfoModel.this;
		}
		
		public void setProperty(PropertyModel[] ps) {
			for (DefaultPropertyModel p : properties) {
				p.unown();
			}
			properties.clear();
			for (PropertyModel p : ps) {
				addProperty0(-1, p);
			}
			if (added)
				fireStateChanged();
		}
		
		public void addProperty(PropertyModel p) {
			addProperty0(-1, p);
			if (added)
				fireStateChanged();
		}
		
		private void addProperty0(int index, PropertyModel m) {
			if (m == null)
				throw null;
			DefaultPropertyModel dm = (DefaultPropertyModel) m;
			if (dm.getRoot() != DefaultInfoModel.this) throw new IllegalArgumentException();
			if (dm.added) throw new IllegalArgumentException();
			if (dm.getParent() != this) throw new IllegalArgumentException();
			DefaultPropertyModel old = index < 0 ? null : properties.get(index);
			for (DefaultPropertyModel k : properties) {
				if (k == old) continue;
				if (k.getTitle().equals(dm.getTitle()))
					throw new IllegalArgumentException();
			}
			if (index < 0) properties.add(dm);
			else properties.set(index, dm);
			if (added)
				dm.own();
		}
		
		public void clear() {
			for (DefaultPropertyModel p : properties) {
				p.unown();
			}
			properties.clear();
			if (added)
				fireStateChanged();
		}
		
		void unown() {
			added = false;
			if (properties != null)
				for (DefaultPropertyModel p : properties)
					p.unown();
		}
		
		void own() {
			if (properties != null)
				for (DefaultPropertyModel p : properties)
					p.own();
			added = true;
		}
		
	}
	
	public PropertyModel createProperty(String title, Object value, PropertyType type) {
		return new DefaultPropertyModel(null, title, value, type);
	}

	public PropertyModel createNestedProperty(PropertyModel parent, String title, Object value, PropertyType type) {
		if (parent == null) throw null;
		if (((DefaultPropertyModel) parent).getRoot() != this)
			throw new IllegalArgumentException();
		return new DefaultPropertyModel(parent, title, value, type);
	}

	private EventListenerList listenerList = new EventListenerList();
	
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	
	protected transient ChangeEvent changeEvent;
	
	protected void fireStateChanged() {
		if (changeEvent == null)
			changeEvent = new ChangeEvent(this);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == ChangeListener.class)
				((ChangeListener) listeners[i+1]).stateChanged(changeEvent);
	}
	
	private ArrayList<DefaultPropertyModel> properties = new ArrayList<DefaultPropertyModel>();

	public int getPropertyCount() {
		return properties.size();
	}

	public PropertyModel[] getProperty() {
		return properties.toArray(new PropertyModel[properties.size()]);
	}

	public String getPropertyTitle(int index) {
		return properties.get(index).getTitle();
	}

	public Object getPropertyValue(int index) {
		return properties.get(index).getValue();
	}
	
	public void setProperty(PropertyModel[] ps) {
		for (DefaultPropertyModel p : properties) {
			p.unown();
		}
		properties.clear();
		for (PropertyModel p : ps) {
			addProperty0(-1, p);
		}
		fireStateChanged();
	}
	
	public void addProperty(PropertyModel p) {
		addProperty0(-1, p);
		fireStateChanged();
	}
	
	public void addNestedProperty(PropertyModel parent, PropertyModel nested) {
		((DefaultPropertyModel) parent).addProperty(nested);
	}
	
	private void addProperty0(int index, PropertyModel m) {
		if (m == null)
			throw null;
		DefaultPropertyModel dm = (DefaultPropertyModel) m;
		if (dm.getRoot() != this) throw new IllegalArgumentException();
		if (dm.added) throw new IllegalArgumentException();
		if (dm.getParent() != this) throw new IllegalArgumentException();
		DefaultPropertyModel old = index < 0 ? null : properties.get(index);
		for (DefaultPropertyModel k : properties) {
			if (k == old) continue;
			if (k.getTitle().equals(dm.getTitle()))
				throw new IllegalArgumentException();
		}
		if (index < 0) properties.add(dm);
		else properties.set(index, dm);
		dm.own();
	}
	
	public void clear() {
		for (DefaultPropertyModel p : properties) {
			p.unown();
		}
		properties.clear();
		fireStateChanged();
	}

}
