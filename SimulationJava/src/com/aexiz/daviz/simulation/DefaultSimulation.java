package com.aexiz.daviz.simulation;

public class DefaultSimulation extends AbstractSimulation {
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
