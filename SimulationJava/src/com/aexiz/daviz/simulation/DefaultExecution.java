package com.aexiz.daviz.simulation;

public class DefaultExecution extends AbstractExecution {
    @Override
    public void loadFirst() {
        isInvariant();
        resetExecution();
        configuration = new DefaultConfiguration();
        configuration.setSimulation(simulation);
        ((DefaultConfiguration) configuration).unload();

        throw new Error("DefaultExecution not implemented");
    }

    private void resetExecution(){
        parent = null;
        lastEvent = null;
        configuration = null;
    }
}
