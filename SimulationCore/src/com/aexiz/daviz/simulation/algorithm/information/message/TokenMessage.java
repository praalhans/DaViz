package com.aexiz.daviz.simulation.algorithm.information.message;

import java.util.Map;

public class TokenMessage extends AbstractMessage {
    public TokenMessage() {
        super(
                "token",
                Map.of("", "Token")
        );
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TokenMessage;
    }
}
