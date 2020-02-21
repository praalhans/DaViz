package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Set;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Lazy;
import frege.run8.Thunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class Network {

    /**
     * Simulation ownership
     */
    transient Simulation simulation;
    // Haskell dependence
    transient TSet<TTuple2<Integer, Integer>> hNetwork;

    private ArrayList<Node> processes = new ArrayList<>();
    private ArrayList<Channel> channels = new ArrayList<>();
    private String uuid;

    public Network() {
        uuid = UUID.randomUUID().toString();
    }

    public Node addNode(@NotNull Node process) {
        if (process.belongsToAnyNetwork() && !process.belongsToNetwork(uuid)) {
            throw new Error("Process already owned by other network");
        }
        process.setNetworkID(uuid);
        if (processes.contains(process)) return process;
        processes.add(process);
        return process;
    }

    public Channel addChannel(@NotNull Channel channel) {
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

    public boolean hasNode(Node process) {
        return process.belongsToNetwork(uuid);
    }

    public boolean hasChannel(Node from, Node to) {
        for (Channel c : channels) {
            if (c.from == from && c.to == to) return true;
        }
        return false;
    }

    public Node[] getNodes() {
        return processes.toArray(new Node[processes.size()]);
    }

    public Channel[] getChannels() {
        return channels.toArray(new Channel[channels.size()]);
    }

    public void makeUndirected() {
        // Symmetric closure, loop over copy to prevent CME
        for (Channel c : getChannels()) {
            addChannel(new Channel(c.to, c.from));
        }
    }

    public boolean isWeighted() {
        for (Channel c : channels) {
            if (!c.hasWeight()) return false;
        }
        return true;
    }

    public boolean isStronglyConnected() {
        for (Node n : processes) {
            n.marked = false;
        }
        if (processes.size() > 0) {
            Node start = processes.get(0);
            floodFill(start);
        }
        for (Node n : processes) {
            if (!n.marked) return false;
        }
        return true;
    }

    private void floodFill(Node node) {
        if (node.marked) return;
        node.marked = true;
        for (Channel c : channels) {
            if (c.from == node)
                floodFill(c.to);
        }
    }

    void load() {
        // 1. Construct unique Integers for processes
        int last = 1;
        for (Node p : processes) {
            p.hId = last++;
        }
        // 2. Construct set of edge tuples
        // 2.1. Empty set
        hNetwork = Set.<TTuple2<Integer, Integer>>emptyS().call();
        for (Channel c : channels) {
            // 2.2. Construct one element
            Lazy<Integer> from = Thunk.lazy(c.from.hId);
            Lazy<Integer> to = Thunk.lazy(c.to.hId);
            TTuple2<Integer, Integer> ch = TTuple2.mk(from, to);
            // 2.3. Add one element to set
            hNetwork = Set.addS(hNetwork, ch);
        }
    }

    Node getNodeById(int hId) {
        for (Node p : processes) {
            if (p.hId == hId)
                return p;
        }
        throw new Error("Haskell processes out-of-sync");
    }

}
