package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.JavaAlgorithm;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;

public class DefaultConfiguration extends AbstractConfiguration {
    void unload() {
        if (simulation == null) throw new Error("Invalid simulation");

        JavaAlgorithm alg = (JavaAlgorithm) simulation.getAlgorithm();
        processes = simulation.getNetwork().getNodes();
        processAlive = new boolean[processes.length];
        processState = new StateInformation[processes.length];
    }
}
