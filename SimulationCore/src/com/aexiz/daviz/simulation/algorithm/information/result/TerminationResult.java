package com.aexiz.daviz.simulation.algorithm.information.result;

import java.util.HashMap;

public class TerminationResult extends AbstractResult {
    public TerminationResult() {
        super("Terminated", new HashMap<String, String>() {{
            put("", "Terminated");
        }});
    }
}
