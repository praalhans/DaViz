package com.aexiz.daviz.simulation;

public interface Event {
    int TYPE_INTERNAL = 1;
    int TYPE_RECEIVE = 2;
    int TYPE_RESULT = 3;
    int TYPE_SEND = 4;

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

    int getType();

    boolean isFromType(int type);

}
