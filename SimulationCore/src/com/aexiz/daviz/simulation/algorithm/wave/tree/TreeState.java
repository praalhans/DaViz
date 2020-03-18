package com.aexiz.daviz.simulation.algorithm.wave.tree;

import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;
import java.util.List;

public class TreeState extends AbstractAlgorithmState {
    List<Channel> neighbors;
    PropertyVisitor state;

    @Override
    public String toString() {
        return "(" + neighbors + "," + state + ")";
    }

    public void setNeighbors(List<Channel> neighbors) {
        this.neighbors = neighbors;
    }

    public void setState(PropertyVisitor state) {
        this.state = state;
    }

    @Override
    public void makeProperties() {
        properties = new HashMap<String, Object>() {{
            put("State", state);
            put("Neighbors", makeNodesProperty(neighbors));
        }};
    }
}