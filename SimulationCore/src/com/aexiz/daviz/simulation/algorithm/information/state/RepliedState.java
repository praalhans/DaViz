package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;

public class RepliedState extends AbstractViewpointState {
    public RepliedState(Channel channel) {
        super(
                channel,
                "Replied",
                new HashMap<String, String>() {{
                    put("", "Replied");
                    put("To:", channel.to.getLabel());
                }}
        );
    }
}
