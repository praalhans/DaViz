package com.aexiz.daviz.simulation;

import org.jetbrains.annotations.NotNull;

public interface Network {
    Node addNode(@NotNull Node process);

    Channel addChannel(@NotNull Channel channel);

    boolean hasNode(@NotNull Node process);

    boolean hasChannel(Node from, Node to);

    Node[] getNodes();

    Channel[] getChannels();

    boolean isWeighted();

    boolean isStronglyConnected();

    void setSimulationUUID(String simulationUUID);

    boolean belongsToSimulation();

    boolean belongsToSimulation(String simulationUUID);

    void load();

    Node getNodeById(int hid);

    void makeUndirected();
}
