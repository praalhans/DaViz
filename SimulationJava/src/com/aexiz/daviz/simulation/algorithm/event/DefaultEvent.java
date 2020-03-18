package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.simulation.AbstractEvent;
import com.aexiz.daviz.simulation.Event;
import com.aexiz.daviz.simulation.Execution;

public abstract class DefaultEvent extends AbstractEvent implements Cloneable, Event {
    DefaultEvent() {
        super();
    }

    static public Event load(Execution execution) {
        return null;
    }

    @Override
    public abstract DefaultEvent clone();
}
