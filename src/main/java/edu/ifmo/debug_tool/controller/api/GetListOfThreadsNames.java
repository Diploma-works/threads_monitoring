package edu.ifmo.debug_tool.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import edu.ifmo.debug_tool.metrics.MetricsContainer;
import edu.ifmo.debug_tool.metrics.MetricsNode;
import edu.ifmo.debug_tool.properties.PropertiesContainer;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;

public class GetListOfThreadsNames extends AbstractApiController {

    private static class ThreadNameWrapper {
        long id;
        long eventsAmount;
        String name;
        String state;

        public ThreadNameWrapper() {
        }

        public long getEventsAmount() {
            return eventsAmount;
        }

        public void setEventsAmount(long eventsAmount) {
            this.eventsAmount = eventsAmount;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    private static class ListOfThreadsNamesWrapper {
        long timestamp;
        ThreadNameWrapper[] threadNameWrappers;

        public ListOfThreadsNamesWrapper() {
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public ThreadNameWrapper[] getThreadNameWrappers() {
            return threadNameWrappers;
        }

        public void setThreadNameWrappers(ThreadNameWrapper[] threadNameWrappers) {
            this.threadNameWrappers = threadNameWrappers;
        }
    }

    public GetListOfThreadsNames(HttpExchange exchange) {
        super(exchange);
    }

    @Override
    protected void doJob() throws Exception {
        MetricsNode[] nodes = MetricsContainer.METRICS_COLLECTION.getLast(1);

        if (nodes.length == 0) {
            throw new RuntimeException("404");
        }

        MetricsNode lastNode = nodes[0];

        ListOfThreadsNamesWrapper listOfThreadsNamesWrapper = new ListOfThreadsNamesWrapper();
        List<ThreadNameWrapper> nameWrapperList = new ArrayList<>();

        for (ThreadInfo threadInfo : lastNode.getThreadInfos()) {
            ThreadNameWrapper nameWrapper = new ThreadNameWrapper();

            nameWrapper.setName(threadInfo.getThreadName());
            nameWrapper.setId(threadInfo.getThreadId());
            nameWrapper.setState(threadInfo.getThreadState().name());

            nameWrapper.setEventsAmount(PropertiesContainer.THREADS_EVENTS_AMOUNT_MAP.getOrDefault(threadInfo.getThreadId(), 0));

            nameWrapperList.add(nameWrapper);
        }

        listOfThreadsNamesWrapper.setThreadNameWrappers(nameWrapperList.toArray(new ThreadNameWrapper[]{}));
        listOfThreadsNamesWrapper.setTimestamp(System.currentTimeMillis());

        ObjectMapper objectMapper = new ObjectMapper();
        result = objectMapper.writeValueAsString(listOfThreadsNamesWrapper);

    }
}
