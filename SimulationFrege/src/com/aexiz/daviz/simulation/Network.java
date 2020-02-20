package com.aexiz.daviz.simulation;

import java.util.ArrayList;
import java.util.UUID;

import com.aexiz.daviz.frege.simulation.Set;
import com.aexiz.daviz.frege.simulation.Set.TSet;

import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Lazy;
import frege.run8.Thunk;
import org.jetbrains.annotations.NotNull;

public class Network {

    private ArrayList<Viewpoint.Node> processes = new ArrayList<>();
    private ArrayList<Viewpoint.Channel> channels = new ArrayList<>();
    private String uuid;

    // Simulation ownership
    transient Simulation simulation;

    // Haskell dependence
    transient TSet<TTuple2<Integer, Integer>> hNetwork;

	public Network() {
		uuid = UUID.randomUUID().toString();
	}

    public Viewpoint.Node addNode(@NotNull Viewpoint.Node process) {
        if (process.belongsToAnyNetwork() && !process.belongsToNetwork(uuid)) {
            throw new Error("Process already owned by other network");
        }
        process.setNetworkID(uuid);
        if (processes.contains(process)) return process;
        processes.add(process);
        return process;
    }

    public Viewpoint.Channel addChannel(@NotNull Viewpoint.Channel channel) {
        if (channel.belongsToAnyNetwork() && !channel.belongsToNetwork(uuid)) {
            throw new Error("Channel already owned by other network");
        }
        channel.setNetworkID(uuid);
        int i = channels.indexOf(channel);
        if (i == -1) {
            channels.add(channel);
            return channel;
        } else {
            return channels.get(i);
        }
    }

    public boolean hasNode(Viewpoint.Node process){
	    return process.belongsToNetwork(uuid);
    }

    public boolean hasChannel(Viewpoint.Node from, Viewpoint.Node to) {
        for (Viewpoint.Channel c : channels) {
            if (c.from == from && c.to == to) return true;
        }
        return false;
    }

    public Viewpoint.Node[] getNodes() {
        return processes.toArray(new Viewpoint.Node[processes.size()]);
    }

    public Viewpoint.Channel[] getChannels() {
        return channels.toArray(new Viewpoint.Channel[channels.size()]);
    }

    public void makeUndirected() {
        // Symmetric closure, loop over copy to prevent CME
        for (Viewpoint.Channel c : getChannels()) {
            addChannel(new Viewpoint.Channel(c.to, c.from));
        }
    }

    public boolean isWeighted() {
        for (Viewpoint.Channel c : channels) {
            if (!c.hasWeight()) return false;
        }
        return true;
    }

    public boolean isStronglyConnected() {
        for (Viewpoint.Node n : processes) {
            n.marked = false;
        }
        if (processes.size() > 0) {
            Viewpoint.Node start = processes.get(0);
            floodFill(start);
        }
        for (Viewpoint.Node n : processes) {
            if (!n.marked) return false;
        }
        return true;
    }

    private void floodFill(Viewpoint.Node node) {
        if (node.marked) return;
        node.marked = true;
        for (Viewpoint.Channel c : channels) {
            if (c.from == node)
                floodFill(c.to);
        }
    }

    void load() {
        // 1. Construct unique Integers for processes
        int last = 1;
        for (Viewpoint.Node p : processes) {
            p.hId = last++;
        }
        // 2. Construct set of edge tuples
        // 2.1. Empty set
        hNetwork = Set.<TTuple2<Integer, Integer>>emptyS().call();
        for (Viewpoint.Channel c : channels) {
            // 2.2. Construct one element
            Lazy<Integer> from = Thunk.lazy(c.from.hId);
            Lazy<Integer> to = Thunk.lazy(c.to.hId);
            TTuple2<Integer, Integer> ch = TTuple2.<Integer, Integer>mk(from, to);
            // 2.3. Add one element to set
            hNetwork = Set.addS(hNetwork, ch);
        }
    }

    Viewpoint.Node getNodeById(int hId) {
        for (Viewpoint.Node p : processes) {
            if (p.hId == hId)
                return p;
        }
        throw new Error("Haskell processes out-of-sync");
    }

}
