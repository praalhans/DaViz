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

    // TODO Consider using the simulation UUID only
    //  Verify:
    //  - Do we need the Simulation for anything other than `belongsToSimulation`?
    //  Benefits:
    //  - Avoid cyclic parameters between Network and Simulation
    //  - Simplify code legibility/maintenance
    Simulation getSimulation();

    void setSimulation(Simulation simulation);

    boolean belongsToSimulation();

    void load();

    Node getNodeById(int hid);
}
