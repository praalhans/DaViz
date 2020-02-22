package com.aexiz.daviz.simulation.algorithm.information.message;

import java.util.Map;

public class BroadcastMessage extends AbstractMessage {
    public BroadcastMessage() {
        super(
                "broadcast",
                Map.of("", "Broadcast")
        );
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BroadcastMessage;
    }
}