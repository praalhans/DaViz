package com.aexiz.daviz.simulation.algorithm.information.state;

import java.util.Map;

public class InitiatorSeenState extends AbstractState {

    public InitiatorSeenState(boolean seem) {
        super(
                seem ? "InitiatorSeen" : "InitiatorUnseen",
                Map.of(
                        "", "Initiator",
                        "Seen token?", seem ? "true" : "false"
                )
        );
    }
}
