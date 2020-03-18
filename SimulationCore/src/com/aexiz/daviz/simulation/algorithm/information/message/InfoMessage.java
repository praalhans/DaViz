package com.aexiz.daviz.simulation.algorithm.information.message;

import java.util.HashMap;

public class InfoMessage extends AbstractMessage {
    public InfoMessage() {
        super("info", new HashMap<String, String>() {{
            put("", "Info");
        }});
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InfoMessage;
    }
}
