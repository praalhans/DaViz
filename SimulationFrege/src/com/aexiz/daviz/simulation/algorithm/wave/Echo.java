package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Echo.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Echo.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Echo.TRRUI;
import com.aexiz.daviz.simulation.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.FregeAlgorithm;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.echo.*;
import frege.run8.Thunk;

import java.util.List;

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

        class EchoState implements StateInformation {
            List<Channel> neighbors;
            List<Channel> children;
            PropertyVisitor state;

            public String toString() {
                return "(" + neighbors + "," + children + "," + state + ")";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.compoundProperty("State", state);
                builder.compoundProperty("Neighbors", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", neighbors.size() + " elements");
                        for (int i = 0, size = neighbors.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", neighbors.get(i).to.getLabel());
                        }
                    }
                });
                builder.compoundProperty("Children", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", children.size() + " elements");
                        for (int i = 0, size = children.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", children.get(i).to.getLabel());
                        }
                    }
                });
            }
        }
        TPS st = (TPS) o;
        EchoState result = new EchoState();
        result.neighbors = helper.forEdgeSet(st.mem$neighbors.call());
        result.children = helper.forEdgeSet(st.mem$children.call());
        TRRUI up = st.mem$state.call();
        if (up.asUndefined() != null) {
            result.state = new EchoUndefined();
        } else if (up.asInitiator() != null) {
            result.state = new EchoInitiator();
        } else if (up.asReceived() != null) {
            result.state = new EchoReceived(helper.getChannelByTuple(up.asReceived().mem1.call()));
        } else if (up.asReplied() != null) {
            result.state = new EchoReplied(helper.getChannelByTuple(up.asReplied().mem1.call()));
        } else throw new Error();
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

}
