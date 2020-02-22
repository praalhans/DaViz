package com.aexiz.daviz.simulation.algorithm.wave.cidon;

import com.aexiz.daviz.simulation.viewpoint.Channel;
import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;

import java.util.List;
import java.util.Map;

public class CidonState extends AbstractAlgorithmState {
    boolean hasToken;
    PropertyVisitor state;
    Channel intention;
    List<Channel> forward;
    List<Channel> info;

    @Override
    public String toString() {
        return "(" + hasToken + "," + state + "," + intention +
                "," + forward + "," + info + ")";
    }

    public void setHasToken(boolean hasToken) {
        this.hasToken = hasToken;
    }

    public void setState(PropertyVisitor state) {
        this.state = state;
    }

    public void setIntention(Channel intention) {
        this.intention = intention;
    }

    public void setForward(List<Channel> forward) {
        this.forward = forward;
    }

    public void setInfo(List<Channel> info) {
        this.info = info;
    }

    @Override
    public void makeProperties() {
        properties = Map.of(
                "Has token?", String.valueOf(hasToken),
                "State", state,
                "Token to", intention == null ? "None" : intention.to.getLabel(),
                "Candidates", makeNodesProperty(forward),
                "Neighbors", makeNodesProperty(info)
        );
    }
}
