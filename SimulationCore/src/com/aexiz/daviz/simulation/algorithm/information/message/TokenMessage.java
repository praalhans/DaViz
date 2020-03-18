package com.aexiz.daviz.simulation.algorithm.information.message;

import java.util.HashMap;

public class TokenMessage extends AbstractMessage {
    public TokenMessage() {
        super("token", new HashMap<String, String>() {{
            put("", "Token");
        }});
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TokenMessage;
    }
}
