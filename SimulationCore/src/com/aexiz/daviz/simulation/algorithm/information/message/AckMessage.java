package com.aexiz.daviz.simulation.algorithm.information.message;

import java.util.HashMap;

public class AckMessage extends AbstractMessage {
    public AckMessage() {
        super("ack", new HashMap<String, String>() {{
            put("", "Ack");
        }});
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AckMessage;
    }
}
