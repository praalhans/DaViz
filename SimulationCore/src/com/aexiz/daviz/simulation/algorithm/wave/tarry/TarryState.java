package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;

import java.util.List;
import java.util.Map;

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
        properties = Map.of(
                "Has token?", String.valueOf(hasToken),
                "State", state,
                "Neighbors", makeNodesProperty(neighbors)
        );
    }
}
