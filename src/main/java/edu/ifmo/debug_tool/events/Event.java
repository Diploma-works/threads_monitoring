package edu.ifmo.debug_tool.events;

import edu.ifmo.debug_tool.events.steps.AbstractEventExecutionStep;

import java.util.*;

public final class Event {

    private final String name;
    private final int priority;

    private final List<EventCondition> conditions = new ArrayList<>();
    private final Queue<AbstractEventExecutionStep> steps = new PriorityQueue<>(new StepComparator());

    private static class StepComparator implements Comparator<AbstractEventExecutionStep> {
        @Override
        public int compare(AbstractEventExecutionStep o1, AbstractEventExecutionStep o2) {
            return o1.getOrderId() - o2.getOrderId();
        }
    }

    public Event(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public void setConditions(Collection<EventCondition> conditions) {
        this.conditions.addAll(conditions);
    }

    public void setSteps(Collection<AbstractEventExecutionStep> steps) {
        this.steps.addAll(steps);
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public List<EventCondition> getConditions() {
        return conditions;
    }

    public Queue<AbstractEventExecutionStep> getSteps() {
        return steps;
    }

}
