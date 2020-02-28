package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.viewpoint.Channel;
import com.aexiz.daviz.simulation.viewpoint.Node;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface Network {
    Node addNode(@NotNull Node process);

    Channel addChannel(@NotNull Channel channel);

    boolean hasNode(@NotNull Node process);

    boolean hasChannel(Node from, Node to);

    Node[] getNodes();

    Channel[] getChannels();

    List<Channel> getChannelsFromNode(Node node);

    Map<Node, List<Channel>> makeMapOfChannelsFromNodes();

    boolean isWeighted();

    boolean isStronglyConnected();

    void setSimulationUUID(String simulationUUID);

    boolean belongsToSimulation();

    boolean belongsToSimulation(String simulationUUID);

    void load();

    Node getNodeById(int hid);

    void makeUndirected();
}
