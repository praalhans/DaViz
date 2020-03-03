package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.JavaAlgorithm;
import com.aexiz.daviz.simulation.algorithm.wave.tarry.Tarry;

import java.util.ArrayList;
import java.util.List;

public class DefaultExecution extends AbstractExecution {
    public DefaultExecution(Simulation simulation, Configuration configuration) {
        super(simulation, configuration);
    }

    public DefaultExecution(Simulation simulation, Execution parent, Event lastEvent) {
        super(simulation, parent, lastEvent);
    }

    public DefaultExecution(Simulation simulation) {
        super(simulation);
    }

    public DefaultExecution() {
    }

    public void loadConfiguration() {
        isInvariant();
        if (configuration != null) return;
        parent = null;
        lastEvent = null;

        configuration = new DefaultConfiguration(simulation);
        ((DefaultConfiguration) configuration).load();
    }

    private void loadSuccessor() {
        isInvariant();
        if (successors != null) return;

        successors = new ArrayList<>();

        List<Event> possibleNextEvents = ((Tarry) simulation.getAlgorithm()).makePossibleNextEvents();

        possibleNextEvents.forEach((event) -> {
            event.setSimulation(simulation);
            event.setExecution(this);

            successors.add(new DefaultExecution(simulation, this, event));
        });

    }

    @Override
    public Configuration getConfiguration() {
        loadConfiguration();
        return super.getConfiguration();
    }

    @Override
    public boolean hasNext() {
        loadSuccessor();
        return super.hasNext();
    }

    @Override
    public Execution getNext(int index) {
        loadSuccessor();
        Execution nextExecution = super.getNext(index);
        ((JavaAlgorithm) simulation.getAlgorithm()).updateProcessSpace(nextExecution.getLastEvent());
        return nextExecution;
    }

    @Override
    public int getNextCount() {
        loadSuccessor();
        return super.getNextCount();
    }
}
