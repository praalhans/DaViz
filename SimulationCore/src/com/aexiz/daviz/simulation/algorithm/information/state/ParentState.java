package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.Map;

public class ParentState extends AbstractViewpointState {
    public ParentState(Node node) {
        super(
                node,
                "parent",
                Map.of("parent:", node.getLabel()));
    }
}
