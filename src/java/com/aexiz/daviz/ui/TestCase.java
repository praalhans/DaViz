package com.aexiz.daviz.ui;

import java.util.concurrent.Callable;

import com.aexiz.daviz.glue.*;
import com.aexiz.daviz.glue.Viewpoint.*;
import com.aexiz.daviz.glue.alg.*;

// Note that loading this class will also load all Haskell code
class TestCase {
	
	String page;
	AlgorithmSelection algorithm;
	Callable<Simulation> method;
	
	TestCase(String page, AlgorithmSelection algorithm, Callable<Simulation> method) {
		this.page = page;
		this.algorithm = algorithm;
		this.method = method;
	}
	String getPage() { return page; }
	String getName() { return algorithm.name; }
	
	static TestCase[] getTestCases() {
		return new TestCase[] {
				new TestCase("Page 20", AlgorithmSelection.TARRY, TestCase::page20book),
				new TestCase("Page 21", AlgorithmSelection.DFS, TestCase::page21book_dfs),
				new TestCase("Page 21", AlgorithmSelection.VISITED, TestCase::page21book_visited),
				new TestCase("Page 22", AlgorithmSelection.AWERBUCH, TestCase::page22book_awerbuch),
				new TestCase("Page 22", AlgorithmSelection.CIDON, TestCase::page22book_cidon),
				new TestCase("Page 23", AlgorithmSelection.TREE, TestCase::page23book_tree),
				new TestCase("Page 23", AlgorithmSelection.TREEACK, TestCase::page23book_tree_ack),
				new TestCase("Page 24", AlgorithmSelection.ECHO, TestCase::page24book_echo)
		};
	}
	
	static Simulation page20book() {
		// Example 4.1
		Simulation sim = new Simulation();
		sim.setAlgorithm(new Tarry());
		Network net = new Network();
		
		Node p = net.addNode(new Node("p"));
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node q = net.addNode(new Node("q"));
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node r = net.addNode(new Node("r"));
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 4.0f);
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node s = net.addNode(new Node("s"));
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node t = net.addNode(new Node("t"));
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		net.addChannel(new Channel(p, q));
		net.addChannel(new Channel(q, r));
		net.addChannel(new Channel(q, t));
		net.addChannel(new Channel(p, t));
		net.addChannel(new Channel(s, t));
		net.addChannel(new Channel(p, s));
		net.makeUndirected();
		
		sim.setNetwork(net);
		sim.setInitiator(p);
		sim.load();
		
