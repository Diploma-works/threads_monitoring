package edu.ifmo.debug_tool.events.steps;

import edu.ifmo.debug_tool.events.EventExecutionStepType;

public abstract class AbstractEventExecutionStep {

    protected final EventExecutionStepType type;

    protected int orderId;
    protected String content;

    public AbstractEventExecutionStep(int orderId, EventExecutionStepType type, String content) {
        this.orderId = orderId;
        this.type = type;
        this.content = content;
    }

    public int getOrderId() {
        return orderId;
    }

    public EventExecutionStepType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

}
