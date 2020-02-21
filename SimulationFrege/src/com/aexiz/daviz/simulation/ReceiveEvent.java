package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event;

public class ReceiveEvent extends DefaultEvent {

    // Haskell dependencies
    transient Event.TEvent.DEReceive<Object, Object, Object> hEvent;

    // Computed properties
    transient Information.Message message;
    transient Information.State nextState;
    transient Node sender;

    ReceiveEvent() {
    }

    void unload() {
        super.unload();
        hEvent = super.hEvent.asEReceive();
        SimulationHelper helper = new SimulationHelper(simulation);
        message = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadMessage(helper, hEvent.mem$msg.call());
        nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
        sender = simulation.getNetwork().getNodeById(hEvent.mem$send.call());
    }

    protected com.aexiz.daviz.simulation.ReceiveEvent clone(DefaultEvent to) {
        super.clone(to);
        com.aexiz.daviz.simulation.ReceiveEvent tor = (com.aexiz.daviz.simulation.ReceiveEvent) to;
        tor.hEvent = this.hEvent;
        tor.message = this.message;
        tor.nextState = this.nextState;
        tor.sender = this.sender;
        return tor;
    }

    public com.aexiz.daviz.simulation.ReceiveEvent clone() {
        return clone(new com.aexiz.daviz.simulation.ReceiveEvent());
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

    public boolean hasSender() {
        return true;
    }

    public Node getSender() {
        return sender;
    }

    public boolean hasMatchingEvent() {
        return true;
    }

    public SendEvent getMatchingEvent() {
        if (matchingEvent == null) throw new Error("Unmatched receive event");
        return (SendEvent) matchingEvent;
    }

    public String toString() {
        return "Process " + happensAt.getLabel() + " receives " + message + " from " + sender;
    }

}
