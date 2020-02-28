package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.information.Information;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJavaBasicAlgorithm extends AbstractBasicAlgorithm implements JavaAlgorithm {
    protected Map<Node, Information> processesSpace;
    protected Network network;

    public AbstractJavaBasicAlgorithm() {
        processesSpace = new HashMap<>();
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public void makeState() {
        if (network == null) throw new Error("Algorithm does not know the network");
        if (!processesSpace.isEmpty()) {
            processesSpace = new HashMap<>();
        }
        makeInitialNodeStates();
    }

    @Override
    public StateInformation getProcessSpace(Node node) {
        return (StateInformation) processesSpace.get(node);
    }

    abstract protected void makeInitialNodeStates();
}
