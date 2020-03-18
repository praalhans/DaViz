package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;

public class AckDeciderState extends AbstractViewpointState {
    public AckDeciderState(Channel channel) {
        super(
                channel,
                "Decider",
                new HashMap<String, String>() {{
                    put("Decider", channel.to.getLabel());
                }}
        );
    }
}
