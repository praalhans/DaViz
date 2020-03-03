package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

public class ReceiveEvent extends DefaultEvent implements tReceiveEvent {
    transient MessageInformation message;
    transient StateInformation nextState;
    transient Node sender;

    ReceiveEvent() {
        super();
    }

    public ReceiveEvent(MessageInformation message, StateInformation nextState, Node sender, Node happensAt) {
        this.message = message;
        this.nextState = nextState;
        this.sender = sender;
        this.happensAt = happensAt;
    }

    public ReceiveEvent clone() {
        ReceiveEvent clonedEvent = (ReceiveEvent) super.clone(new ReceiveEvent());
        clonedEvent.message = this.message;
        clonedEvent.nextState = this.nextState;
        clonedEvent.sender = this.sender;

        return clonedEvent;
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
