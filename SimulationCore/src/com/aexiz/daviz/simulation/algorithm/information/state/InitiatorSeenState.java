package com.aexiz.daviz.simulation.algorithm.information.state;

import java.util.HashMap;

public class InitiatorSeenState extends AbstractState {

    public InitiatorSeenState(boolean seem) {
        super(
                seem ? "InitiatorSeen" : "InitiatorUnseen",
                new HashMap<String, String>() {{
                    put("", "Initiator");
                    put("Seen token?", String.valueOf(seem));
                }}
        );
    }
}
