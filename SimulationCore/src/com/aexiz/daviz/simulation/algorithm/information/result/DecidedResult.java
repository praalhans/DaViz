package com.aexiz.daviz.simulation.algorithm.information.result;

import java.util.HashMap;

public class DecidedResult extends AbstractResult {
    public DecidedResult() {
        super("Decided", new HashMap<String, String>() {{
            put("", "Decided");
        }});
    }
}
