package com.aexiz.daviz.simulation;

public class DefaultSimulation extends AbstractSimulation {
    @Override
    public void load() {
        network.load();

        execution = new DefaultExecution(this);
        ((DefaultExecution) execution).loadConfiguration();
    }
}
