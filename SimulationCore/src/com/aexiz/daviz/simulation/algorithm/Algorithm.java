package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Network;

public interface Algorithm {
    Assumption getAssumption();

    Integer getMaxRounds(Network network);

    boolean hasAssumption();
}
