package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event;

public class SendEvent extends DefaultEvent {

    // Haskell dependencies
    transient Event.TEvent.DESend<Object, Object, Object> hEvent;

    // Computed properties
    transient Information.Message message;
    transient Information.State nextState;
    transient Node receiver;

    SendEvent() {
    }

    void unload() {
        super.unload();
        hEvent = super.hEvent.asESend();
        SimulationHelper helper = new SimulationHelper(simulation);
        message = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadMessage(helper, hEvent.mem$msg.call());
        nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
        receiver = simulation.getNetwork().getNodeById(hEvent.mem$recv.call());
    }

    protected com.aexiz.daviz.simulation.SendEvent clone(DefaultEvent to) {
        super.clone(to);
        com.aexiz.daviz.simulation.SendEvent tor = (com.aexiz.daviz.simulation.SendEvent) to;
        tor.hEvent = this.hEvent;
        tor.message = this.message;
        tor.nextState = this.nextState;
        tor.receiver = this.receiver;
        return tor;
    }

    public com.aexiz.daviz.simulation.SendEvent clone() {
        return clone(new com.aexiz.daviz.simulation.SendEvent());
    }

    public boolean hasMessage() {
        return true;
    }

    public Information.Message getMessage() {
        return message;
    }

    public boolean hasNextState() {
        return true;
    }

    public Information.State getNextState() {
        return nextState;
    }

    public boolean hasReceiver() {
        return true;
    }

    public Node getReceiver() {
        return receiver;
    }

    public boolean hasMatchingEvent() {
        return matchingEvent != null;
    }

    public ReceiveEvent getMatchingEvent() {
        return (ReceiveEvent) matchingEvent;
    }

    public String toString() {
        return "Process " + happensAt.getLabel() + " sends " + message + " to " + receiver;
    }

}
