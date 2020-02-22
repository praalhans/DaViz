package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.frege.simulation.Visited.TRRUI;
import com.aexiz.daviz.simulation.*;
import com.aexiz.daviz.simulation.algorithm.information.*;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.visited.*;
import frege.prelude.PreludeBase.TMaybe;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple3;
import frege.run8.Thunk;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.Visited.procDesc;

public class Visited extends AbstractFregeBasicAlgorithm {

    public Visited() {
        assumption = VisitedAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        @SuppressWarnings("unchecked")
        TSet<Integer> t = (TSet<Integer>) o;
        VisitedToken result = new VisitedToken();
        result.setVisited(helper.forVertexSet(t));
        return result;
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        abstract class VisitedRRUI implements PropertyVisitor {
        }
        class VisitedState implements StateInformation {
            List<Node> hasToken;
            PropertyVisitor rrui;
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
        @SuppressWarnings("unchecked")
        TTuple3<TMaybe<TSet<Integer>>, TRRUI, TSet<TTuple2<Integer, Integer>>> st =
                (TTuple3<TMaybe<TSet<Integer>>, TRRUI, TSet<TTuple2<Integer, Integer>>>) o;
        VisitedState result = new VisitedState();
        DJust<TSet<Integer>> tok = st.mem1.call().asJust();
        result.hasToken = tok == null ? null : helper.forVertexSet(tok.mem1.call());
        TRRUI rrui = st.mem2.call();
        if (rrui.asReceived() != null) {
            VisitedReceived r = new VisitedReceived(helper.getChannelByTuple(rrui.asReceived().mem1.call()));
            result.rrui = r;
        } else if (rrui.asReplied() != null) {
            VisitedReplied r = new VisitedReplied(helper.getChannelByTuple(rrui.asReplied().mem1.call()));
            result.rrui = r;
        } else if (rrui.asUndefined() != null) {
            result.rrui = new VisitedUndefined();
        } else if (rrui.asInitiator() != null) {
            result.rrui = new VisitedInitiator();
        } else {
            throw new Error("Invalid RRUI value");
        }
        result.neighbors = helper.forEdgeSet(st.mem3.call());
        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        return (Boolean) o ? new VisitedTerminated() : new VisitedDecided();
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

}
