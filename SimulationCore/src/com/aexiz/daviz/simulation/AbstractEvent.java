package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.event.tInternalEvent;
import com.aexiz.daviz.simulation.algorithm.event.tReceiveEvent;
import com.aexiz.daviz.simulation.algorithm.event.tResultEvent;
import com.aexiz.daviz.simulation.algorithm.event.tSendEvent;
import com.aexiz.daviz.simulation.viewpoint.Locus;
import com.aexiz.daviz.simulation.viewpoint.Node;

public abstract class AbstractEvent extends Locus implements Cloneable, Event {
    protected Simulation simulation;
    protected Execution execution;

    protected transient Node happensAt;

    /**
     * Computed properties, unique to instance (not cloned)
     */
    protected transient Event matchingEvent;

    /**
     * Computed properties, unique to instance (not cloned)
     */
    protected transient Event previousEvent;

    public AbstractEvent() {
        if (!(this instanceof tSendEvent ||
                this instanceof tReceiveEvent ||
                this instanceof tInternalEvent ||
                this instanceof tResultEvent)) {
            throw new Error("Invalid event type. Event classes must implement one specific event interface");
        }
    }

    protected void isInvariant() {
        if (simulation == null) throw new Error("Invalid simulation");
        if (execution == null) throw new Error("Invalid execution");
    }

    @Override
    public Simulation getSimulation() {
        isInvariant();
        return simulation;
    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Execution getExecution() {
        isInvariant();
        return execution;
    }

    @Override
    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    @Override
    public Event getPreviousEvent() {
        return previousEvent;
    }

    @Override
    public Node getHappensAt() {
        return happensAt;
    }

    @Override
    public void setHappensAt(Node happensAt) {
        this.happensAt = happensAt;
    }

    @Override
    public boolean hasMatchingEvent() {
        return false;
    }

    @Override
    public Event getMatchingEvent() {
        throw new Error("Only defined for Send or Receive");
    }

    @Override
    public boolean hasHappensAt() {
        return true;
    }

    @Override
    public boolean hasNextState() {
        return false;
    }

    @Override
    public StateInformation getNextState() {
        throw new Error();
    }

    @Override
    public boolean hasMessage() {
        return false;
    }

    @Override
    public MessageInformation getMessage() {
        throw new Error();
    }

    @Override
    public boolean hasResult() {
        return false;
    }

    @Override
    public ResultInformation getResult() {
        throw new Error();
    }

    @Override
    public boolean hasSender() {
        return false;
    }

    @Override
    public Node getSender() {
        throw new Error();
    }

    @Override
    public boolean hasReceiver() {
        return false;
    }

    @Override
    public Node getReceiver() {
        throw new Error();
    }

    @Override
    public abstract Event clone();

    protected AbstractEvent clone(AbstractEvent to) {
        to.simulation = this.simulation;
        to.execution = this.execution;
        to.happensAt = this.happensAt;
        return to;
    }
}
