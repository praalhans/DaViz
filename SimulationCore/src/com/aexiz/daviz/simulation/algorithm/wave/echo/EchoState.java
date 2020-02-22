package com.aexiz.daviz.simulation.algorithm.wave.echo;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;

import java.util.List;
import java.util.Map;

public class EchoState extends AbstractAlgorithmState {
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

    public void makeState() {
        properties = Map.of(
                "State", state,
                "Neighbors", makeNodesProperty(neighbors),
                "Children", makeNodesProperty(children)
        );
    }
}