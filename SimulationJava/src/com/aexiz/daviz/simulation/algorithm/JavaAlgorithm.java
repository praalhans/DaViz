package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Event;
import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

public interface JavaAlgorithm extends Algorithm {
    void setNetwork(Network network);

    void makeState();

    StateInformation getProcessSpace(Node node);

    Event[] makePossibleNextEvents();

}
