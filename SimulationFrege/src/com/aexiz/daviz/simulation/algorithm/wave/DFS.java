package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.frege.simulation.algorithm.wave.DFS.TRRUI;
import com.aexiz.daviz.simulation.*;
import com.aexiz.daviz.simulation.algorithm.information.*;
import com.aexiz.daviz.simulation.algorithm.wave.dfs.*;
import frege.prelude.PreludeBase.TMaybe;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple4;
import frege.run8.Thunk;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.DFS.procDesc;

public class DFS extends AbstractFregeBasicAlgorithm {

    public DFS() {
        assumption = DFSAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        if ((Short) o == 0) return new DFSToken();
        throw new Error("Invalid Haskell unit");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        abstract class DFS_RRUI implements PropertyVisitor {
        }
        class DFS_State implements StateInformation {
            boolean hasToken;
            PropertyVisitor rrui;
            List<Channel> neighbors;
            Channel incoming;

            public String toString() {
                return "(" + hasToken + "," + rrui + "," + neighbors + "," + incoming + ")";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Has token?", String.valueOf(hasToken));
                builder.compoundProperty("State", rrui);
                builder.simpleProperty("Reply to:", incoming == null ? "None" : incoming.to.getLabel());
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
        class DFS_Received extends DFS_RRUI {
            private Channel c;

            public String toString() {
                return "Received<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Received");
                builder.simpleProperty("From:", c.to.getLabel());
            }
        }
        class DFS_Initiator extends DFS_RRUI {
            public String toString() {
                return "Initiator";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Initiator");
            }
        }
        @SuppressWarnings("unchecked")
        TTuple4<Boolean, TRRUI, TSet<TTuple2<Integer, Integer>>, TMaybe<TTuple2<Integer, Integer>>> st =
                (TTuple4<Boolean, TRRUI, TSet<TTuple2<Integer, Integer>>, TMaybe<TTuple2<Integer, Integer>>>) o;
        DFS_State result = new DFS_State();
        result.hasToken = st.mem1.call();
        TRRUI rrui = st.mem2.call();
        if (rrui.asReceived() != null) {
            DFS_Received r = new DFS_Received();
            r.c = helper.getChannelByTuple(rrui.asReceived().mem1.call());
            result.rrui = r;
        } else if (rrui.asReplied() != null) {
            DFSReplied r = new DFSReplied(helper.getChannelByTuple(rrui.asReplied().mem1.call()));
            result.rrui = r;
        } else if (rrui.asUndefined() != null) {
            result.rrui = new DFSUndefined();
        } else if (rrui.asInitiator() != null) {
            result.rrui = new DFS_Initiator();
        } else {
            throw new Error("Invalid RRUI value");
        }
        result.neighbors = helper.forEdgeSet(st.mem3.call());
        DJust<TTuple2<Integer, Integer>> in = st.mem4.call().asJust();
        result.incoming = in == null ? null : helper.getChannelByTuple(in.mem1.call());
        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        return (Boolean) o ? new DFSTerminated() : new DFSDecided();
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

}
