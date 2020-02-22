package com.aexiz.daviz.simulation.algorithm.wave.visited;

import com.aexiz.daviz.simulation.viewpoint.Node;
import com.aexiz.daviz.simulation.algorithm.information.message.VisitedTokenMessage;

import java.util.List;

public class VisitedToken extends VisitedTokenMessage {
    public VisitedToken(List<Node> visited) {
        super(visited);
    }
}
