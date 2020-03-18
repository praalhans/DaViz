package com.aexiz.daviz.simulation.algorithm.information.message;

import java.util.HashMap;

public class BroadcastMessage extends AbstractMessage {
    public BroadcastMessage() {
        super("broadcast", new HashMap<String, String>() {{
            put("", "Broadcast");
        }});
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BroadcastMessage;
    }
}