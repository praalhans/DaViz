package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Echo.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Echo.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Echo.TRRUI;
import com.aexiz.daviz.simulation.algorithm.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.FregeAlgorithm;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.echo.*;
import frege.run8.Thunk;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Echo.procDesc;

public class Echo extends AbstractFregeBasicAlgorithm {

    public Echo() {
        assumption = EchoAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        if ((Short) o == TMS.Broadcast) return new EchoBroadcast();
        throw new Error("Invalid message");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        TPS st = (TPS) o;
        EchoState result = new EchoState();
        result.setNeighbors(helper.forEdgeSet(st.mem$neighbors.call()));
        result.setChildren(helper.forEdgeSet(st.mem$children.call()));
        TRRUI up = st.mem$state.call();
        result.setState(makeState(helper, up));
        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        return (Boolean) o ? new EchoDecided() : new EchoTerminated();
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

    private PropertyVisitor makeState(FregeHelper helper, TRRUI up) {
        if (up.asUndefined() != null) {
            return new EchoUndefined();
        }
        if (up.asInitiator() != null) {
            return new EchoInitiator();
        }
        if (up.asReceived() != null) {
            return new EchoReceived(helper.getChannelByTuple(up.asReceived().mem1.call()));
        }
        if (up.asReplied() != null) {
            return new EchoReplied(helper.getChannelByTuple(up.asReplied().mem1.call()));
        }
        throw new Error();
    }
}
