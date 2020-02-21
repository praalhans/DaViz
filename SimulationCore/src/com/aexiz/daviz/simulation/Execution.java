package com.aexiz.daviz.simulation;

import java.util.List;

public interface Execution {
    boolean hasParent();

    Execution getParent();

    void setParent(Execution parent);

    boolean hasNext();

    int getNextCount();

    Execution getNext();

    Execution getNext(int index);

    Execution[] getSuccessors();

    boolean hasEvents();

    Event[] getLinkedEvents();

    List<Execution> getExecutionPath();

    Event getLastEvent();

    void setLastEvent(Event lastEvent);

    Configuration getConfiguration();

    void loadFirst();

    Simulation getSimulation();

    void setSimulation(Simulation simulation);
}
