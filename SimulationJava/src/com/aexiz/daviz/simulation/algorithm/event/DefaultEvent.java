package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.simulation.AbstractEvent;
import com.aexiz.daviz.simulation.Event;

public abstract class DefaultEvent extends AbstractEvent implements Cloneable, Event {
    DefaultEvent() {
        super();
    }

    @Override
    public abstract DefaultEvent clone();

    protected DefaultEvent clone(DefaultEvent to) {
        super.clone(to);
        return to;
    }
}
