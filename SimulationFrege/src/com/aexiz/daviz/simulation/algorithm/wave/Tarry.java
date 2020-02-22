package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Tarry.TDUI;
import com.aexiz.daviz.simulation.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.FregeAlgorithm;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.tarry.*;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple3;
import frege.run8.Thunk;

import java.util.List;

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

        class TarryState implements StateInformation {
            boolean hasToken;
            PropertyVisitor dui;
            List<Channel> neighbors;

            public String toString() {
                return "(" + hasToken + "," + dui + "," + neighbors + ")";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Has token?", String.valueOf(hasToken));
                builder.compoundProperty("State", dui);
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
        TTuple3<Boolean, TDUI, TSet<TTuple2<Integer, Integer>>> st =
                (TTuple3<Boolean, TDUI, TSet<TTuple2<Integer, Integer>>>) o;
        TarryState result = new TarryState();
        result.hasToken = st.mem1.call();
        TDUI dui = st.mem2.call();
        if (dui.asReceived() != null) {
            result.dui = new TarryReceived(helper.getChannelByTuple(dui.asReceived().mem1.call()));
        } else if (dui.asReplied() != null) {
            result.dui = new TarryReplied(helper.getChannelByTuple(dui.asReplied().mem1.call()));
        } else if (dui.asUndefined() != null) {
            result.dui = new TarryUndefined();
        } else if (dui.asInitiator() != null) {
            result.dui = new TarryInitiator();
        } else {
            throw new Error("Invalid DUI value");
        }
        result.neighbors = helper.forEdgeSet(st.mem3.call());
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

}
