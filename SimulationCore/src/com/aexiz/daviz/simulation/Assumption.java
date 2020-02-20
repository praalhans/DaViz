package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.Viewpoint.Node;

import java.util.List;

public class Assumption {

    // Configured by subclass
    protected boolean infiniteGraph;
    protected boolean directedGraph;
    protected boolean acyclicGraph;
    protected boolean dynamicGraph;

    protected boolean fifo_channels; // True if first-in-first-out channels
    protected boolean ooo_channels; // True if out-of-order channels

    protected boolean centralized_user; // User-input required
    protected boolean decentralized_user; // User-input required
    protected boolean decentralized_computed; // No user-input, algorithm decides initiators
    protected boolean centralized_computed; // No user-input, algorithm decides initiator
    Node initiator; // Given by user or undefined
    List<Node> initiators; // Given by user or undefined

    public Node getInitiator() {
        if (initiator == null) throw new Error("Initiator is not set");
        return initiator;
    }

    public void setInitiator(Node node) {
        if (!centralized_user) throw new Error("Algorithm is not centralized and not user-defined");
        initiator = node;
    }

    public boolean isDirectedGraph() {
        return directedGraph;
    }

    public boolean isAcyclicGraph() {
        return acyclicGraph;
    }

    public boolean isCentralized() {
        return centralized_user || centralized_computed;
    }

    public boolean isDecentralized() {
        return decentralized_user || decentralized_computed;
    }

    public boolean isInitiatorUser() {
        return centralized_user || decentralized_user;
    }

}
