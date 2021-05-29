package edu.ifmo.debug_tool.metrics;

import edu.ifmo.debug_tool.properties.PropertiesContainer;

public final class MetricsContainer {

    public static final MetricsCollection METRICS_COLLECTION = new MetricsCollection(PropertiesContainer.getInstance().getMetricsInMemoryAmount());

    private static final MetricsContainer instance = new MetricsContainer();

    private MetricsContainer() {
    }

    public static MetricsContainer getInstance() {
        return instance;
    }

}
