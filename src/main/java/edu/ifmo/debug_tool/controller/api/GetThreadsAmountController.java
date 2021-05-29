package edu.ifmo.debug_tool.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import edu.ifmo.debug_tool.metrics.MetricsContainer;
import edu.ifmo.debug_tool.metrics.MetricsNode;

import java.lang.management.ThreadInfo;

public class GetThreadsAmountController extends AbstractApiController {

    private static class MetricsAmountWrapper {
        long totalAmount = 0;
        long newAmount = 0;
        long runnableAmount = 0;
        long blockedAmount = 0;
        long waitingAmount = 0;
        long timedWaitingAmount = 0;
        long terminatedAmount = 0;

        long timestamp = 0;

        public MetricsAmountWrapper() {
        }

        public long getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(long totalAmount) {
            this.totalAmount = totalAmount;
        }

        public long getNewAmount() {
            return newAmount;
        }

        public void setNewAmount(long newAmount) {
            this.newAmount = newAmount;
        }

        public long getRunnableAmount() {
            return runnableAmount;
        }

        public void setRunnableAmount(long runnableAmount) {
            this.runnableAmount = runnableAmount;
        }

        public long getBlockedAmount() {
            return blockedAmount;
        }

        public void setBlockedAmount(long blockedAmount) {
            this.blockedAmount = blockedAmount;
        }

        public long getWaitingAmount() {
            return waitingAmount;
        }

        public void setWaitingAmount(long waitingAmount) {
            this.waitingAmount = waitingAmount;
        }

        public long getTimedWaitingAmount() {
            return timedWaitingAmount;
        }

        public void setTimedWaitingAmount(long timedWaitingAmount) {
            this.timedWaitingAmount = timedWaitingAmount;
        }

        public long getTerminatedAmount() {
            return terminatedAmount;
        }

        public void setTerminatedAmount(long terminatedAmount) {
            this.terminatedAmount = terminatedAmount;
        }
    }

    public GetThreadsAmountController(HttpExchange exchange) {
        super(exchange);
    }

    @Override
    protected void doJob() throws Exception {
        MetricsNode[] nodes = MetricsContainer.METRICS_COLLECTION.getLast(1);
        if (nodes.length == 0) {
            throw new RuntimeException("404");
        }

        MetricsAmountWrapper metricsAmountWrapper = new MetricsAmountWrapper();
        metricsAmountWrapper.timestamp = System.currentTimeMillis();

        MetricsNode lastNode = nodes[0];

        metricsAmountWrapper.totalAmount = lastNode.getThreadInfos().length;

        for (ThreadInfo threadInfo : lastNode.getThreadInfos()) {
            switch (threadInfo.getThreadState()) {
                case NEW:
                    metricsAmountWrapper.newAmount++;
                    break;
                case RUNNABLE:
                    metricsAmountWrapper.runnableAmount++;
                    break;
                case BLOCKED:
                    metricsAmountWrapper.blockedAmount++;
                    break;
                case WAITING:
                    metricsAmountWrapper.waitingAmount++;
                    break;
                case TIMED_WAITING:
                    metricsAmountWrapper.timedWaitingAmount++;
                    break;
                case TERMINATED:
                    metricsAmountWrapper.terminatedAmount++;
                    break;
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        result = objectMapper.writeValueAsString(metricsAmountWrapper);

    }

}
