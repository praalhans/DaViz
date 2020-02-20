package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;

public abstract class Algorithm extends AbstractAlgorithm {

    protected Assumption assumption;

    public Assumption getAssumption() {
        return assumption;
    }

    // General property before simulation

    public MaxRounds getMaxRounds() {
        return null;
    }

    protected abstract Information.Message makeAndUnloadMessage(SimulationHelper helper, Object o);

    // Unloading information from simulation

    protected abstract Information.State makeAndUnloadState(SimulationHelper helper, Object o);

    protected abstract Information.Result makeAndUnloadResult(SimulationHelper helper, Object o);

    protected abstract TProcessDescription<Object, Object, Object, Object> getProcessDescription(SimulationHelper helper);

    // Loading information into simulation

    public interface MaxRounds {
        int maxRounds(Network network);
    }

}
