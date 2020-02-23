package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.Map;

public class RepliedState extends AbstractViewpointState {
    public RepliedState(Channel channel) {
        super(
                channel,
                "Replied",
                Map.of(
                        "", "Replied",
                        "To", channel.to.getLabel()
                )
        );
    }
}
