package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event.TEvent;
import com.aexiz.daviz.frege.simulation.Simulation;
import com.aexiz.daviz.frege.simulation.Simulation.TSimulation;
import com.aexiz.daviz.simulation.algorithm.FregeAlgorithm;
import com.aexiz.daviz.simulation.algorithm.event.DefaultEvent;
import com.aexiz.daviz.simulation.util.FregeHelper;
import frege.prelude.PreludeBase.TList;
import frege.prelude.PreludeBase.TList.DCons;
import frege.prelude.PreludeBase.TTuple2;

import java.util.ArrayList;
import java.util.Collections;

public class DefaultExecution extends AbstractExecution implements Execution {

    transient TSimulation<Object, Object, Object> hSimulation;

    DefaultExecution() {
    }

    public void loadFirst() {
        if (simulation == null) throw new Error("Invalid simulation");
        if (!(configuration instanceof InitialConfiguration)) throw new Error("Invalid initial configuration");
        FregeHelper helper = new FregeHelper(simulation);
        hSimulation = Simulation.simulation(
                ((DefaultConfiguration) configuration).hConfiguration,
                ((FregeAlgorithm) simulation.getAlgorithm()).getProcessDescription(helper));
        parent = null;
        lastEvent = null;
        // Reload configuration from Haskell
        configuration = null;
        unloadConfiguration();
    }

    @Override
    protected void isInvariant() {
        super.isInvariant();
        if (hSimulation == null) throw new Error("No Haskell simulation");
    }

    protected void unloadConfiguration() {
        isInvariant();
        if (configuration != null) return;
        // 1. Set configuration, translated back from Haskell
        configuration = new DefaultConfiguration();
        configuration.setSimulation(simulation);
        ((DefaultConfiguration) configuration).hConfiguration = hSimulation.mem$config.call();
        ((DefaultConfiguration) configuration).unload();
    }

    protected void unloadSuccessors() {
        isInvariant();
        if (successors != null) return;
        successors = new ArrayList<>();
        // 2. Check if there are any successors
        TList<TTuple2<TEvent<Object, Object, Object>, TSimulation<Object, Object, Object>>> succ =
                hSimulation.mem$successors.call();
        while (succ.asCons() != null) {
            DCons<TTuple2<TEvent<Object, Object, Object>, TSimulation<Object, Object, Object>>> head = succ.asCons();
            TTuple2<TEvent<Object, Object, Object>, TSimulation<Object, Object, Object>> tup = head.mem1.call();
            DefaultExecution result = new DefaultExecution();
            result.parent = this;
            result.simulation = simulation;
            result.hSimulation = tup.mem2.call();
            result.lastEvent = DefaultEvent.makeAndUnload(tup.mem1.call(), result);
            // Not unloading configuration
            // Not unloading successors
            successors.add(result);
            succ = succ.asCons().mem2.call();
        }
    }

    @Override
    public Configuration getConfiguration() {
        unloadConfiguration();
        return configuration;
    }

    @Override
    public Execution getNext(int index) {
        unloadSuccessors();
        return successors.get(index);
    }

    @Override
    public boolean hasNext() {
        unloadSuccessors();
        return successors.size() != 0;
    }

    @Override
    public int getNextCount() {
        unloadSuccessors();
        return successors.size();
    }

}
