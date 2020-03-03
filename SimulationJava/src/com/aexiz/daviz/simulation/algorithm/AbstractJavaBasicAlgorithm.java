package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Event;
import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.event.ResultEvent;
import com.aexiz.daviz.simulation.algorithm.information.Information;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJavaBasicAlgorithm extends AbstractAlgorithm implements JavaAlgorithm, BasicAlgorithm {
    protected Map<Node, Information> processesSpace;

    protected transient Event lastEvent;

    public AbstractJavaBasicAlgorithm() {
        processesSpace = new HashMap<>();
    }

    @Override
    public void makeState(Network network) {
        if (network == null) throw new Error("Algorithm does not know the network");
        if (!processesSpace.isEmpty()) {
            processesSpace = new HashMap<>();
        }
        makeInitialNodeStates(network);
    }

    @Override
    public StateInformation getProcessSpace(Node node) {
        return (StateInformation) processesSpace.get(node);
    }

    @Override
    public void updateProcessSpace(Event event) {
        lastEvent = event;
        processesSpace.put(event.getHappensAt(), event instanceof ResultEvent ? event.getResult() : event.getNextState());
    }

    abstract protected void makeInitialNodeStates(Network network);
}
