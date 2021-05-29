package edu.ifmo.debug_tool.events.executors;

import edu.ifmo.debug_tool.events.StringContentPropagator;
import edu.ifmo.debug_tool.events.steps.ExecuteEventExecutionStep;

import java.lang.management.ThreadInfo;

public class ExecuteEventStepExecutor implements EventStepExecutor<ExecuteEventExecutionStep> {

    private final ThreadInfo threadInfo;

    public ExecuteEventStepExecutor(ThreadInfo threadInfo) {
        this.threadInfo = threadInfo;
    }

    @Override
    public void execute(ExecuteEventExecutionStep eventExecutionStep) throws Exception {
        String preparedArgumentsArray = new StringContentPropagator().propagate(eventExecutionStep.getArgs(), threadInfo);

        Runtime.getRuntime().exec(eventExecutionStep.getContent() + " " + preparedArgumentsArray).waitFor();
    }

}
