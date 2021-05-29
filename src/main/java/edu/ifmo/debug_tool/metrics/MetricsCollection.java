package edu.ifmo.debug_tool.metrics;

import java.util.ArrayList;

public class MetricsCollection {

    private final MetricsNode[] nodes;
    private int currentNodeId = 0;
    private int currentSize = 0;

    public MetricsCollection(int length) {
        nodes = new MetricsNode[length];
    }

    public void append(MetricsNode node) {
        synchronized (nodes) {
            if (currentSize < nodes.length) {
                currentSize++;
            }

            if (currentNodeId == nodes.length) {
                currentNodeId = 0;
            }

            nodes[currentNodeId] = node;
            currentNodeId++;
        }
    }

    public MetricsNode[] getLast(int amount) {
        synchronized (nodes) {
            if (amount > currentSize) {
                amount = currentSize;
            }

            if (amount < 0) {
                amount = 1;
            }

            if (amount == 0) {
                return new MetricsNode[0];
            }

            MetricsNode[] metricNodes = new MetricsNode[amount];

            int i = 0;
            int ci = currentNodeId - 1;
            while (i < amount) {
                if (ci < 0) {
                    ci = nodes.length - 1;
                }

                if (nodes[ci] != null) {
                    metricNodes[i] = nodes[ci];
                    i++;
                }
                ci--;
            }

            return metricNodes;
        }
    }

    public MetricsNode[] getLast(long timestamp, int amount) {
        ArrayList<MetricsNode> nodeArrayList = new ArrayList<>();

        int ci = currentNodeId - 1;
        boolean isLower = false;
        boolean repeat = false;
        while (!isLower) {
            if (ci < 0) {
                ci = nodes.length - 1;
                repeat = true;
            }

            if (repeat && ci == currentNodeId - 1) {
                break;
            }

            if (nodes[ci] != null) {
                if (nodes[ci].timestamp < timestamp || nodeArrayList.size() >= amount) {
                    isLower = true;
                } else {
                    nodeArrayList.add(nodes[ci]);
                }
            }
            ci--;
        }

        return nodeArrayList.toArray(new MetricsNode[]{});
    }

}
