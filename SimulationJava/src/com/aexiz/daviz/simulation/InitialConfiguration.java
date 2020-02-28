package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.JavaAlgorithm;
import com.aexiz.daviz.simulation.algorithm.wave.tarry.Tarry;

public class InitialConfiguration extends DefaultConfiguration implements Configuration.InitialConfiguration {
    public InitialConfiguration(Simulation simulation) {
        super();
        this.simulation = simulation;
    }

    @Override
    public void load() {
        ((JavaAlgorithm)simulation.getAlgorithm()).makeInitialNodeStates(simulation.getNetwork());
        throw new Error("InitialConfiguration not implemented");
    }
}
