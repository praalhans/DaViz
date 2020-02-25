package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.algorithm.wave.Cidon;
import com.aexiz.daviz.simulation.algorithm.wave.Tarry;

import java.util.HashMap;
import java.util.Map;

public class Algorithms {
    public static HashMap<String, ? extends Algorithm> getAlgorithm() {
        System.out.println("Frege");
        return (HashMap<String, ? extends Algorithm>) Map.of(
                "Tarry", new Tarry(),
                "Cion", new Cidon()
        );
    }
}