		return sim;
	}
	
	static Simulation page21book_dfs() {
		// Example 4.2
		Simulation sim = new Simulation();
		sim.setAlgorithm(new DFS());
		Network net = new Network();
		
		Node p = net.addNode(new Node("p"));
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node q = net.addNode(new Node("q"));
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node r = net.addNode(new Node("r"));
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 4.0f);
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node s = net.addNode(new Node("s"));
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node t = net.addNode(new Node("t"));
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		net.addChannel(new Channel(p, q));
		net.addChannel(new Channel(q, r));
		net.addChannel(new Channel(q, t));
		net.addChannel(new Channel(p, t));
		net.addChannel(new Channel(s, t));
		net.addChannel(new Channel(p, s));
		net.makeUndirected();
		
		sim.setNetwork(net);
		sim.setInitiator(p);
		sim.load();
		
		return sim;
	}
	
	static Simulation page21book_visited() {
		// Example 4.2
		Simulation sim = new Simulation();
		sim.setAlgorithm(new Visited());
		Network net = new Network();
		
		Node p = net.addNode(new Node("p"));
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node q = net.addNode(new Node("q"));
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node r = net.addNode(new Node("r"));
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 4.0f);
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node s = net.addNode(new Node("s"));
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node t = net.addNode(new Node("t"));
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		net.addChannel(new Channel(p, q));
		net.addChannel(new Channel(q, r));
		net.addChannel(new Channel(q, t));
		net.addChannel(new Channel(p, t));
		net.addChannel(new Channel(s, t));
		net.addChannel(new Channel(p, s));
		net.makeUndirected();
		
		sim.setNetwork(net);
		sim.setInitiator(p);
		sim.load();
		
		return sim;
	}
	
	static Simulation page22book_awerbuch() {
		// Example 4.1
		Simulation sim = new Simulation();
		sim.setAlgorithm(new Awerbuch());
		Network net = new Network();
		
		Node p = net.addNode(new Node("p"));
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node q = net.addNode(new Node("q"));
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node r = net.addNode(new Node("r"));
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 4.0f);
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node s = net.addNode(new Node("s"));
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node t = net.addNode(new Node("t"));
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		net.addChannel(new Channel(p, q));
		net.addChannel(new Channel(q, r));
		net.addChannel(new Channel(q, t));
		net.addChannel(new Channel(p, t));
		net.addChannel(new Channel(s, t));
		net.addChannel(new Channel(p, s));
		net.makeUndirected();
		
		sim.setNetwork(net);
		sim.setInitiator(p);
		sim.load();
		
		return sim;
	}
	
	static Simulation page22book_cidon() {
		// Example 4.3
		Simulation sim = new Simulation();
		sim.setAlgorithm(new Cidon());
		Network net = new Network();
		
		Node p = net.addNode(new Node("p"));
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node q = net.addNode(new Node("q"));
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node r = net.addNode(new Node("r"));
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node t = net.addNode(new Node("t"));
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 4.0f);
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node s = net.addNode(new Node("s"));
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		net.addChannel(new Channel(p, q));
		net.addChannel(new Channel(p, r));
		net.addChannel(new Channel(p, s));
		net.addChannel(new Channel(q, r));
		net.addChannel(new Channel(s, r));
		net.addChannel(new Channel(r, t));
		net.makeUndirected();
		
		sim.setNetwork(net);
		sim.setInitiator(p);
		sim.load();
		
		return sim;
	}
	
	static Simulation page23book_tree() {
		// Example 4.4
		Simulation sim = new Simulation();
		sim.setAlgorithm(new Tree());
		Network net = new Network();
		
		Node p = net.addNode(new Node("p"));
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node q = net.addNode(new Node("q"));
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node r = net.addNode(new Node("r"));
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 1.0f);
		Node s = net.addNode(new Node("s"));
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 4.0f);
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 1.0f);
		Node t = net.addNode(new Node("t"));
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 6.0f);
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node u = net.addNode(new Node("u"));
		u.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 6.0f);
		u.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		net.addChannel(new Channel(p, r));
		net.addChannel(new Channel(q, r));
		net.addChannel(new Channel(r, s));
		net.addChannel(new Channel(s, t));
		net.addChannel(new Channel(s, u));
		net.makeUndirected();
		
		sim.setNetwork(net);
		sim.load();
		
		return sim;
	}
	
	static Simulation page23book_tree_ack() {
		// Example 4.4
		Simulation sim = new Simulation();
		sim.setAlgorithm(new TreeAck());
		Network net = new Network();
		
		Node p = net.addNode(new Node("p"));
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node q = net.addNode(new Node("q"));
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node r = net.addNode(new Node("r"));
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 1.0f);
		Node s = net.addNode(new Node("s"));
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 4.0f);
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 1.0f);
		Node t = net.addNode(new Node("t"));
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 6.0f);
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node u = net.addNode(new Node("u"));
		u.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 6.0f);
		u.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		net.addChannel(new Channel(p, r));
		net.addChannel(new Channel(q, r));
		net.addChannel(new Channel(r, s));
		net.addChannel(new Channel(s, t));
		net.addChannel(new Channel(s, u));
		net.makeUndirected();
		
		sim.setNetwork(net);
		sim.load();
		
		return sim;
	}
	
	static Simulation page24book_echo() {
		// Example 4.5
		Simulation sim = new Simulation();
		sim.setAlgorithm(new Echo());
		Network net = new Network();
		
		Node p = net.addNode(new Node("p"));
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		p.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node q = net.addNode(new Node("q"));
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		q.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node r = net.addNode(new Node("r"));
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 4.0f);
		r.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 0.0f);
		Node s = net.addNode(new Node("s"));
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 0.0f);
		s.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		Node t = net.addNode(new Node("t"));
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_X, 2.0f);
		t.putClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, 2.0f);
		net.addChannel(new Channel(p, q));
		net.addChannel(new Channel(p, t));
		net.addChannel(new Channel(p, s));
		net.addChannel(new Channel(q, r));
		net.addChannel(new Channel(q, s));
		net.addChannel(new Channel(q, t));
		net.addChannel(new Channel(t, s));
		net.makeUndirected();
		
		sim.setNetwork(net);
		sim.setInitiator(p);
		sim.load();
		
		return sim;
	}
	
}
