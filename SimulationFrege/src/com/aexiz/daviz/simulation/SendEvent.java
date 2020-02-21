package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event;
import com.aexiz.daviz.simulation.event.tSendEvent;

public class SendEvent extends DefaultEvent implements tSendEvent {

    // Haskell dependencies
    transient Event.TEvent.DESend<Object, Object, Object> hEvent;

    // Computed properties
    transient Information.Message message;
    transient Information.State nextState;
    transient Node receiver;

    SendEvent() {
        super(TYPE_SEND);
    }

    @Override
    void unload() {
        super.unload();
        hEvent = super.hEvent.asESend();
        SimulationHelper helper = new SimulationHelper(simulation);
        message = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadMessage(helper, hEvent.mem$msg.call());
        nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
        receiver = simulation.getNetwork().getNodeById(hEvent.mem$recv.call());
    }

    @Override
    protected SendEvent clone(DefaultEvent to) {
        super.clone(to);
        SendEvent tor = (SendEvent) to;
        tor.hEvent = this.hEvent;
        tor.message = this.message;
        tor.nextState = this.nextState;
        tor.receiver = this.receiver;
        return tor;
    }

    @Override
    public SendEvent clone() {
        return clone(new SendEvent());
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
    public boolean hasReceiver() {
        return true;
    }

    @Override
    public Node getReceiver() {
        return receiver;
    }

    @Override
    public boolean hasMatchingEvent() {
        return matchingEvent != null;
    }

    @Override
    public ReceiveEvent getMatchingEvent() {
        return (ReceiveEvent) matchingEvent;
    }

    @Override
    public String toString() {
        return "Process " + happensAt.getLabel() + " sends " + message + " to " + receiver;
    }

}
