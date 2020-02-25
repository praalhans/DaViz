package com.aexiz.daviz;

import com.aexiz.daviz.simulation.algorithm.Algorithm;
import com.aexiz.daviz.simulation.algorithm.Algorithms;

import java.util.HashMap;
import java.util.Map;

// TODO Rename class to be more intuitive
class AlgorithmUI {

    String name;
    Algorithm alg;

    AlgorithmUI(String name, Algorithm alg) {
        this.name = name;
        this.alg = alg;
    }

    static AlgorithmUI[] getAlgorithms() {
        HashMap<String, ? extends Algorithm> list = Algorithms.getAlgorithm();
        AlgorithmUI[] algorithms = new AlgorithmUI[list.size()];
        int index = 0;
        for (Map.Entry<String, ? extends Algorithm> entry : list.entrySet()) {
            String key = entry.getKey();
            Algorithm value = entry.getValue();
            algorithms[index] = new AlgorithmUI(key, value);
            index++;
        }
        return algorithms;

//        .forEach((key, value) -> {
//
//        });
//        return new AlgorithmUI[]{
//                new AlgorithmUI("Tarry", new Tarry()),
//                new AlgorithmUI("DFS", new DFS()),
//                new AlgorithmUI("DFS + Visited", new Visited()),
//                new AlgorithmUI("Awerbuch", new Awerbuch()),
//                new AlgorithmUI("Cidon", new Cidon()),
//                new AlgorithmUI("Tree", new Tree()),
//                new AlgorithmUI("Tree + Ack", new TreeAck()),
//                new AlgorithmUI("Echo", new Echo()),
//        };
    }

    // It is necessary to expose the same methods as assumption, since
    // we do not want other classes depend on the class of Assumption.
    // It may pull in other classes compiled by Frege, which is too slow.

    public String toString() {
        return name;
    }

    public boolean isDirectedGraph() {
        return alg.getAssumption().isDirectedGraph();
    }

    public boolean isAcyclicGraph() {
        return alg.getAssumption().isAcyclicGraph();
    }

    public boolean isCentralized() {
        return alg.getAssumption().isCentralized();
    }

    public boolean isDecentralized() {
        return alg.getAssumption().isDecentralized();
    }

    public boolean isInitiatorUser() {
        return alg.getAssumption().isInitiatorUser();
    }

}
