package edu.ifmo.debug_tool.events.steps;

import edu.ifmo.debug_tool.events.EventExecutionStepType;
import edu.ifmo.debug_tool.metrics.MetricsContainer;

public class CommandEventExecutionStep extends AbstractEventExecutionStep{

    public CommandEventExecutionStep(Object content, int orderId) {
        super(orderId, EventExecutionStepType.COMMAND, (String) content);
    }

}
