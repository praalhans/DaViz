package com.aexiz.daviz.simulation.algorithm.information.message;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;

public abstract class AbstractMessageInformation extends AbstractInformation implements MessageInformation {
    @Override
    public abstract boolean equals(Object obj);
}
