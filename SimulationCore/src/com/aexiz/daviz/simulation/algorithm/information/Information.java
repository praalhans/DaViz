package com.aexiz.daviz.simulation.algorithm.information;

public interface Information {
    void buildProperties(PropertyBuilder builder);

    @Override
    String toString();
}
