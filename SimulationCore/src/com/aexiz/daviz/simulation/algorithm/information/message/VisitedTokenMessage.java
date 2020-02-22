package com.aexiz.daviz.simulation.algorithm.information.message;

import com.aexiz.daviz.simulation.viewpoint.Node;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

import java.util.List;

public class VisitedTokenMessage extends AbstractMessage {
    protected List<Node> visited;

    public VisitedTokenMessage(List<Node> visited) {
        super("token");
        this.visited = visited;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VisitedTokenMessage) {
            VisitedTokenMessage other = (VisitedTokenMessage) obj;
            return other.visited.equals(visited);
        }
        return false;
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Token");
        builder.compoundProperty("Visited", builder1 -> {
            builder1.simpleProperty("", visited.size() + " elements");
            for (int i = 0, size = visited.size(); i < size; i++) {
                builder1.simpleProperty(i + ":", visited.get(i).getLabel());
            }
        });
    }
}