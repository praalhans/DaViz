package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event;

public class InternalEvent extends DefaultEvent {

    // Haskell dependencies
    transient Event.TEvent.DEInternal<Object, Object, Object> hEvent;

    // Computed properties
    transient Information.State nextState;

    InternalEvent() {
    }

    void unload() {
        super.unload();
        hEvent = super.hEvent.asEInternal();
        SimulationHelper helper = new SimulationHelper(simulation);
        nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
    }

    protected com.aexiz.daviz.simulation.InternalEvent clone(DefaultEvent to) {
        super.clone(to);
        com.aexiz.daviz.simulation.InternalEvent tor = (com.aexiz.daviz.simulation.InternalEvent) to;
        tor.hEvent = this.hEvent;
        tor.nextState = this.nextState;
        return tor;
    }

    public com.aexiz.daviz.simulation.InternalEvent clone() {
        return clone(new com.aexiz.daviz.simulation.InternalEvent());
    }

    public boolean hasNextState() {
        return true;
    }

    public Information.State getNextState() {
        return nextState;
    }

    public String toString() {
        return "Process " + happensAt.getLabel() + " takes an internal action";
    }

}
