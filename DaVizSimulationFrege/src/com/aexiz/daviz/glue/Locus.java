package com.aexiz.daviz.glue;

import java.util.HashMap;

public abstract class Locus {
	
	private HashMap<String,Object> clientProperties = new HashMap<String,Object>();
	
	public void putClientProperty(String key, Object value) {
		if (value == null) {
			clientProperties.remove(key);
		} else {
			clientProperties.put(key, value);
		}
	}
	
	public boolean hasClientProperty(String key) {
		return clientProperties.containsKey(key);
	}
	
	public boolean hasClientProperty(String key, Class<?> type) {
		if (!clientProperties.containsKey(key)) return false;
		Object value = clientProperties.get(key);
		return type.isInstance(value);
	}
	
	public Object getClientProperty(String key) {
		return clientProperties.get(key);
	}
	
}
