package com.aexiz.daviz;

import com.aexiz.daviz.simulation.*;
import com.aexiz.daviz.simulation.SendEvent;
import com.aexiz.daviz.simulation.algorithm.wave.Cidon;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

public class RandomProgram {

    private Simulation sim = new DefaultSimulation();
    private Random random = new Random();

    public static void main(String[] args) throws Exception {
        System.out.println("param_min,param_max,param_ratio,events,messages,edges,vertices,limited,time_ns");
        Params[] params = new Params[]{
                // Small, dense networks
                new Params(5, 10, 0.9f), new Params(5, 10, 0.8f),
                new Params(5, 10, 0.7f), new Params(5, 10, 0.6f),
                // Small, sparse networks
                new Params(5, 10, 0.5f),
                new Params(5, 10, 0.4f),
                // Large, dense networks
                new Params(10, 100, 0.9f), new Params(10, 100, 0.8f),
                new Params(10, 100, 0.7f), new Params(10, 100, 0.6f),
                // Large, sparse networks
                new Params(10, 100, 0.5f),
                new Params(10, 100, 0.4f),
                // Huge, dense networks
                new Params(100, 1000, 0.9f), new Params(100, 1000, 0.8f),
                new Params(100, 1000, 0.7f), new Params(100, 1000, 0.6f),
                // Huge, sparse networks
                new Params(100, 1000, 0.5f),
                new Params(100, 1000, 0.4f),
        };
        int MAX_TESTS = 1024;
        int WORKERS = 16;
        for (Params p : params) {
            int TESTS_PER_WORKER = MAX_TESTS / WORKERS;
            class Worker {
                RandomProgram r = new RandomProgram();

                public void call() throws Exception {
                    r.setAlgorithm(new Cidon());
                    for (int i = 0; i < TESTS_PER_WORKER; i++) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream out = new PrintStream(baos);
                        long before = System.nanoTime();
                        out.print(p);
                        out.print(",");
                        r.generateNetwork(p.min, p.max, p.ratio);
                        r.generateInitiator();
                        r.simulate(out);
                        long after = System.nanoTime();
                        out.print(after - before);
                        synchronized (System.out) {
                            System.out.println(baos.toString("UTF-8"));
                            System.out.flush();
                        }
                    }
                }
            }
            ;
            Thread[] th = new Thread[WORKERS];
            for (int i = 0; i < WORKERS; i++) {
                th[i] = new Thread() {
                    public void run() {
                        Worker w = new Worker();
                        try {
                            w.call();
                        } catch (Exception | Error ex) {
                            synchronized (System.out) {
                                ex.printStackTrace(System.out);
                                System.out.flush();
                            }
                        }
                    }
                };
                th[i].start();
            }
            for (int i = 0; i < WORKERS; i++) {
                th[i].join();
            }
        }
    }

    public void setAlgorithm(DefaultAlgorithm alg) {
        sim.setAlgorithm(alg);
    }

    public void setNetwork(DefaultNetwork network) {
        sim.setNetwork(network);
    }

    public void generateNetwork(int minSize, int maxSize, float edgesRatio) {
        DefaultNetwork result;
        while (true) {
            result = new DefaultNetwork();
            int size = minSize + (minSize < maxSize ? random.nextInt(maxSize - minSize) : 0);
            Node[] nodes = new Node[size];
            for (int i = 0; i < size; i++) {
                nodes[i] = new Node(String.valueOf(i));
                result.addNode(nodes[i]);
            }
            if (result.getNodes().length != size) throw new Error();
            int maxEdges = (size * (size - 1)) / 2;
            int edges = (int) (edgesRatio * maxEdges);
            for (int i = 0, j = 0; i < edges && j < maxEdges; i++, j++) {
                int from = random.nextInt(size);
                int to = random.nextInt(size);
                if (to > from) {
                    int tmp = from;
                    from = to;
                    to = tmp;
                }
                Channel c = new Channel(nodes[from], nodes[to]);
                Channel d = result.addChannel(c);
                if (c != d) i--;
            }
            result.makeUndirected();
            if (result.isStronglyConnected())
                break;
        }
        setNetwork(result);
    }

    public void generateInitiator() {
        Node[] nodes = sim.getNetwork().getNodes();
        if (nodes.length == 0) throw new Error("Network is invalid");
        sim.setInitiator(nodes[random.nextInt(nodes.length)]);
    }

    public void simulate(PrintStream out) throws Exception {
        sim.load();
        ExecutionStepper st = new ExecutionStepper(sim.getExecution()) {
            Execution getNext() {
                Execution[] c = current.getSuccessors();
                return c[random.nextInt(c.length)];
            }
        };
        st.max_rounds = sim.getAlgorithm().getMaxRounds(sim.getNetwork());
        // Run simulation until termination
        while (st.hasNext()) {
            st.step(st.getNext());
        }
        // Compute number of messages
        int messages = 0;
        DefaultEvent[] events = (DefaultEvent[]) st.current.getLinkedEvents();
        for (DefaultEvent e : events) {
            if (e instanceof SendEvent)
                messages++;
        }
        int edges = sim.getNetwork().getChannels().length / 2;
        int vertices = sim.getNetwork().getNodes().length;
        out.print(events.length + "," + messages + "," + edges + "," + vertices + "," + st.limited() + ",");
    }

    static class Params {
        int min, max;
        float ratio;

        Params(int min, int max, float ratio) {
            this.min = min;
            this.max = max;
            this.ratio = ratio;
        }

        public String toString() {
            return min + "," + max + "," + ratio;
        }
    }

}
