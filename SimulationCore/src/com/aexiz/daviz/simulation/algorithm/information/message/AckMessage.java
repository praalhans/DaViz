package com.aexiz.daviz.simulation.algorithm.information.message;

import java.util.Map;

public class AckMessage extends AbstractMessage {
    public AckMessage() {
        super(
                "ack",
                Map.of("", "Ack")
        );
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AckMessage;
    }
}
