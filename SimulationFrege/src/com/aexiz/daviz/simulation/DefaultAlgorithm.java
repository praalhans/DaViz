package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.simulation.algorithm.information.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.StateInformation;

public abstract class DefaultAlgorithm extends AbstractAlgorithm implements Algorithm {

    // General property before simulation

    protected abstract MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o);

    // Unloading information from simulation

    protected abstract StateInformation makeAndUnloadState(FregeHelper helper, Object o);

    protected abstract ResultInformation makeAndUnloadResult(FregeHelper helper, Object o);

    protected abstract TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper);

    // Loading information into simulation
}
