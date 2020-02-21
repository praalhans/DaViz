package com.aexiz.daviz;

import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.Simulation;
import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.Node;
import com.aexiz.daviz.simulation.algorithm.wave.*;

import java.util.concurrent.Callable;

// Note that loading this class will also load all Haskell code
class TestCases {

    String name;
    String page;
    Callable<Simulation> method;

    TestCases(String page, String name, Callable<Simulation> method) {
        this.page = page;
        this.name = name;
        this.method = method;
    }

    static TestCases[] getTestCases() {
        return new TestCases[]{
                new TestCases("Page 20", "Tarry", TestCases::page20book),
                new TestCases("Page 21", "DFS", TestCases::page21book_dfs),
                new TestCases("Page 21", "DFS + visited", TestCases::page21book_visited),
                new TestCases("Page 22", "Awerbuch", TestCases::page22book_awerbuch),
                new TestCases("Page 22", "Cidon", TestCases::page22book_cidon),
                new TestCases("Page 23", "Tree", TestCases::page23book_tree),
                new TestCases("Page 23", "Tree + ack", TestCases::page23book_tree_ack),
                new TestCases("Page 24", "Echo", TestCases::page24book_echo)
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

    String getPage() {
        return page;
    }

    String getName() {
        return name;
    }

}
