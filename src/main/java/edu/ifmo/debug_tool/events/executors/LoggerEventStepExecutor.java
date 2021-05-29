package edu.ifmo.debug_tool.events.executors;

import edu.ifmo.debug_tool.events.steps.LoggingEventExecutionStep;
import edu.ifmo.debug_tool.properties.PropertiesContainer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerEventStepExecutor implements EventStepExecutor<LoggingEventExecutionStep> {

    private final String content;

    public LoggerEventStepExecutor(String content) {
        this.content = content;
    }

    @Override
    public void execute(LoggingEventExecutionStep loggingEventExecutionStep) throws IOException {
        String logFileName = loggingEventExecutionStep.getLogFileName();

        if (!PropertiesContainer.LOG_FILES_MONITORS_SET.containsKey(logFileName)) {
            PropertiesContainer.LOG_FILES_MONITORS_SET.put(logFileName, new Object());
        }

        synchronized (PropertiesContainer.LOG_FILES_MONITORS_SET.get(logFileName)) {
            try (
                    BufferedWriter writer = Files.newBufferedWriter(
                            Paths.get(logFileName),
                            StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND
                    )
            ) {
                writer.write(String.format("%s\t%s\n", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()), content));
            }
        }
    }

}
