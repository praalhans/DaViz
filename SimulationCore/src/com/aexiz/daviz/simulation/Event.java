package com.aexiz.daviz.simulation;

public interface Event {
    Simulation getSimulation();

    void setSimulation(Simulation simulation);

    Execution getExecution();

    void setExecution(Execution execution);

    Event getPreviousEvent();

    Node getHappensAt();

    void setHappensAt(Node happensAt);

    boolean hasMatchingEvent();

    Event getMatchingEvent();

    boolean hasHappensAt();

    boolean hasNextState();

    boolean hasMessage();

    boolean hasResult();

    boolean hasSender();

    boolean hasReceiver();

    Node getSender();

    Node getReceiver();

    Information.State getNextState();

    Information.Message getMessage();

    Information.Result getResult();

}
