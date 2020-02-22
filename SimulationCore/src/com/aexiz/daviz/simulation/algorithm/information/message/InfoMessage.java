package com.aexiz.daviz.simulation.algorithm.information.message;

import java.util.Map;

public class InfoMessage extends AbstractMessage {
    public InfoMessage() {
        super(
                "info",
                Map.of("", "Info")
        );
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InfoMessage;
    }
}
