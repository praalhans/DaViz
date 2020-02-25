package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.algorithm.wave.*;

import java.util.Map;

public class Algorithms {
    public static Map<String, Algorithm> getAlgorithm() {
        return Map.of(
                "Tarry", new Tarry(),
                "DFS", new DFS(),
                "DFS + Visited", new Visited(),
                "Awerbuch", new Awerbuch(),
                "Cidon", new Cidon(),
                "Tree", new Tree(),
                "Tree + Ack", new TreeAck(),
                "Echo", new Echo()
        );
    }
}