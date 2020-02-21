package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Process;
import com.aexiz.daviz.frege.simulation.Set;
import frege.prelude.PreludeBase;

public class InitialConfiguration extends DefaultConfiguration implements Configuration.InitialConfiguration {
    @Override
    public void load() {
        Set.TSet<PreludeBase.TTuple2<Integer, Integer>> network = ((DefaultNetwork) simulation.getNetwork()).hNetwork;
        SimulationHelper helper = new SimulationHelper(simulation);
        Process.TProcessDescription<Object, Object, Object, Object> o = ((DefaultAlgorithm) simulation.getAlgorithm()).getProcessDescription(helper);
        hConfiguration = com.aexiz.daviz.frege.simulation.Simulation.initialConfiguration(network, o.simsalabim());
    }

}