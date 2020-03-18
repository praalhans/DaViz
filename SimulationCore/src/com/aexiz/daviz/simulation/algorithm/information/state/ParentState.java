package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.HashMap;

public class ParentState extends AbstractViewpointState {
    public ParentState(Node node) {
        super(
                node,
                "parent",
                new HashMap<String, String>() {{
                    put("parent:", node.getLabel());
                }}
        );
    }
}
