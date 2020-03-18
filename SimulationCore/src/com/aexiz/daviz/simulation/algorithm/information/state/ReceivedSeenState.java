package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;

public class ReceivedSeenState extends AbstractViewpointState {

    public ReceivedSeenState(Channel channel, boolean seem) {
        super(
                channel,
                seem ? "ReceivedSeen" : "ReceivedUnseen",
                new HashMap<String, String>() {{
                    put("", "Received");
                    put("Seen token?", seem ? "true" : "false");
                    put("From:", channel.to.getLabel());
                }}
        );
    }
}
