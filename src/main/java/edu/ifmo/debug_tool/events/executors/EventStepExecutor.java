package edu.ifmo.debug_tool.events.executors;

import edu.ifmo.debug_tool.events.steps.AbstractEventExecutionStep;

public interface EventStepExecutor<T extends AbstractEventExecutionStep> {
    void execute(T eventExecutionStep) throws Exception;
}
