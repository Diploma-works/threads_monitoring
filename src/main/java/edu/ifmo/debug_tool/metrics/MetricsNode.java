package edu.ifmo.debug_tool.metrics;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetricsNode {

    public static class ThreadInfoWrapper {
        ThreadInfo threadInfo;
        long timestamp;
        String[] eventsNames;

        public ThreadInfoWrapper() {
        }

        public ThreadInfo getThreadInfo() {
            return threadInfo;
        }

        public void setThreadInfo(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        public String[] getEventsNames() {
            return eventsNames;
        }

        public void setEventsNames(String[] eventsNames) {
            this.eventsNames = eventsNames;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    ThreadInfoWrapper[] threadInfoWrappers;
    long timestamp;

    public MetricsNode() {
    }

    public MetricsNode(Map<ThreadInfo, List<String>> namesMap, ThreadInfo[] threadInfos) {
//        if (namesMap.size() != threadInfos.length) {
//            throw new RuntimeException("Len of eventsNames != len of threadInfos!");
//        }

        this.timestamp = System.currentTimeMillis();

        List<ThreadInfoWrapper> wrappers = new ArrayList<>();
        for (ThreadInfo threadInfo : threadInfos) {
            ThreadInfoWrapper wrapper = new ThreadInfoWrapper();
            wrapper.setThreadInfo(threadInfo);
            wrapper.setEventsNames(namesMap.get(threadInfo).toArray(new String[] {}));
            wrapper.setTimestamp(this.timestamp);

            wrappers.add(wrapper);
        }

        this.threadInfoWrappers = wrappers.toArray(new ThreadInfoWrapper[]{});
    }

    public ThreadInfoWrapper[] getThreadInfoWrappers() {
        return threadInfoWrappers;
    }

    public ThreadInfo[] getThreadInfos() {
        List<ThreadInfo> threadInfos = new ArrayList<>();
        for (ThreadInfoWrapper wrapper : threadInfoWrappers) {
            threadInfos.add(wrapper.getThreadInfo());
        }
        return threadInfos.toArray(new ThreadInfo[]{});
    }

    public long getTimestamp() {
        return timestamp;
    }
}
