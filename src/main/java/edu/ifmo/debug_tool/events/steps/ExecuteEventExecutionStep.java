package edu.ifmo.debug_tool.events.steps;

import edu.ifmo.debug_tool.events.EventExecutionStepType;

public class ExecuteEventExecutionStep extends AbstractEventExecutionStep {

    private final String args;

    public ExecuteEventExecutionStep(int orderId, String content, String args) {
        super(orderId, EventExecutionStepType.EXECUTE, content);

        this.args = args;
    }

    public String getArgs() {
        return args;
    }

}
