package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.simulation.Simulation;
import com.aexiz.daviz.frege.simulation.Simulation.TConfiguration;
import frege.prelude.PreludeBase.TEither;
import frege.prelude.PreludeBase.TEither.DLeft;
import frege.prelude.PreludeBase.TList;
import frege.prelude.PreludeBase.TList.DCons;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Thunk;

import java.io.PrintStream;
import java.util.ArrayList;

public class Configuration {

    // Properties
    Simulation simulation;

    // Haskell dependencies
    transient TConfiguration<Object, Object, Object> hConfiguration;

    // Computed properties
    transient Node[] processes;
    transient boolean[] processAlive;
    transient Information.State[] processState;

    transient Channel[] channels;
    transient ArrayList<Information.Message>[] channelState;

    Configuration() {
    }

    void unload() {
        if (simulation == null) throw new Error("Invalid simulation");
        if (hConfiguration == null) throw new Error("Invalid Haskell configuration");
        // Assume network remains unchanged
        Algorithm alg = simulation.getAlgorithm();
        SimulationHelper helper = new SimulationHelper(simulation);
        // 1. Read out state of each process
        processes = simulation.getNetwork().getNodes();
        processAlive = new boolean[processes.length];
        processState = new Information.State[processes.length];
        for (int i = 0; i < processes.length; i++) {
            TEither<Object, Object> m = hConfiguration.mem2.call().apply(Thunk.lazy(processes[i].hId)).call();
            DLeft<Object, Object> j = m.asLeft();
            if (j == null) {
                processAlive[i] = false;
                processState[i] = null; // TODO unload result space
            } else {
                processAlive[i] = true;
                processState[i] = ((DefaultAlgorithm) alg).makeAndUnloadState(helper, j.mem1.call());
            }
        }
        // 2. Read out channel state
        channels = simulation.getNetwork().getChannels();
        @SuppressWarnings("unchecked")
        ArrayList<Information.Message>[] o = (ArrayList<Information.Message>[]) new ArrayList<?>[channels.length];
        channelState = o;
        for (int i = 0; i < channels.length; i++) {
            channelState[i] = new ArrayList<Information.Message>();
        }
        TList<TTuple2<TTuple2<Integer, Integer>, Object>> l = hConfiguration.mem3.call();
        while (l.asCons() != null) {
            DCons<TTuple2<TTuple2<Integer, Integer>, Object>> c = l.asCons();
            TTuple2<TTuple2<Integer, Integer>, Object> cm = c.mem1.call();
            int f = cm.mem1.call().mem1.call().intValue();
            int t = cm.mem1.call().mem2.call().intValue();
            Object msg = cm.mem2.call();
            boolean found = false;
            for (int i = 0; i < channels.length; i++) {
                if (channels[i].from.hId == f && channels[i].to.hId == t) {
                    channelState[i].add(((DefaultAlgorithm) alg).makeAndUnloadMessage(helper, msg));
                    found = true;
                    break;
                }
            }
            if (!found) throw new Error("Network and simulation out-of-sync");
            l = c.mem2.call();
        }
    }

    public void printSummary(PrintStream out) {
        if (simulation == null) throw new Error("Invalid simulation");
        if (processes == null) throw new Error("Configuration not unloaded");
        for (int i = 0; i < processes.length; i++) {
            out.print("Process ");
            out.print(processes[i].getLabel());
            out.print(": ");
            if (processAlive[i]) {
                out.print(processState[i]);
            } else {
                out.print("*terminated*");
            }
            out.println();
        }
        out.println();
        for (int i = 0; i < channels.length; i++) {
            if (channelState[i].size() == 0) continue;
            out.print("Channel ");
            out.print(channels[i]);
            out.print(": ");
            for (Information.Message m : channelState[i]) {
                out.print(m);
            }
            out.println();
        }
        out.println();
    }

    public void loadProcessState(StateVisitor visitor) {
        for (int i = 0; i < processes.length; i++) {
            visitor.setState(processes[i], processState[i]);
        }
    }

    public interface StateVisitor {
        void setState(Node process, Information.State state);
    }

    public static class InitialConfiguration extends Configuration {

        void load() {
            TSet<TTuple2<Integer, Integer>> network = ((DefaultNetwork) simulation.getNetwork()).hNetwork;
            SimulationHelper helper = new SimulationHelper(simulation);
            TProcessDescription<Object, Object, Object, Object> o = ((DefaultAlgorithm) simulation.getAlgorithm()).getProcessDescription(helper);
            hConfiguration = com.aexiz.daviz.frege.simulation.Simulation.initialConfiguration(network, o.simsalabim());
        }

    }

}
