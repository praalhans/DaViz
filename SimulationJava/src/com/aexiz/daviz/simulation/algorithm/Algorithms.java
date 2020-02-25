package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.algorithm.wave.Tarry;

import java.util.Map;

public class Algorithms {
    public static Map<String, Algorithm> getAlgorithm() {
        return Map.of(
                "Tarry", new Tarry()
        );
    }
}