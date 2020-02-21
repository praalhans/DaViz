package com.aexiz.daviz.simulation;

public abstract class AbstractAlgorithm implements Algorithm {
    protected Assumption assumption;

    @Override
    public Assumption getAssumption() {
        return assumption;
    }

    @Override
    public Integer getMaxRounds(Network network){
        return null;
    }

    @Override
    public boolean hasAssumption() {
        return assumption != null;
    }
}
