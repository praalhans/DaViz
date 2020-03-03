package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;

public class InternalEvent extends DefaultEvent implements tInternalEvent {
    transient StateInformation nextState;

    InternalEvent() {
        super();
    }

    public InternalEvent clone() {
        InternalEvent clonedEvent = (InternalEvent) super.clone(new InternalEvent());
        clonedEvent.nextState = this.nextState;

        return clonedEvent;
    }

    @Override
    public boolean hasNextState() {
        return true;
    }

    @Override
    public StateInformation getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return "Process " + happensAt.getLabel() + " takes an internal action";
    }

}
