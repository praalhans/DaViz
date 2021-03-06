package com.aexiz.daviz.ui;

import java.util.ArrayList;
import com.aexiz.daviz.glue.Execution;

class ExecutionStepper {
	int max_rounds;
	ArrayList<Execution> path;
	Execution current;
	boolean replay = false;
	
	ExecutionStepper(Execution ex) {
		path = new ArrayList<>();
		path.add(current = ex);
	}
	
	boolean hasNext() {
		return path.size() < max_rounds && current.hasNext();
	}
	boolean limited() {
		return path.size() >= max_rounds;
	}
	Execution getNext() {
		return current.getNext();
	}
	void step(Execution next) throws Exception {
		if (max_rounds <= 0) throw new Error("Invalid step bound");
		boolean found = false;
		for (Execution succ : current.getSuccessors()) {
			if (succ == next) {
				found = true;
				break;
			}
		}
		if (!found) throw new Error("Stepping into unknown successor");
		path.add(next);
		current = next;
	}
}
