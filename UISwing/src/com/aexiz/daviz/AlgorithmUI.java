package com.aexiz.daviz;

import com.aexiz.daviz.simulation.algorithm.Algorithm;
import com.aexiz.daviz.simulation.algorithm.Algorithms;

import java.util.ArrayList;
import java.util.List;

// TODO Rename class to be more intuitive
class AlgorithmUI {

    String name;
    Algorithm alg;

    AlgorithmUI(String name, Algorithm alg) {
        this.name = name;
        this.alg = alg;
    }

    static AlgorithmUI[] getAlgorithms() {
        List<AlgorithmUI> algorithms = new ArrayList<>();

        Algorithms.getAlgorithm().forEach((key, value) -> {
            algorithms.add(new AlgorithmUI(key, value));
        });

        return algorithms.toArray(new AlgorithmUI[0]);
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
