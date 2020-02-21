package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.simulation.algorithm.AbstractAlgorithm;
import com.aexiz.daviz.simulation.algorithm.space.MessageSpace;
import com.aexiz.daviz.simulation.algorithm.space.ResultSpace;

public abstract class Algorithm extends AbstractAlgorithm {

    // General property before simulation

    public Integer getMaxRounds(Network network){
        return null;
    }

    protected abstract Information.Message makeAndUnloadMessage(SimulationHelper helper, Object o);

    // Unloading information from simulation

    protected abstract Information.State makeAndUnloadState(SimulationHelper helper, Object o);

    protected abstract Information.Result makeAndUnloadResult(SimulationHelper helper, Object o);

    protected abstract TProcessDescription<Object, Object, Object, Object> getProcessDescription(SimulationHelper helper);

    @Override
    protected Information.Result makeAndUnloadResult(ResultSpace resultSpace) {
        return makeAndUnloadResult(null, resultSpace);
    }

    @Override
    protected Information.Message makeAndUnloadMessage(MessageSpace messageSpace) {
        return makeAndUnloadMessage(null, messageSpace);
    }

    // Loading information into simulation
}
