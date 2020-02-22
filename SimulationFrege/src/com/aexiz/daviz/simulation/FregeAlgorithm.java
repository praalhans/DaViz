package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Process;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;

import java.security.InvalidParameterException;

public interface FregeAlgorithm extends Algorithm {
    // Unloading information from simulation
    MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o);

    StateInformation makeAndUnloadState(FregeHelper helper, Object o);

    ResultInformation makeAndUnloadResult(FregeHelper helper, Object o);

    // Loading information into simulation
    Process.TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper);

    static void validateParameters(FregeHelper helper, Object o) {
        if (helper == null || o == null) throw new InvalidParameterException("Parameters cannot be null");
    }
}
