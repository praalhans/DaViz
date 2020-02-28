package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.AbstractJavaBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Channel;
import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tarry extends AbstractJavaBasicAlgorithm {
    Map<Node, TarryState> nodesState;

    public Tarry() {
        assumption = TarryAssumption.makeAssumption();
        nodesState = new HashMap<>();
    }

    @Override
    public void makeState(Network network) {
        if (nodesState.isEmpty()){
            makeInitialNodeStates(network);
            return;
        }
        throw new Error("makeState is implemented only for initial state");
    }

    private void makeInitialNodeStates(Network network) {
        Node initiator = assumption.getInitiator();
        Map<Node, List<Channel>> mapOfChannelsFromNodes = network.makeMapOfChannelsFromNodes();
        mapOfChannelsFromNodes.forEach((node, channels) -> {
            boolean isNodeInitiator = node.isEqualTo(initiator);
            PropertyVisitor state = isNodeInitiator ? new TarryInitiator() : new TarryUndefined();
            TarryState initialState = new TarryState();

            initialState.setHasToken(isNodeInitiator);
            initialState.setState(state);
            initialState.setNeighbors(channels);
            initialState.makeProperties();

            nodesState.put(node, initialState);
        });
    }

    @Override
    public StateInformation getState(Node node) {
        return nodesState.get(node);
    }

}
