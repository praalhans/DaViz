package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.frege.simulation.Visited.TRRUI;
import com.aexiz.daviz.simulation.Algorithm;
import com.aexiz.daviz.simulation.Assumption;
import com.aexiz.daviz.simulation.SimulationHelper;
import com.aexiz.daviz.simulation.Information;
import com.aexiz.daviz.simulation.Information.PropertyBuilder;
import com.aexiz.daviz.simulation.Information.PropertyVisitor;
import com.aexiz.daviz.simulation.Information.Result;
import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.Node;
import frege.prelude.PreludeBase.TMaybe;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple3;
import frege.run8.Thunk;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.Visited.procDesc;

public class Visited extends Algorithm {

    public Visited() {
        assumption = new Assumption() {
            {
                centralized_user = true;
            }
        };
    }

    protected Information.Message makeAndUnloadMessage(SimulationHelper help, Object o) {
        if (help == null || o == null) throw null;
        class VisitedMessage extends Information.Message {
            List<Node> visited;

            public String toString() {
                return "*token* " + visited;
            }

            public boolean equals(Object obj) {
                if (obj instanceof VisitedMessage) {
                    VisitedMessage other = (VisitedMessage) obj;
                    return other.visited.equals(visited);
                }
                return false;
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Token");
                builder.compoundProperty("Visited", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", visited.size() + " elements");
                        for (int i = 0, size = visited.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", visited.get(i).getLabel());
                        }
                    }
                });
            }
        }
        @SuppressWarnings("unchecked")
        TSet<Integer> t = (TSet<Integer>) o;
        VisitedMessage result = new VisitedMessage();
        result.visited = help.forVertexSet(t);
        return result;
    }

    protected Information.State makeAndUnloadState(SimulationHelper help, Object o) {
        if (help == null || o == null) throw null;
        abstract class VisitedRRUI implements PropertyVisitor {
        }
        class VisitedState extends Information.State {
            List<Node> hasToken;
            VisitedRRUI rrui;
            List<Channel> neighbors;

            public String toString() {
                return "(" + hasToken + "," + rrui + "," + neighbors + ")";
            }

            @Override
            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Has token?", hasToken == null ? "false" : hasToken.toString());
                builder.compoundProperty("State", rrui);
                builder.compoundProperty("Neighbors", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", neighbors.size() + " elements");
                        for (int i = 0, size = neighbors.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", neighbors.get(i).to.getLabel());
                        }
                    }
                });
            }
        }
        class VisitedReceived extends VisitedRRUI {
            private Channel c;

            public String toString() {
                return "Received<" + c + ">";
            }

            @Override
            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Received");
                builder.simpleProperty("From:", c.to.getLabel());
            }
        }
        class VisitedReplied extends VisitedRRUI {
            private Channel c;

            public String toString() {
                return "Replied<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Replied");
                builder.simpleProperty("To:", c.to.getLabel());
            }
        }
        class VisitedUndefined extends VisitedRRUI {
            public String toString() {
                return "Undefined";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Undefined");
            }
        }
        class VisitedInitiator extends VisitedRRUI {
            public String toString() {
                return "Initiator";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Initiator");
            }
        }
        @SuppressWarnings("unchecked")
        TTuple3<TMaybe<TSet<Integer>>, TRRUI, TSet<TTuple2<Integer, Integer>>> st =
                (TTuple3<TMaybe<TSet<Integer>>, TRRUI, TSet<TTuple2<Integer, Integer>>>) o;
        VisitedState result = new VisitedState();
        DJust<TSet<Integer>> tok = st.mem1.call().asJust();
        result.hasToken = tok == null ? null : help.forVertexSet(tok.mem1.call());
        TRRUI rrui = st.mem2.call();
        if (rrui.asReceived() != null) {
            VisitedReceived r = new VisitedReceived();
            r.c = help.getChannelByTuple(rrui.asReceived().mem1.call());
            result.rrui = r;
        } else if (rrui.asReplied() != null) {
            VisitedReplied r = new VisitedReplied();
            r.c = help.getChannelByTuple(rrui.asReplied().mem1.call());
            result.rrui = r;
        } else if (rrui.asUndefined() != null) {
            result.rrui = new VisitedUndefined();
        } else if (rrui.asInitiator() != null) {
            result.rrui = new VisitedInitiator();
        } else {
            throw new Error("Invalid RRUI value");
        }
        result.neighbors = help.forEdgeSet(st.mem3.call());
        return result;
    }

    protected Result makeAndUnloadResult(SimulationHelper helper, Object o) {
        class VisitedTerminated extends Information.Result {
            public String toString() {
                return "Terminated";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Terminated");
            }
        }
        class VisitedDecided extends Information.Result {
            public String toString() {
                return "Decided";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Decided");
            }
        }
        boolean result = (Boolean) o;
        if (result) {
            return new VisitedTerminated();
        } else {
            return new VisitedDecided();
        }
    }

    protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(SimulationHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

}
