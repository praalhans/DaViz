package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.algorithm.wave.*;

import java.util.HashMap;
import java.util.Map;

public class Algorithms {
    public static Map<String, Algorithm> getAlgorithm() {
        return new HashMap<String, Algorithm>() {{
            put("Tarry", new Tarry());
            put("DFS", new DFS());
            put("DFS + Visited", new Visited());
            put("Awerbuch", new Awerbuch());
            put("Cidon", new Cidon());
            put("Tree", new Tree());
            put("Tree + Ack", new TreeAck());
            put("Echo", new Echo());
        }};
    }
}