package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.Map;

public class AckSpreaderState extends AbstractViewpointState {
    public AckSpreaderState(Channel channel) {
        super(
                channel,
                "Spreader",
                Map.of(
                        "Spreader:", channel.to.getLabel()
                )
        );
    }
}
