package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;

public abstract class Algorithm {

    protected Assumption assumption;

    public Assumption getAssumption() {
        return assumption;
    }

    // General property before simulation

    public MaxRounds getMaxRounds() {
        return null;
    }

    protected abstract Information.Message makeAndUnloadMessage(GlueHelper helper, Object o);

    // Unloading information from simulation

    protected abstract Information.State makeAndUnloadState(GlueHelper helper, Object o);

    protected abstract Information.Result makeAndUnloadResult(GlueHelper helper, Object o);

    protected abstract TProcessDescription<Object, Object, Object, Object> getProcessDescription(GlueHelper helper);

    // Loading information into simulation

    public interface MaxRounds {
        int maxRounds(Network network);
    }

}
