package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;

import java.util.Map;

public class ReceivedSeemState extends AbstractViewpointState {

    public ReceivedSeemState(Channel channel, boolean seem) {
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
