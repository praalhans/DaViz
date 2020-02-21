package com.aexiz.daviz.simulation;

import java.util.List;

public abstract class AbstractExecution implements Execution {
    protected Simulation simulation;

    /**
     * may be null for root
     */
    protected Execution parent;

    /**
     * may be null for root
     */
    Event lastEvent;

    protected transient List<Execution> successors;
    protected transient Configuration configuration;

    protected void isInvariant() {
        if (simulation == null) throw new Error("Invalid simulation");
    }

    @Override
    public boolean hasParent() {
        isInvariant();
        return parent != null;
    }

    @Override
    public Execution getParent() {
        isInvariant();
        return parent;
    }

    @Override
    public boolean hasEvents() {
        return hasParent();
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void setParent(Execution parent) {
        this.parent = parent;
    }

    @Override
    public Event getLastEvent() {
        return lastEvent;
    }

    @Override
    public void setLastEvent(Event lastEvent) {
        this.lastEvent = lastEvent;
    }

    @Override
    public boolean hasNext() {
        unloadSuccessors();
        return successors.size() != 0;
    }

    @Override
    public int getNextCount() {
        unloadSuccessors();
        return successors.size();
    }

    /**
     * Always choose first successor bu default
     */
    @Override
    public Execution getNext() {
        return getNext(0);
    }

    @Override
    public Execution getNext(int index) {
        unloadSuccessors();
        return successors.get(index);
    }

    @Override
    public Execution[] getSuccessors() {
        Execution[] result = new Execution[getNextCount()];
        for (int i = 0; i < result.length; i++) {
            result[i] = getNext(i);
        }
        return result;
    }

    protected abstract void unloadSuccessors();
}
