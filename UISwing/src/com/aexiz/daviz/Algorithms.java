package com.aexiz.daviz;

import com.aexiz.daviz.simulation.algorithm.Algorithm;
import com.aexiz.daviz.simulation.algorithm.wave.*;

class Algorithms {

    String name;
    Algorithm alg;

    Algorithms(String name, Algorithm alg) {
        this.name = name;
        this.alg = alg;
    }

    static Algorithms[] getAlgorithms() {
        return new Algorithms[]{
                new Algorithms("Tarry", new Tarry()),
                new Algorithms("DFS", new DFS()),
                new Algorithms("DFS + Visited", new Visited()),
                new Algorithms("Awerbuch", new Awerbuch()),
                new Algorithms("Cidon", new Cidon()),
                new Algorithms("Tree", new Tree()),
                new Algorithms("Tree + Ack", new TreeAck()),
                new Algorithms("Echo", new Echo()),
        };
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
