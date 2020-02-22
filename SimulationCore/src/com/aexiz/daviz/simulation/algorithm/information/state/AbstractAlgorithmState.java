package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAlgorithmState implements StateInformation {
    protected Map<String, Object> properties;

    static protected Map<String, String> makeNodesProperty(List<Channel> channels) {
        Map<String, String> map = new HashMap<>();
        int channelsSize = channels.size();
        map.put("", channelsSize + " node" + (channelsSize != 1 ? "s" : ""));
        for (int i = 0; i < channelsSize; i++) {
            map.put((i + 1) + ":", channels.get(i).to.getLabel());
        }
        return map;
    }

    @Override
    public abstract String toString();

    @Override
    public void buildProperties(PropertyBuilder builder) {
        buildPropertiesHelper(builder, properties);
    }

    @SuppressWarnings("unchecked")
    private void buildPropertiesHelper(PropertyBuilder builder, Map<String, Object> properties) {
        properties.forEach(
                (key, value) -> {
                    if (value instanceof String) builder.simpleProperty(key, (String) value);
                    else if (value instanceof PropertyVisitor) builder.compoundProperty(key, (PropertyVisitor) value);
                    else if (value instanceof Map) builder.compoundProperty(key, nestedBuilder -> {
                        buildPropertiesHelper(nestedBuilder, (Map<String, Object>) value);
                    });
                }
        );
    }

}
