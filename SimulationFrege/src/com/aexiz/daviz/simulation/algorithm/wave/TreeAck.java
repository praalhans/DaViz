package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TUPDS;
import com.aexiz.daviz.simulation.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.FregeAlgorithm;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.tree.TreeAckDecider;
import com.aexiz.daviz.simulation.algorithm.wave.tree.TreeAckSpreader;
import com.aexiz.daviz.simulation.algorithm.wave.treeack.*;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.procDesc;

public class TreeAck extends AbstractFregeBasicAlgorithm {

    public TreeAck() {
        assumption = TreeAckAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        Short t = (Short) o;
        if (t == TMS.Info) return new TreeAckInfo();
        if (t == TMS.Ack) return new TreeAckAck();
        throw new Error("Invalid message");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        abstract class TreeAckUPDS implements PropertyVisitor {
        }
        class TreeAckState implements StateInformation {
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
        TreeAckState result = new TreeAckState();
        result.neighbors = helper.forEdgeSet(st.mem$neighbors.call());
        result.children = helper.forEdgeSet(st.mem$children.call());
        TUPDS up = st.mem$state.call();
        if (up.asUndefined() != null) {
            result.state = new TreeAckUndefined();
        } else if (up.asParent() != null) {
            result.state = new TreeAckParent(helper.getChannelByTuple(up.asParent().mem1.call()).to);
        } else if (up.asDecider() != null) {
            result.state = new TreeAckDecider(helper.getChannelByTuple(up.asDecider().mem1.call()));
        } else if (up.asSpreader() != null) {
            result.state = new TreeAckSpreader(helper.getChannelByTuple(up.asSpreader().mem1.call()));
        } else throw new Error();
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

}
