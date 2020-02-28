package com.aexiz.daviz.simulation;

public class DefaultSimulation extends AbstractSimulation {
    @Override
    public void load() {
        network.load();
        InitialConfiguration initialConfiguration = new InitialConfiguration(this);
        initialConfiguration.load();
        execution = new DefaultExecution();
        execution.setSimulation(this);
        execution.setConfiguration(initialConfiguration);
        execution.loadFirst();
    }
}
