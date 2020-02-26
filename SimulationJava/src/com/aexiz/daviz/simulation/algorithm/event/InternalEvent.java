package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;

public class InternalEvent extends DefaultEvent implements tInternalEvent {
    transient StateInformation nextState;

    InternalEvent() {
        super();
    }

    @Override
    protected InternalEvent clone(DefaultEvent to) {
        super.clone(to);
        InternalEvent tor = (InternalEvent) to;
        tor.nextState = this.nextState;
        return tor;
    }

    public InternalEvent clone() {
        return clone(new InternalEvent());
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
