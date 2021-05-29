package edu.ifmo.debug_tool.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import edu.ifmo.debug_tool.metrics.MetricsContainer;
import edu.ifmo.debug_tool.metrics.MetricsNode;

import java.util.ArrayList;
import java.util.List;

public class GetThreadInfosByThreadIdAndTimestamp extends AbstractApiController {

    public GetThreadInfosByThreadIdAndTimestamp(HttpExchange exchange) {
        super(exchange);
    }

    @Override
    protected void doJob() throws Exception {

        long timestamp;
        int threadId;

        try {
            timestamp = Long.parseLong(queryParametersMap.get("timestamp"));
            threadId = Integer.parseInt(queryParametersMap.get("id"));
        } catch (NumberFormatException e) {
            throw new RuntimeException("400");
        }

        MetricsNode[] nodes = MetricsContainer.METRICS_COLLECTION.getLast(timestamp, 30);

        if (nodes.length == 0) {
            throw new RuntimeException("404");
        }

        List<MetricsNode.ThreadInfoWrapper> threadInfoWrapperList = new ArrayList<>();
        for (MetricsNode node : nodes) {
            for (MetricsNode.ThreadInfoWrapper threadInfoWrapper : node.getThreadInfoWrappers()) {
                if (threadInfoWrapper.getThreadInfo().getThreadId() == threadId) {
                    threadInfoWrapperList.add(threadInfoWrapper);
                }
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        result = objectMapper.writeValueAsString(threadInfoWrapperList.toArray(new MetricsNode.ThreadInfoWrapper[]{}));

    }
}
