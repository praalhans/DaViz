package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Set;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.simulation.viewpoint.Channel;
import com.aexiz.daviz.simulation.viewpoint.Node;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Lazy;
import frege.run8.Thunk;

public class DefaultNetwork extends AbstractNetwork implements Network {
    // Haskell dependence
    transient TSet<TTuple2<Integer, Integer>> hNetwork;

    public DefaultNetwork() {
        super();
    }

    @Override
    public void load() {
        // 1. Construct unique Integers for processes
        super.initiateNodesIds();
        // 2. Construct set of edge tuples
        mapNetworkToFrege();
    }

    @Override
    public Node getNodeById(int hId) {
        Node node = super.getNodeById(hId);
        if (node == null) {
            throw new Error("Haskell processes out-of-sync");
        }

        return node;
    }

    private void mapNetworkToFrege() {
        // 2.1. Empty set
        hNetwork = Set.<TTuple2<Integer, Integer>>emptyS().call();
        for (Channel c : channels) {
            // 2.2. Construct one element
            Lazy<Integer> from = Thunk.lazy(c.from.gethId());
            Lazy<Integer> to = Thunk.lazy(c.to.gethId());
            TTuple2<Integer, Integer> ch = TTuple2.mk(from, to);
            // 2.3. Add one element to set
            hNetwork = Set.addS(hNetwork, ch);
        }
    }
}
