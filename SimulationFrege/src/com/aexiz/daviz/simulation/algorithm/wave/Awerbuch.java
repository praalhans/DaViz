package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Awerbuch.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Awerbuch.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Awerbuch.TRRRUII;
import com.aexiz.daviz.simulation.algorithm.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.FregeAlgorithm;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.awerbuch.*;
import com.aexiz.daviz.simulation.util.FregeHelper;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Thunk;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Awerbuch.procDesc;

public class Awerbuch extends AbstractFregeBasicAlgorithm {

    public Awerbuch() {
        assumption = AwerbuchAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        short t = (Short) o;

        if (t == TMS.Token) return new AwerbuchToken();
        if (t == TMS.Inf) return new AwerbuchInfo();
        if (t == TMS.Ack) return new AwerbuchAck();
        throw new Error("Unknown message");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        TPS st = (TPS) o;
        AwerbuchState result = new AwerbuchState();
        result.setHasToken(st.mem$hasToken.call());
        TRRRUII rrruii = st.mem$state.call();

        result.setState(makeState(helper, rrruii));

        DJust<TTuple2<Integer, Integer>> in;
        result.setInform(helper.forEdgeSet(st.mem$inform.call()));
        result.setAcked(helper.forEdgeSet(st.mem$acked.call()));
        in = st.mem$intended.call().asJust();
        result.setIntended(in == null ? null : helper.getChannelByTuple(in.mem1.call()));
        result.setForward(helper.forEdgeSet(st.mem$forward.call()));
        result.setInfo(helper.forEdgeSet(st.mem$info.call()));
        in = st.mem$last.call().asJust();
        result.setLast(in == null ? null : helper.getChannelByTuple(in.mem1.call()));
        in = st.mem$toAck.call().asJust();
        result.setToAck(in == null ? null : helper.getChannelByTuple(in.mem1.call()));

        result.makeProperties();

        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        return (Boolean) o ? new AwerbuchTerminated() : new AwerbuchDecided();
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

    private PropertyVisitor makeState(FregeHelper helper, TRRRUII rrruii) {
        if (rrruii.asReceivedSeen() != null) {
            return new AwerbuchReceivedSeen(
                    helper.getChannelByTuple(rrruii.asReceivedSeen().mem1.call()),
                    true);
        }
        if (rrruii.asReceivedUnseen() != null) {
            return new AwerbuchReceivedSeen(
                    helper.getChannelByTuple(rrruii.asReceivedUnseen().mem1.call()),
                    false);
        }
        if (rrruii.asReplied() != null) {
            return new AwerbuchReplied(helper.getChannelByTuple(rrruii.asReplied().mem1.call()));
        }
        if (rrruii.asUndefined() != null) {
            return new AwerbuchUndefined();
        }
        if (rrruii.asInitiatorSeen() != null) {
            return new AwerbuchInitiatorSeen(true);
        }
        if (rrruii.asInitiatorUnseen() != null) {
            return new AwerbuchInitiatorSeen(false);
        }
        throw new Error("Invalid RRRUII value");
    }

}
