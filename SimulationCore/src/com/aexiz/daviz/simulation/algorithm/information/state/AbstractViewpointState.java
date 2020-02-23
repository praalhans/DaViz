package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Viewpoint;

import java.util.Map;

public class AbstractViewpointState extends AbstractState implements PropertyVisitor {
    Viewpoint viewpoint;

    public AbstractViewpointState(Viewpoint viewpoint, String state, Map<String, String> parameters) {
        super(state, parameters);
        this.viewpoint = viewpoint;
    }

    @Override
    public String toString() {
        Class<? extends Viewpoint> viewpointClass = viewpoint.getClass();
        return state + "<" + viewpointClass + ">";
    }
}
