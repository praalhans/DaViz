package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;
import java.util.List;

public class TarryState extends AbstractAlgorithmState {
    boolean hasToken;
    PropertyVisitor state;
    List<Channel> neighbors;

    @Override
    public String toString() {
        return "(" + hasToken + "," + state + "," + neighbors + ")";
    }

    public void setHasToken(boolean hasToken) {
        this.hasToken = hasToken;
    }

    public void setState(PropertyVisitor state) {
        this.state = state;
    }

    public void setNeighbors(List<Channel> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public void makeProperties() {
        properties = new HashMap<String, Object>() {{
            put("Has token?", String.valueOf(hasToken));
            put("State", state);
            put("Neighbors", makeNodesProperty(neighbors));
        }};
    }
}
