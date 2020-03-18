package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.JavaAlgorithm;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

public class DefaultConfiguration extends AbstractConfiguration {
    public DefaultConfiguration(Simulation simulation) {
        super();
        this.simulation = simulation;
    }

    void load() {
        if (simulation == null) throw new Error("Invalid simulation");

        processes = simulation.getNetwork().getNodes();
        processAlive = new boolean[processes.length];
        processState = new StateInformation[processes.length];

        JavaAlgorithm algorithm = ((JavaAlgorithm) simulation.getAlgorithm());
        algorithm.makeState(simulation.getNetwork());
        for (int i = 0, processesLength = processes.length; i < processesLength; i++) {
            Node process = processes[i];
            processAlive[i] = true;
            processState[i] = algorithm.getProcessSpace(process);
        }
    }
}
