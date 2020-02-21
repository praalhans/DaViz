package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;

public abstract class DefaultAlgorithm extends AbstractAlgorithm implements Algorithm {

    // General property before simulation

    protected abstract Information.Message makeAndUnloadMessage(FregeHelper helper, Object o);

    // Unloading information from simulation

    protected abstract Information.State makeAndUnloadState(FregeHelper helper, Object o);

    protected abstract Information.Result makeAndUnloadResult(FregeHelper helper, Object o);

    protected abstract TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper);

    // Loading information into simulation
}
