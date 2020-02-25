package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.simulation.algorithm.AbstractBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.wave.tarry.TarryAssumption;

public class Tarry extends AbstractBasicAlgorithm {
    public Tarry() {
        assumption = TarryAssumption.makeAssumption();
    }
}
