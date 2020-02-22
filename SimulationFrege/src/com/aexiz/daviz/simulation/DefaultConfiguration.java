package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Simulation.TConfiguration;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import frege.prelude.PreludeBase.TEither;
import frege.prelude.PreludeBase.TEither.DLeft;
import frege.prelude.PreludeBase.TList;
import frege.prelude.PreludeBase.TList.DCons;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Thunk;

import java.util.ArrayList;

public class DefaultConfiguration extends AbstractConfiguration implements Configuration {

    // Haskell dependencies
    transient TConfiguration<Object, Object, Object> hConfiguration;

    DefaultConfiguration() {
    }

    void unload() {
        if (simulation == null) throw new Error("Invalid simulation");
        if (hConfiguration == null) throw new Error("Invalid Haskell configuration");
        // Assume network remains unchanged
        Algorithm alg = simulation.getAlgorithm();
        FregeHelper helper = new FregeHelper(simulation);
        // 1. Read out state of each process
        processes = simulation.getNetwork().getNodes();
        processAlive = new boolean[processes.length];
        processState = new StateInformation[processes.length];
        for (int i = 0; i < processes.length; i++) {
            TEither<Object, Object> m = hConfiguration.mem2.call().apply(Thunk.lazy(processes[i].hId)).call();
            DLeft<Object, Object> j = m.asLeft();
            if (j == null) {
                processAlive[i] = false;
                processState[i] = null; // TODO unload result space
            } else {
                processAlive[i] = true;
                processState[i] = ((FregeAlgorithm) alg).makeAndUnloadState(helper, j.mem1.call());
            }
        }
        // 2. Read out channel state
        channels = simulation.getNetwork().getChannels();
        @SuppressWarnings("unchecked")
        ArrayList<MessageInformation>[] o = (ArrayList<MessageInformation>[]) new ArrayList<?>[channels.length];
        channelState = o;
        for (int i = 0; i < channels.length; i++) {
            channelState[i] = new ArrayList<MessageInformation>();
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
                    channelState[i].add(((FregeAlgorithm) alg).makeAndUnloadMessage(helper, msg));
                    found = true;
                    break;
                }
            }
            if (!found) throw new Error("Network and simulation out-of-sync");
            l = c.mem2.call();
        }
    }

}
