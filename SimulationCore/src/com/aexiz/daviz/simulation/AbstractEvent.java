package com.aexiz.daviz.simulation;

import java.util.Objects;

public abstract class AbstractEvent extends Locus implements Cloneable, Event {
    protected Simulation simulation;
    protected Execution execution;

    private final int type;

    protected transient Node happensAt;

    /**
     * Computed properties, unique to instance (not cloned)
     */
    protected transient Event matchingEvent;

    /**
     * Computed properties, unique to instance (not cloned)
     */
    protected transient Event previousEvent;

    protected void isInvariant() {
        if (simulation == null) throw new Error("Invalid simulation");
        if (execution == null) throw new Error("Invalid execution");
    }

    public AbstractEvent(int type){
        this.type = type;
    }

    @Override
    public Simulation getSimulation() {
        isInvariant();
        return simulation;
    }

    @Override
    public Execution getExecution() {
        isInvariant();
        return execution;
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
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void setExecution(Execution execution) {
        this.execution = execution;
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
    public Information.State getNextState() {
        throw new Error();
    }

    @Override
    public boolean hasMessage() {
        return false;
    }

    @Override
    public Information.Message getMessage() {
        throw new Error();
    }

    @Override
    public boolean hasResult() {
        return false;
    }

    @Override
    public Information.Result getResult() {
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

    @Override
    public boolean isFromType(int type) {
        return this.type == type;
    }

    @Override
    public int getType() {
        return type;
    }

    protected AbstractEvent clone(AbstractEvent to) {
        to.simulation = this.simulation;
        to.execution = this.execution;
        to.happensAt = this.happensAt;
        return to;
    }
}
