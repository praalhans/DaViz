package com.aexiz.daviz.simulation;

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
        DefaultConfiguration.InitialConfiguration ic = new DefaultConfiguration.InitialConfiguration();
        ic.simulation = this;
        ic.load();
        execution = new DefaultExecution();
        execution.setSimulation(this);
        execution.configuration = ic;
        execution.loadFirst();
    }

}
