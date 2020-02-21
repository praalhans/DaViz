package com.aexiz.daviz.simulation;

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
