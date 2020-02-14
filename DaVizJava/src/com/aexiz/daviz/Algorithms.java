package com.aexiz.daviz;

import com.aexiz.daviz.glue.Algorithm;
import com.aexiz.daviz.glue.alg.*;

class Algorithms {
	
	String name;
	Object alg;
	
	Algorithms(String name, Object alg) {
		this.name = name;
		this.alg = alg;
	}
	
	public String toString() {
		return name;
	}
	
	// It is necessary to expose the same methods as assumption, since
	// we do not want other classes depend on the class of Assumption.
	// It may pull in other classes compiled by Frege, which is too slow.
	
	public boolean isDirectedGraph() {
		return ((Algorithm) alg).getAssumption().isDirectedGraph();
	}
	
	public boolean isAcyclicGraph() {
		return ((Algorithm) alg).getAssumption().isAcyclicGraph();
	}
	
	public boolean isCentralized() {
		return ((Algorithm) alg).getAssumption().isCentralized();
	}
	
	public boolean isDecentralized() {
		return ((Algorithm) alg).getAssumption().isDecentralized();
	}
	
	public boolean isInitiatorUser() {
		return ((Algorithm) alg).getAssumption().isInitiatorUser();
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
	
}
