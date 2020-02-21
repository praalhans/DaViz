package com.aexiz.daviz;

import com.aexiz.daviz.simulation.DefaultExecution;

import java.util.ArrayList;

class ExecutionStepper {
    int max_rounds;
    ArrayList<DefaultExecution> path;
    DefaultExecution current;
    boolean replay = false;

    ExecutionStepper(DefaultExecution ex) {
        path = new ArrayList<>();
        path.add(current = ex);
    }

    boolean hasNext() {
        return path.size() < max_rounds && current.hasNext();
    }

    boolean limited() {
        return path.size() >= max_rounds;
    }

    DefaultExecution getNext() {
        return current.getNext();
    }

    void step(DefaultExecution next) throws Exception {
        if (max_rounds <= 0) throw new Error("Invalid step bound");
        boolean found = false;
        for (DefaultExecution succ : current.getSuccessors()) {
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
