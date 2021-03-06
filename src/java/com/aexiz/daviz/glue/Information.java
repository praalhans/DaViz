package com.aexiz.daviz.glue;

public abstract class Information {
	
	Information() {
	}
	
	public static abstract class Message extends Information {
		public Message() {
		}
		public abstract String toString();
		public abstract boolean equals(Object obj);
	}
	
	public static abstract class State extends Information {
		public State() {
		}
		public abstract String toString();
	}
	
	public static abstract class Result extends Information {
		public Result() {
		}
		public abstract String toString();
	}
	
	public interface PropertyBuilder {
		
		void simpleProperty(String name, String value);
		void compoundProperty(String name, PropertyVisitor visitor);
		
	}
	
	public interface PropertyVisitor {
		
		void buildProperties(PropertyBuilder builder);
		
	}
	
	public abstract void buildProperties(PropertyBuilder builder);
	
}
