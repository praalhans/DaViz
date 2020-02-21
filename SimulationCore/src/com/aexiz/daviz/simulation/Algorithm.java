package com.aexiz.daviz.simulation;

public interface Algorithm {
    Assumption getAssumption();

    Integer getMaxRounds(Network network);

    boolean hasAssumption();
}
