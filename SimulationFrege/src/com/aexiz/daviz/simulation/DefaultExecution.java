package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event.TEvent;
import com.aexiz.daviz.frege.simulation.Simulation;
import com.aexiz.daviz.frege.simulation.Simulation.TSimulation;
import com.aexiz.daviz.simulation.DefaultConfiguration.InitialConfiguration;
import frege.prelude.PreludeBase.TList;
import frege.prelude.PreludeBase.TList.DCons;
import frege.prelude.PreludeBase.TTuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultExecution extends AbstractExecution implements Execution {

    transient TSimulation<Object, Object, Object> hSimulation;

    DefaultExecution() {
    }

    @Override
    public void loadFirst() {
        if (simulation == null) throw new Error("Invalid simulation");
        if (!(configuration instanceof InitialConfiguration)) throw new Error("Invalid initial configuration");
        SimulationHelper helper = new SimulationHelper(simulation);
        hSimulation = Simulation.simulation(
                configuration.hConfiguration,
                ((DefaultAlgorithm) simulation.getAlgorithm()).getProcessDescription(helper));
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

    private void unloadConfiguration() {
        isInvariant();
        if (configuration != null) return;
        // 1. Set configuration, translated back from Haskell
        configuration = new DefaultConfiguration();
        configuration.simulation = simulation;
        configuration.hConfiguration = hSimulation.mem$config.call();
        configuration.unload();
    }

    private void unloadSuccessors() {
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

    // Traversal methods


    @Override
    public Event[] getLinkedEvents() {
        ArrayList<DefaultEvent> events = new ArrayList<>();
        // Traverse and collect
        Execution elem = this;
        while (elem.hasEvents()) {
            events.add(elem.getLastEvent());
            elem = elem.getParent();
        }
        // Reverse
        Collections.reverse(events);
        // Match and link
        DefaultEvent.matchAndLinkEvents(events);
        return events.toArray(new DefaultEvent[0]);
    }

    @Override
    public List<Execution> getExecutionPath() {
        ArrayList<Execution> result = new ArrayList<>();
        // Traverse and collect
        Execution elem = this;
        while (elem != null) {
            result.add(elem);
            elem = elem.getParent();
        }
        // Reverse
        Collections.reverse(result);
        return result;
    }

    @Override
    public Event getLastEvent() {
        Event[] linkedEvents = getLinkedEvents();
        return linkedEvents[linkedEvents.length - 1];
    }

    // Property methods

    @Override
    public Configuration getConfiguration() {
        unloadConfiguration();
        return configuration;
    }

}
