package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Cidon.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Cidon.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Cidon.TRRUI;
import com.aexiz.daviz.simulation.*;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.cidon.*;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Thunk;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Cidon.procDesc;

public class Cidon extends AbstractFregeBasicAlgorithm {

    public Cidon() {
        assumption = CidonAssumption.makeAssumption();
    }

    @Override
    public Integer getMaxRounds(Network network) {
        return (network.getNodes().length + network.getChannels().length) * 15;
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        short t = (Short) o;

        if (t == TMS.Token) return new CidonToken();
        if (t == TMS.Inf) return new CidonInfo();
        throw new Error("Unknown message");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        class CidonState implements StateInformation {
            boolean hasToken;
            PropertyVisitor rrui;
            Channel intention;
            List<Channel> forward;
            List<Channel> info;

            public String toString() {
                return "(" + hasToken + "," + rrui + "," + intention +
                        "," + forward + "," + info + ")";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Has token?", String.valueOf(hasToken));
                builder.compoundProperty("State", rrui);
                builder.simpleProperty("Token to:", intention == null ? "None" : intention.to.getLabel());
                builder.compoundProperty("Candidates", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", forward.size() + " elements");
                        for (int i = 0, size = forward.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", forward.get(i).to.getLabel());
                        }
                    }
                });
                builder.compoundProperty("Neighbors", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", info.size() + " elements");
                        for (int i = 0, size = info.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", info.get(i).to.getLabel());
                        }
                    }
                });
            }
        }
        TPS st = (TPS) o;
        CidonState result = new CidonState();
        result.hasToken = st.mem$hasToken.call();
        TRRUI rrui = st.mem$state.call();
        if (rrui.asReceived() != null) {
            result.rrui = new CidonReceived(helper.getChannelByTuple(rrui.asReceived().mem1.call()));
        } else if (rrui.asReplied() != null) {
            result.rrui = new CidonReplied(helper.getChannelByTuple(rrui.asReplied().mem1.call()));
        } else if (rrui.asUndefined() != null) {
            result.rrui = new CidonUndefined();
        } else if (rrui.asInitiator() != null) {
            result.rrui = new CidonInitiator();
        } else {
            throw new Error("Invalid RRUI value");
        }
        DJust<TTuple2<Integer, Integer>> in;
        in = st.mem$intention.call().asJust();
        result.intention = in == null ? null : helper.getChannelByTuple(in.mem1.call());
        result.forward = helper.forEdgeSet(st.mem$forward.call());
        result.info = helper.forEdgeSet(st.mem$info.call());
        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        return (Boolean) o ? new CidonTerminated() : new CidonDecided();
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

}
