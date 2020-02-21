package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event;

public class InternalEvent extends DefaultEvent {

    // Haskell dependencies
    transient Event.TEvent.DEInternal<Object, Object, Object> hEvent;

    // Computed properties
    transient Information.State nextState;

    InternalEvent() {
        super(TYPE_INTERNAL);
    }

    @Override
    void unload() {
        super.unload();
        hEvent = super.hEvent.asEInternal();
        SimulationHelper helper = new SimulationHelper(simulation);
        nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
    }

    @Override
    protected InternalEvent clone(DefaultEvent to) {
        super.clone(to);
        InternalEvent tor = (InternalEvent) to;
        tor.hEvent = this.hEvent;
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
    public Information.State getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return "Process " + happensAt.getLabel() + " takes an internal action";
    }

}
