package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.algorithm.wave.tarry.Tarry;

import java.util.HashMap;
import java.util.Map;

public class Algorithms {
    public static Map<String, Algorithm> getAlgorithm() {
        return new HashMap<String, Algorithm>() {{
            put("Tarry", new Tarry());
        }};
    }
}