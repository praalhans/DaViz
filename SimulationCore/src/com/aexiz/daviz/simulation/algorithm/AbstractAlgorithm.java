package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Network;

public abstract class AbstractAlgorithm implements Algorithm {
    protected Assumption assumption;

    @Override
    public Assumption getAssumption() {
        return assumption;
    }

    @Override
    public Integer getMaxRounds(Network network) {
        return null;
    }

    @Override
    public boolean hasAssumption() {
        return assumption != null;
    }
}
