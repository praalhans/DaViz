package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

public interface JavaAlgorithm extends Algorithm {
    void makeInitialNodeStates(Network network);

    StateInformation getState(Node node);

}
