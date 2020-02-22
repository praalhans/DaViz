package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TUPDS;
import com.aexiz.daviz.simulation.algorithm.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.FregeAlgorithm;
import com.aexiz.daviz.simulation.util.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.tree.TreeAckDecider;
import com.aexiz.daviz.simulation.algorithm.wave.tree.TreeAckSpreader;
import com.aexiz.daviz.simulation.algorithm.wave.treeack.*;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.procDesc;

public class TreeAck extends AbstractFregeBasicAlgorithm {

    public TreeAck() {
        assumption = TreeAckAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        short t = (Short) o;
        if (t == TMS.Info) return new TreeAckInfo();
        if (t == TMS.Ack) return new TreeAckAck();
        throw new Error("Invalid message");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        TPS st = (TPS) o;
        TreeAckState result = new TreeAckState();
        result.setNeighbors(helper.forEdgeSet(st.mem$neighbors.call()));
        result.setChildren(helper.forEdgeSet(st.mem$children.call()));
        TUPDS up = st.mem$state.call();
        result.setState(makeState(helper, up));
        result.makeProperties();

        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        return (Boolean) o ? new TreeAckDecided() : new TreeAckTerminated();
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc.call().simsalabim();
    }

    private PropertyVisitor makeState(FregeHelper helper, TUPDS up) {
        if (up.asUndefined() != null) {
            return new TreeAckUndefined();
        }
        if (up.asParent() != null) {
            return new TreeAckParent(helper.getChannelByTuple(up.asParent().mem1.call()).to);
        }
        if (up.asDecider() != null) {
            return new TreeAckDecider(helper.getChannelByTuple(up.asDecider().mem1.call()));
        }
        if (up.asSpreader() != null) {
            return new TreeAckSpreader(helper.getChannelByTuple(up.asSpreader().mem1.call()));
        }
        throw new Error();
    }

}
