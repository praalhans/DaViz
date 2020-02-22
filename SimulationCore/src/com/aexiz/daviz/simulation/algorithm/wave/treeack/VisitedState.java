package com.aexiz.daviz.simulation.algorithm.wave.treeack;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.Node;
import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;

import java.util.List;
import java.util.Map;

public class VisitedState extends AbstractAlgorithmState {
    List<Node> hasToken;
    PropertyVisitor state;
    List<Channel> neighbors;

    public String toString() {
        return "(" + hasToken + "," + state + "," + neighbors + ")";
    }

    public void setHasToken(List<Node> hasToken) {
        this.hasToken = hasToken;
    }

    public void setState(PropertyVisitor state) {
        this.state = state;
    }

    public void setNeighbors(List<Channel> neighbors) {
        this.neighbors = neighbors;
    }

    public void makeProperties() {
        properties = Map.of(
                "Has token?", hasToken == null ? "false" : hasToken.toString(),
                "State", state,
                "Neighbors", makeNodesProperty(neighbors)
        );
    }
}
