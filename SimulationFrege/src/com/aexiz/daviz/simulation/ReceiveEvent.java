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

    @Override
    void unload() {
        super.unload();
        hEvent = super.hEvent.asEReceive();
        SimulationHelper helper = new SimulationHelper(simulation);
        message = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadMessage(helper, hEvent.mem$msg.call());
        nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
        sender = simulation.getNetwork().getNodeById(hEvent.mem$send.call());
    }

    @Override
    protected ReceiveEvent clone(DefaultEvent to) {
        super.clone(to);
        ReceiveEvent tor = (ReceiveEvent) to;
        tor.hEvent = this.hEvent;
        tor.message = this.message;
        tor.nextState = this.nextState;
        tor.sender = this.sender;
        return tor;
    }

    public ReceiveEvent clone() {
        return clone(new ReceiveEvent());
    }

    @Override
    public boolean hasMessage() {
        return true;
    }

    @Override
    public Information.Message getMessage() {
        return message;
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
    public boolean hasSender() {
        return true;
    }

    @Override
    public Node getSender() {
        return sender;
    }

    @Override
    public boolean hasMatchingEvent() {
        return true;
    }

    @Override
    public SendEvent getMatchingEvent() {
        if (matchingEvent == null) throw new Error("Unmatched receive event");
        return (SendEvent) matchingEvent;
    }

    @Override
    public String toString() {
        return "Process " + happensAt.getLabel() + " receives " + message + " from " + sender;
    }

}
