package com.aexiz.daviz.simulation.algorithm.wave.echo;

import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;
import java.util.List;

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

    @Override
    public void makeProperties() {
        properties = new HashMap<String, Object>() {{
            put("State", state);
            put("Neighbors", makeNodesProperty(neighbors));
            put("Children", makeNodesProperty(children));
        }};
    }
}