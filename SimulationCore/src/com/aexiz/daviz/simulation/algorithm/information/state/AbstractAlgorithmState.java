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

    /**
     * Specify the properties to be set in the Information windows.
     * <p>
     * This method implementation must assign to {@link AbstractAlgorithmState#properties} a {@link Map} that contains a
     * {@link String} as key and a {@link Object} as value. There are three possible classes that can be used as values:
     *
     * <ul>
     *     <li> A {@link String} for simple properties.</li>
     *     <li> A nested {@link Map}<String, Object> for compound properties. /li>
     *     <li> A defined {@link PropertyVisitor} that already specifies the properties. </li>
     * </ul>
     * <p>
     * The following helper methods can be used to make maps:
     * <ul>
     *     <li> {@link AbstractAlgorithmState#makeNodesProperty} </li>
     * </ul>
     * <p>
     * The properties will be translated to a format known to the UI in {@link AbstractAlgorithmState#buildPropertiesHelper}.
     * <p>
     * Implementation example:
     * <pre>
     *  properties = Map.of(
     *      "Has token?", "false",
     *      "State", "Initiator",
     *      "Neighbours", makeNodesProperty(neighbours)
     *  );
     * </pre>
     */
    abstract public void makeProperties();

    @SuppressWarnings("unchecked")
    private void buildPropertiesHelper(PropertyBuilder builder, Map<String, Object> properties) {
        properties.forEach(
                (key, value) -> {
                    if (value instanceof String) builder.simpleProperty(key, (String) value);
                    else if (value instanceof PropertyVisitor) builder.compoundProperty(key, (PropertyVisitor) value);
                    else if (value instanceof Map) builder.compoundProperty(key, nestedBuilder -> {
                        buildPropertiesHelper(nestedBuilder, (Map<String, Object>) value);
                    });
                    else throw new IllegalArgumentException("Unknown property type");
                }
        );
    }

}
