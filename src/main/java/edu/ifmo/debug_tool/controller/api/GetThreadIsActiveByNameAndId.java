package edu.ifmo.debug_tool.controller.api;

import com.sun.net.httpserver.HttpExchange;
import edu.ifmo.debug_tool.metrics.MetricsContainer;
import edu.ifmo.debug_tool.metrics.MetricsNode;

import java.lang.management.ThreadInfo;

public class GetThreadIsActiveByNameAndId extends AbstractApiController {

    public GetThreadIsActiveByNameAndId(HttpExchange exchange) {
        super(exchange);
    }

    @Override
    protected void doJob() throws Exception {

        String threadName = queryParametersMap.get("name");
        long threadId;
        if (threadName == null) {
            throw new RuntimeException("400");
        }

        try {
            threadId = Long.parseLong(queryParametersMap.get("id"));
        } catch (NumberFormatException e) {
            throw new RuntimeException("400");
        }

        MetricsNode[] nodes = MetricsContainer.METRICS_COLLECTION.getLast(1);
        if (nodes.length != 0) {
            for (ThreadInfo threadInfo : nodes[0].getThreadInfos()) {
                if (threadInfo.getThreadId() == threadId && threadInfo.getThreadName().equals(threadName)) {
                    result = "1";
                    return;
                }
            }
        }

        throw new RuntimeException("404");
    }
}
