package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.frege.simulation.Visited.TRRUI;
import com.aexiz.daviz.simulation.algorithm.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.FregeAlgorithm;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.treeack.VisitedState;
import com.aexiz.daviz.simulation.algorithm.wave.visited.*;
import com.aexiz.daviz.simulation.util.FregeHelper;
import frege.prelude.PreludeBase.TMaybe;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple3;
import frege.run8.Thunk;

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
        VisitedToken result = new VisitedToken(helper.forVertexSet(t));
        return result;
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        @SuppressWarnings("unchecked")
        TTuple3<TMaybe<TSet<Integer>>, TRRUI, TSet<TTuple2<Integer, Integer>>> st =
                (TTuple3<TMaybe<TSet<Integer>>, TRRUI, TSet<TTuple2<Integer, Integer>>>) o;
        VisitedState result = new VisitedState();
        DJust<TSet<Integer>> tok = st.mem1.call().asJust();
        result.setHasToken(tok == null ? null : helper.forVertexSet(tok.mem1.call()));
        TRRUI rrui = st.mem2.call();
        result.setState(makeState(helper, rrui));
        result.setNeighbors(helper.forEdgeSet(st.mem3.call()));
        result.makeProperties();
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

    private PropertyVisitor makeState(FregeHelper helper, TRRUI rrui) {
        if (rrui.asReceived() != null) {
            return new VisitedReceived(helper.getChannelByTuple(rrui.asReceived().mem1.call()));
        }
        if (rrui.asReplied() != null) {
            return new VisitedReplied(helper.getChannelByTuple(rrui.asReplied().mem1.call()));
        }
        if (rrui.asUndefined() != null) {
            return new VisitedUndefined();
        }
        if (rrui.asInitiator() != null) {
            return new VisitedInitiator();
        }
        throw new Error("Invalid RRUI value");
    }

}
