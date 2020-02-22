package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.frege.simulation.Visited.TRRUI;
import com.aexiz.daviz.simulation.*;
import com.aexiz.daviz.simulation.algorithm.information.*;
import com.aexiz.daviz.simulation.algorithm.wave.visited.VisitedDecided;
import com.aexiz.daviz.simulation.algorithm.wave.visited.VisitedTerminated;
import com.aexiz.daviz.simulation.algorithm.wave.visited.VisitedToken;
import frege.prelude.PreludeBase.TMaybe;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple3;
import frege.run8.Thunk;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.Visited.procDesc;

public class Visited extends DefaultAlgorithm {

    public Visited() {
        assumption = new Assumption() {
            {
                centralized_user = true;
            }
        };
    }

    protected MessageInformation makeAndUnloadMessage(FregeHelper help, Object o) {
        if (help == null || o == null) throw null;

        @SuppressWarnings("unchecked")
        TSet<Integer> t = (TSet<Integer>) o;
        VisitedToken result = new VisitedToken();
        result.setVisited(help.forVertexSet(t));
        return result;
    }

    protected StateInformation makeAndUnloadState(FregeHelper help, Object o) {
        if (help == null || o == null) throw null;
        abstract class VisitedRRUI implements PropertyVisitor {
        }
        class VisitedState implements StateInformation {
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

    protected ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        return (Boolean) o ? new VisitedTerminated() : new VisitedDecided();
    }

    protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

}
