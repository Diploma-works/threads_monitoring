package edu.ifmo.debug_tool.events;

import edu.ifmo.debug_tool.events.executors.ExecuteEventStepExecutor;
import edu.ifmo.debug_tool.events.executors.LoggerEventStepExecutor;
import edu.ifmo.debug_tool.events.steps.AbstractEventExecutionStep;
import edu.ifmo.debug_tool.events.steps.CommandEventExecutionStep;
import edu.ifmo.debug_tool.events.steps.ExecuteEventExecutionStep;
import edu.ifmo.debug_tool.events.steps.LoggingEventExecutionStep;
import edu.ifmo.debug_tool.metrics.MetricsContainer;
import edu.ifmo.debug_tool.metrics.MetricsNode;
import edu.ifmo.debug_tool.properties.PropertiesContainer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventsHandler {

    private static EventsHandler instance;

    private final ScriptEngine scriptEngine;

    private EventsHandler() {
        this.scriptEngine = new ScriptEngineManager().getEngineByName("js");
    }

    public static EventsHandler getInstance() {
        if (instance == null) {
            instance = new EventsHandler();
        }

        return instance;
    }

    public void handle(ThreadInfo[] threadInfos) throws Exception {
        StringContentPropagator contentPropagator = new StringContentPropagator();

        List<ThreadInfo> threadInfosToStoreList = new ArrayList<>();

        Map<ThreadInfo, List<String>> eventsNamesMap = new HashMap<>();
        int eventsHappened = 0;

        for (ThreadInfo threadInfo : threadInfos) {
            eventsNamesMap.put(threadInfo, new ArrayList<>());

            for (Event event : PropertiesContainer.USER_EVENTS_LIST) {
                for (EventCondition condition : event.getConditions()) {

                    String preparedConditionContent = contentPropagator.propagate(condition.getContent(), threadInfo);

                    boolean result = (boolean) scriptEngine.eval(preparedConditionContent);

                    if (result) {
                        Map<Long, Integer> eventsAmountMap = PropertiesContainer.THREADS_EVENTS_AMOUNT_MAP;

                        if (eventsAmountMap.containsKey(threadInfo.getThreadId())) {
                            eventsAmountMap.put(threadInfo.getThreadId(), eventsAmountMap.get(threadInfo.getThreadId()) + 1);
                        } else {
                            eventsAmountMap.put(threadInfo.getThreadId(), 1);
                        }

                        for (AbstractEventExecutionStep eventExecutionStep : event.getSteps()) {
                            switch (eventExecutionStep.getType()) {
                                case LOG:
                                    LoggingEventExecutionStep loggingEventExecutionStep = (LoggingEventExecutionStep) eventExecutionStep;
                                    String preparedContent = contentPropagator.propagate(eventExecutionStep.getContent(), threadInfo);
                                    new LoggerEventStepExecutor(preparedContent).execute(loggingEventExecutionStep);
                                    break;
                                case COMMAND:
                                    CommandEventExecutionStep commandEventExecutionStep = (CommandEventExecutionStep) eventExecutionStep;
                                    switch (commandEventExecutionStep.getContent()) {
                                        case "stop":
                                            System.exit(0);
                                            break;
                                        case "store":
                                            eventsHappened++;
                                            eventsNamesMap.get(threadInfo).add(event.getName());
                                            threadInfosToStoreList.add(threadInfo);
                                            break;
                                    }
                                    break;
                                case EXECUTE:
                                    ExecuteEventExecutionStep executeEventExecutionStep = (ExecuteEventExecutionStep) eventExecutionStep;
                                    ExecuteEventStepExecutor executeEventStepExecutor = new ExecuteEventStepExecutor(threadInfo);
                                    executeEventStepExecutor.execute(executeEventExecutionStep);
                                    break;
                            }
                        }
                    }
                }
            }
        }

        if (eventsHappened > 0) {
            try {
                MetricsNode metricsNode = new MetricsNode(eventsNamesMap, threadInfosToStoreList.toArray(new ThreadInfo[]{}));
                MetricsContainer.METRICS_COLLECTION.append(metricsNode);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
