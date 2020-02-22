package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event;
import com.aexiz.daviz.simulation.algorithm.information.StateInformation;
import com.aexiz.daviz.simulation.event.tInternalEvent;

public class InternalEvent extends DefaultEvent implements tInternalEvent {

    // Haskell dependencies
    transient Event.TEvent.DEInternal<Object, Object, Object> hEvent;

    // Computed properties
    transient StateInformation nextState;

    InternalEvent() {
        super();
    }

    @Override
    protected void unload() {
        super.unload();
        hEvent = super.hEvent.asEInternal();
        FregeHelper helper = new FregeHelper(simulation);
        nextState = ((FregeAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
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
    public StateInformation getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return "Process " + happensAt.getLabel() + " takes an internal action";
    }

}
