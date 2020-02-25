package com.aexiz.daviz.simulation.algorithm;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;

public class Algorithms {
    @Contract(value = " -> new", pure = true)
    public static HashMap<String, ? extends Algorithm> getAlgorithm() {
        System.out.println("Dummy");
        return new HashMap<>();
    }
}
