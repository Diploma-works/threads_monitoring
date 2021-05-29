package edu.ifmo.debug_tool.events;

import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;

public class StringContentPropagator {

    public String propagate(String initialString, ThreadInfo threadInfo) {
        HashMap<String, String> mapToSubstitute = (HashMap<String, String>) getSubstitutionThreadInfosMap(threadInfo);

        StringBuilder conditionStringBuilder = new StringBuilder(initialString);

        String lp = "%{";
        String rp = "}%";

        int li = conditionStringBuilder.indexOf(lp);
        int ri = conditionStringBuilder.indexOf(rp);

        while (li >= 0) {
            String varToReplace = conditionStringBuilder.substring(li + 2, ri);

            if (!mapToSubstitute.containsKey(varToReplace)) {
                throw new RuntimeException("No such variable");
            }

            conditionStringBuilder.replace(li, ri + 2, mapToSubstitute.get(varToReplace));

            li = conditionStringBuilder.indexOf(lp);
            ri = conditionStringBuilder.indexOf(rp);
        }

        return conditionStringBuilder.toString();
    }

    private Map<String, String> getSubstitutionThreadInfosMap(ThreadInfo threadInfo) {
        HashMap<String, String> result = new HashMap<>();

        result.put("ThreadName", threadInfo.getThreadName());
        result.put("ThreadId", String.valueOf(threadInfo.getThreadId()));
        result.put("BlockedTime", String.valueOf(threadInfo.getBlockedTime()));
        result.put("BlockedCount", String.valueOf(threadInfo.getBlockedCount()));
        result.put("WaitedTime", String.valueOf(threadInfo.getWaitedTime()));
        result.put("WaitedCount", String.valueOf(threadInfo.getWaitedCount()));
        result.put("LockName", String.valueOf(threadInfo.getLockName()));
        result.put("LockOwnerName", String.valueOf(threadInfo.getLockOwnerName()));
        result.put("InNative", String.valueOf(threadInfo.isInNative()));
        result.put("Suspended", String.valueOf(threadInfo.isSuspended()));
        result.put("ThreadState", String.valueOf(threadInfo.getThreadState()));

        return result;
    }

}
