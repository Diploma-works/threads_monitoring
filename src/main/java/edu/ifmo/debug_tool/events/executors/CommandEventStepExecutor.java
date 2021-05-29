package edu.ifmo.debug_tool.events.executors;

import edu.ifmo.debug_tool.Main;
import edu.ifmo.debug_tool.events.steps.CommandEventExecutionStep;

public class CommandEventStepExecutor implements EventStepExecutor<CommandEventExecutionStep> {

    @Override
    public void execute(CommandEventExecutionStep commandEventExecutionStep) {
        if ("stop".equals((String) commandEventExecutionStep.getContent())) {
            Main.isWorking = false;
        }
    }

}
