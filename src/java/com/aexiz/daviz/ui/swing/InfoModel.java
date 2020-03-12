package com.aexiz.daviz.ui.swing;

import java.io.Serializable;

import javax.swing.event.ChangeListener;

public interface InfoModel {
	
	static abstract class PropertyType implements Serializable {
		private static final long serialVersionUID = -7469784089240414551L;
		PropertyType() {}
	}
	// value is String
	static class SimplePropertyType extends PropertyType {
		private static final long serialVersionUID = 3989437777413291201L;
		SimplePropertyType() {}
	}
	// value is Property[]
	static class CompoundPropertyType extends PropertyType {
		private static final long serialVersionUID = -8024401864871586063L;
		CompoundPropertyType() {}
	}
	
	static final SimplePropertyType SIMPLE_TYPE = new SimplePropertyType();
	static final CompoundPropertyType COMPOUND_TYPE = new CompoundPropertyType();
	
	interface PropertyModel {
		
		String getTitle();
		
		Object getValue();
		
		PropertyType getType();
		
		// PropertyModel if nested, InfoModel if top-level
		Object getParent();
		
	}
	
	PropertyModel createProperty(String title, Object value, PropertyType type);
	
	PropertyModel createNestedProperty(PropertyModel parent, String title, Object value, PropertyType type);
	
	void addChangeListener(ChangeListener l);
	
	void removeChangeListener(ChangeListener l);
	
	int getPropertyCount();
	
	PropertyModel[] getProperty();
	
	String getPropertyTitle(int index);
	
	Object getPropertyValue(int index);
	
}
