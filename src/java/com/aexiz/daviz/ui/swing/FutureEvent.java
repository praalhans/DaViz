package com.aexiz.daviz.ui.swing;

import com.aexiz.daviz.ui.swing.ExecutionModel.DecideEventType;
import com.aexiz.daviz.ui.swing.ExecutionModel.EventType;
import com.aexiz.daviz.ui.swing.ExecutionModel.InternalEventType;
import com.aexiz.daviz.ui.swing.ExecutionModel.ReceiveEventType;
import com.aexiz.daviz.ui.swing.ExecutionModel.SendEventType;
import com.aexiz.daviz.ui.swing.ExecutionModel.TerminateEventType;

public class FutureEvent {
	
	String process;
	EventType type;
	String other;
	
	public FutureEvent(String process, EventType type, String other) {
		if (process == null || type == null) throw null;
		this.process = process;
		this.type = type;
		if (type instanceof ReceiveEventType || type instanceof SendEventType) {
			if (other == null) throw null;
			this.other = other;
		} else if (other != null) throw new IllegalArgumentException();
	}
	
	public String getProcessLabel() {
		return process;
	}
	
	public String getOtherLabel() {
		return other;
	}
	
	public EventType getType() {
		return type;
	}
	
	public String toString() {
		if (type instanceof ReceiveEventType) {
			return "(" + other + " ->) " + process;
		}
		if (type instanceof SendEventType) {
			return process + " (-> " + other + ")";
		}
		if (type instanceof InternalEventType) {
			return process + " (i)";
		}
		if (type instanceof TerminateEventType) {
			return process + " (|)";
		}
		if (type instanceof DecideEventType) {
			return process + " (*)";
		}
		throw new Error();
	}
	
}
