package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.frege.simulation.algorithm.wave.DFS.TRRUI;
import com.aexiz.daviz.simulation.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.FregeAlgorithm;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.dfs.*;
import frege.prelude.PreludeBase.TMaybe;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple4;
import frege.run8.Thunk;

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

        @SuppressWarnings("unchecked")
        TTuple4<Boolean, TRRUI, TSet<TTuple2<Integer, Integer>>, TMaybe<TTuple2<Integer, Integer>>> st =
                (TTuple4<Boolean, TRRUI, TSet<TTuple2<Integer, Integer>>, TMaybe<TTuple2<Integer, Integer>>>) o;
        DFSState result = new DFSState();
        result.setHasToken(st.mem1.call());
        TRRUI rrui = st.mem2.call();
        result.setState(makeState(helper, rrui));
        result.setNeighbors(helper.forEdgeSet(st.mem3.call()));
        DJust<TTuple2<Integer, Integer>> in = st.mem4.call().asJust();
        result.setIncoming(in == null ? null : helper.getChannelByTuple(in.mem1.call()));
        result.makeProperties();

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

    private PropertyVisitor makeState(FregeHelper helper, TRRUI rrui) {
        if (rrui.asReceived() != null) {
            return new DFSReceived(helper.getChannelByTuple(rrui.asReceived().mem1.call()));
        }
        if (rrui.asReplied() != null) {
            return new DFSReplied(helper.getChannelByTuple(rrui.asReplied().mem1.call()));
        }
        if (rrui.asUndefined() != null) {
            return new DFSUndefined();
        }
        if (rrui.asInitiator() != null) {
            return new DFSInitiator();
        }
        throw new Error("Invalid RRUI value");
    }

}
