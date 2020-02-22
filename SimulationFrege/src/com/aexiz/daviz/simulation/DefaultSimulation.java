package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.Algorithm;
import com.aexiz.daviz.simulation.algorithm.Simulation;

public class DefaultSimulation extends AbstractSimulation implements Simulation {


    public DefaultSimulation() {
        super();
    }

    public DefaultSimulation(Algorithm algorithm) {
        super(algorithm);
    }

    @Override
    public void load() {
        network.load();
        InitialConfiguration ic = new InitialConfiguration();
        ic.simulation = this;
        ic.load();
        execution = new DefaultExecution();
        execution.setSimulation(this);
        execution.setConfiguration(ic);
        execution.loadFirst();
    }

}
