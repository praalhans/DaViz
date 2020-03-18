package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;

public class AckSpreaderState extends AbstractViewpointState {
    public AckSpreaderState(Channel channel) {
        super(
                channel,
                "Spreader",
                new HashMap<String, String>() {{
                    put("Spreader:", channel.to.getLabel());
                }}
        );
    }
}
