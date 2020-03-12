package com.aexiz.daviz.ui;

import com.aexiz.daviz.glue.Algorithm;
import com.aexiz.daviz.glue.alg.*;

class AlgorithmSelection {
	
	String name;
	Object alg; // use Object to avoid pulling in Algorithm class
	
	AlgorithmSelection(String name, Object alg) {
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
	
	static AlgorithmSelection[] getAlgorithms() {
		// The class loader will pull in the Haskell dependencies
		return new AlgorithmSelection[]{
				TARRY,
				DFS,
				VISITED,
				AWERBUCH,
				CIDON,
				TREE,
				TREEACK,
				ECHO,
		};
	}
	
	static AlgorithmSelection TARRY = new AlgorithmSelection("Tarry", new Tarry());
	static AlgorithmSelection DFS = new AlgorithmSelection("DFS", new DFS());
	static AlgorithmSelection VISITED = new AlgorithmSelection("DFS + Visited", new Visited());
	static AlgorithmSelection AWERBUCH = new AlgorithmSelection("Awerbuch", new Awerbuch());
	static AlgorithmSelection CIDON = new AlgorithmSelection("Cidon", new Cidon());
	static AlgorithmSelection TREE = new AlgorithmSelection("Tree", new Tree());
	static AlgorithmSelection TREEACK = new AlgorithmSelection("Tree + Ack", new TreeAck());
	static AlgorithmSelection ECHO = new AlgorithmSelection("Echo", new Echo());
	
}
