package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;

public class ReceivedState extends AbstractViewpointState {
    public ReceivedState(Channel channel) {
        super(
                channel,
                "Received",
                new HashMap<String, String>() {{
                    put("", "Received");
                    put("From:", channel.to.getLabel());
                }}
        );
    }
}
