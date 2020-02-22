package com.aexiz.daviz.simulation.algorithm.wave.cidon;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;

import java.util.List;
import java.util.Map;

public class CidonState extends AbstractAlgorithmState {
    boolean hasToken;
    PropertyVisitor rrui;
    Channel intention;
    List<Channel> forward;
    List<Channel> info;

    public CidonState(boolean hasToken, PropertyVisitor rrui, Channel intention, List<Channel> forward, List<Channel> info) {
        super(
                Map.of(
                        "Has token?", String.valueOf(hasToken),
                        "State", rrui,
                        "Token to", intention == null ? "None" : intention.to.getLabel(),
                        "Candidates", makeNodesProperty(forward),
                        "Neighbors", makeNodesProperty(info)
                )
        );
        this.hasToken = hasToken;
        this.rrui = rrui;
        this.intention = intention;
        this.forward = forward;
        this.info = info;
    }

    @Override
    public String toString() {
        return "(" + hasToken + "," + rrui + "," + intention +
                "," + forward + "," + info + ")";
    }
}
