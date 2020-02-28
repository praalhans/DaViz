package com.aexiz.daviz.simulation;

public class DefaultExecution extends AbstractExecution {
    public DefaultExecution(Simulation simulation, Configuration configuration) {
        super(simulation, configuration);
    }

    public DefaultExecution(Simulation simulation) {
        super(simulation);
    }

    public DefaultExecution() {
    }

    public void load() {
        isInvariant();
        if (configuration != null) return;
        parent = null;
        lastEvent = null;

        configuration = new DefaultConfiguration(simulation);
        ((DefaultConfiguration) configuration).load();
    }

    protected void loadConfiguration() {

    }

    @Override
    public Configuration getConfiguration() {
        load();
        return super.getConfiguration();
    }

    @Override
    public boolean hasNext() {
        return super.hasNext();
    }
}
