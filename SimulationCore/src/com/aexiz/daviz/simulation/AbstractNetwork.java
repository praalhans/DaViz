package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.viewpoint.Channel;
import com.aexiz.daviz.simulation.viewpoint.Node;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public abstract class AbstractNetwork implements Network {
    protected ArrayList<Node> processes = new ArrayList<>();
    protected ArrayList<Channel> channels = new ArrayList<>();

    protected String uuid;
    protected String simulationUUID;

    public AbstractNetwork() {
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public Node addNode(@NotNull Node process) {
        if (process.belongsToAnyNetwork() && !process.belongsToNetwork(uuid)) {
            throw new Error("Process already owned by other network");
        }
        process.setNetworkUUID(uuid);
        if (processes.contains(process)) return process;
        processes.add(process);
        return process;
    }

    @Override
    public Channel addChannel(@NotNull Channel channel) {
        if (channel.belongsToAnyNetwork() && !channel.belongsToNetwork(uuid)) {
            throw new Error("Channel already owned by other network");
        }
        channel.setNetworkUUID(uuid);
        if (channels.contains(channel)) return channel;
        channels.add(channel);
        return channel;
    }

    @Override
    public boolean hasNode(@NotNull Node process) {
        return process.belongsToNetwork(uuid);
    }

    @Override
    public boolean hasChannel(Node from, Node to) {
        for (Channel channel : channels) {
            if (channel.from == from && channel.to == to) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Node[] getNodes() {
        return processes.toArray(new Node[0]);
    }

    @Override
    public Channel[] getChannels() {
        return channels.toArray(new Channel[0]);
    }

    @Override
    public void makeUndirected() {
        // Symmetric closure, loop over copy to prevent CME
        for (Channel c : getChannels()) {
            addChannel(new Channel(c.to, c.from));
        }
    }

    @Override
    public boolean isWeighted() {
        for (Channel c : channels) {
            if (!c.hasWeight()) return false;
        }
        return true;
    }

    @Override
    public boolean isStronglyConnected() {
        for (Node n : processes) {
            n.setMarked(false);
        }
        if (processes.size() > 0) {
            Node start = processes.get(0);
            floodFill(start);
        }
        for (Node n : processes) {
            if (!n.isMarked()) return false;
        }
        return true;
    }

    protected void initiateNodesIds() {
        int id = 1;
        for (Node p : processes) {
            p.sethId(id++);
        }
    }

    @Override
    public Node getNodeById(int hId) {
        for (Node p : processes) {
            if (p.ishIdEqual(hId))
                return p;
        }
        return null;
    }

    @Override
    public void setSimulationUUID(String simulationUUID) {
        this.simulationUUID = simulationUUID;
    }

    @Override
    public boolean belongsToSimulation(String simulationUUID) {
        return this.simulationUUID.equals(simulationUUID);
    }

    @Override
    public boolean belongsToSimulation() {
        return simulationUUID != null;
    }

    private void floodFill(@NotNull Node node) {
        if (node.isMarked()) return;
        node.setMarked(true);
        for (Channel c : channels) {
            if (c.from == node)
                floodFill(c.to);
        }
    }
}
