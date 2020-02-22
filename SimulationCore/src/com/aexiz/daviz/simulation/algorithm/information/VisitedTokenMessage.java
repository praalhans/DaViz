package com.aexiz.daviz.simulation.algorithm.information;

import com.aexiz.daviz.simulation.Node;

import java.util.List;

public class VisitedTokenMessage extends AbstractMessageInformation {
    protected List<Node> visited;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VisitedTokenMessage) {
            VisitedTokenMessage other = (VisitedTokenMessage) obj;
            return other.visited.equals(visited);
        }
        return false;
    }

    @Override
    public String toString() {
        return "*token* " + visited;
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

    public List<Node> getVisited() {
        return visited;
    }

    public void setVisited(List<Node> visited) {
        this.visited = visited;
    }
}