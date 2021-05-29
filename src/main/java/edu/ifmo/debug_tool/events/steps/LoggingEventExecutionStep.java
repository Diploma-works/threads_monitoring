package edu.ifmo.debug_tool.events.steps;

import edu.ifmo.debug_tool.events.EventExecutionStepType;

public class LoggingEventExecutionStep extends AbstractEventExecutionStep {

    private final String logFileName;

    public LoggingEventExecutionStep(int orderId, String logFileName, String content) {
        super(orderId, EventExecutionStepType.LOG, content);

        this.logFileName = logFileName;
    }

    public String getLogFileName() {
        return logFileName;
    }

}
