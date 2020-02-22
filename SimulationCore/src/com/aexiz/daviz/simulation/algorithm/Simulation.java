package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Execution;
import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.viewpoint.Node;

public interface Simulation {
    void load();

    Algorithm getAlgorithm();

    void setAlgorithm(Algorithm algorithm);

    Assumption getAssumption();

    Network getNetwork();

    void setNetwork(Network network);

    void setInitiator(Node initiator);

    Execution getExecution();
}
