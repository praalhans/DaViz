package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.Map;

public class ReceivedSeenState extends AbstractViewpointState {

    public ReceivedSeenState(Channel channel, boolean seem) {
        super(
                channel,
                seem ? "ReceivedSeen" : "ReceivedUnseen",
                Map.of(
                        "", "Received",
                        "Seen token?", seem ? "true" : "false",
                        "From:", channel.to.getLabel()
                )
        );
    }
}
