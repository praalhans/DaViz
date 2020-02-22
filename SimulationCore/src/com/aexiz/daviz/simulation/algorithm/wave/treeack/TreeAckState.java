package com.aexiz.daviz.simulation.algorithm.wave.treeack;

import com.aexiz.daviz.simulation.viewpoint.Channel;
import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;

import java.util.List;
import java.util.Map;

public class TreeAckState extends AbstractAlgorithmState {
    List<Channel> neighbors;
    List<Channel> children;
    PropertyVisitor state;

    @Override
    public String toString() {
        return "(" + neighbors + "," + children + "," + state + ")";
    }

    public void setNeighbors(List<Channel> neighbors) {
        this.neighbors = neighbors;
    }

    public void setChildren(List<Channel> children) {
        this.children = children;
    }

    public void setState(PropertyVisitor state) {
        this.state = state;
    }

    @Override
    public void makeProperties() {
        properties = Map.of(
                "State", state,
                "Neighbors", makeNodesProperty(neighbors),
                "Children", makeNodesProperty(children)
        );
    }
}