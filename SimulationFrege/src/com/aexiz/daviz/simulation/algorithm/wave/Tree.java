package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.TUP;
import com.aexiz.daviz.simulation.algorithm.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.FregeAlgorithm;
import com.aexiz.daviz.simulation.util.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.algorithm.wave.tree.*;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.procDesc;

public class Tree extends AbstractFregeBasicAlgorithm {

    public Tree() {
        assumption = TreeAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        if ((Short) o == 0) return new TreeInfo();
        throw new Error("Invalid Haskell unit");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        TPS st = (TPS) o;
        TreeState result = new TreeState();
        result.setNeighbors(helper.forEdgeSet(st.mem$neigh.call()));
        TUP up = st.mem$state.call();
        result.setState(makeState(helper, up));
        result.makeProperties();

        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        if ((Short) o == 0) return new TreeDecided();
        throw new Error("Invalid Haskell unit");
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc.call().simsalabim();
    }

    private PropertyVisitor makeState(FregeHelper helper, TUP up) {
        if (up.asUndefined() != null) {
            return new TreeUndefined();
        }
        if (up.asParent() != null) {
            return new TreeParent(helper.getChannelByTuple(up.asParent().mem1.call()).to);
        }
        throw new Error();
    }

}
