package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Tarry.TDUI;
import com.aexiz.daviz.simulation.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.FregeAlgorithm;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.tarry.*;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple3;
import frege.run8.Thunk;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Tarry.procDesc;

public class Tarry extends AbstractFregeBasicAlgorithm {

    public Tarry() {
        assumption = TarryAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        if ((Short) o == 0) return new TarryToken();
        throw new Error("Invalid Haskell unit");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        @SuppressWarnings("unchecked")
        TTuple3<Boolean, TDUI, TSet<TTuple2<Integer, Integer>>> st = (TTuple3<Boolean, TDUI, TSet<TTuple2<Integer, Integer>>>) o;
        TarryState result = new TarryState();
        result.setHasToken(st.mem1.call());
        TDUI dui = st.mem2.call();
        result.setState(makeState(helper, dui));
        result.setNeighbors(helper.forEdgeSet(st.mem3.call()));
        result.makeProperties();

        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        return (Boolean) o ? new TarryTerminated() : new TarryDecided();
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

    private PropertyVisitor makeState(FregeHelper helper, TDUI dui) {
        if (dui.asReceived() != null) {
            return new TarryReceived(helper.getChannelByTuple(dui.asReceived().mem1.call()));
        }
        if (dui.asReplied() != null) {
            return new TarryReplied(helper.getChannelByTuple(dui.asReplied().mem1.call()));
        }
        if (dui.asUndefined() != null) {
            return new TarryUndefined();
        }
        if (dui.asInitiator() != null) {
            return new TarryInitiator();
        }
        throw new Error("Invalid DUI value");
    }

}
