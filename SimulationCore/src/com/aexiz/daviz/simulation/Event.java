package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

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

    StateInformation getNextState();

    MessageInformation getMessage();

    ResultInformation getResult();

}
