package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.frege.simulation.Event;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.FregeAlgorithm;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

public class SendEvent extends DefaultEvent implements tSendEvent {

    // Haskell dependencies
    transient Event.TEvent.DESend<Object, Object, Object> hEvent;

    // Computed properties
    transient MessageInformation message;
    transient StateInformation nextState;
    transient Node receiver;

    SendEvent() {
        super();
    }

    @Override
    protected void unload() {
        super.unload();
        hEvent = super.hEvent.asESend();
        FregeHelper helper = new FregeHelper(simulation);
        message = ((FregeAlgorithm) simulation.getAlgorithm()).makeAndUnloadMessage(helper, hEvent.mem$msg.call());
        nextState = ((FregeAlgorithm) simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
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
    public MessageInformation getMessage() {
        return message;
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
