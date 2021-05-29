package edu.ifmo.debug_tool.properties;

import edu.ifmo.debug_tool.events.Event;
import edu.ifmo.debug_tool.events.EventCondition;
import edu.ifmo.debug_tool.events.steps.AbstractEventExecutionStep;
import edu.ifmo.debug_tool.events.steps.CommandEventExecutionStep;
import edu.ifmo.debug_tool.events.steps.ExecuteEventExecutionStep;
import edu.ifmo.debug_tool.events.steps.LoggingEventExecutionStep;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class XMLPropertiesReader implements PropertiesReader {

    private final String fileName;

    public XMLPropertiesReader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void read(Map<String, Object> propertiesMap) {
        File file = new File(fileName);
        parseDom(file, propertiesMap);
    }

    private void parseDom(File file, Map<String, Object> propertiesMap) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        DocumentBuilder builder;
        Document document;

        try {
            builder = builderFactory.newDocumentBuilder();
            document = builder.parse(file);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            parseWebModeData(document, xPath, propertiesMap);
            parseConnectionData(document, xPath, propertiesMap);
            parseEventsData(document, xPath, propertiesMap);
            parseMetricsData(document, xPath, propertiesMap);

        } catch (Exception e) {
            System.err.println(PropertiesContainer.RESOURCE_BUNDLE.getString("PROPERTIES_FILE_CAN_NOT_BE_READ_EXCEPTION"));
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private void parseWebModeData(Document document, XPath xPath, Map<String, Object> propertiesMap) throws Exception {
        XPathExpression xPathExpression = xPath.compile("/properties/web-mode/host-name/text()");
        String webHostName = (String) xPathExpression.evaluate(document, XPathConstants.STRING);
        propertiesMap.put("properties.web-mode.host-name", webHostName);

        xPathExpression = xPath.compile("/properties/web-mode/port/text()");
        Integer webPort = Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));
        propertiesMap.put("properties.web-mode.port", webPort);
    }

    private void parseMetricsData(Document document, XPath xPath, Map<String, Object> propertiesMap) throws Exception {
        XPathExpression xPathExpression = xPath.compile("/properties/metrics/period/text()");
        Integer metricsPeriod = Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));
        propertiesMap.put("metrics.period", metricsPeriod);

        xPathExpression = xPath.compile("/properties/metrics/in-memory-amount/text()");
        Integer metricsInMemoryAmount = Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));
        propertiesMap.put("metrics.in-memory-amount", metricsInMemoryAmount);
    }

    private void parseEventsData(Document document, XPath xPath, Map<String, Object> propertiesMap) throws Exception {
        XPathExpression xPathExpression = xPath.compile("count(/properties/events/event)");
        int eventsCount = Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));

        Collection<Event> events = new LinkedList<>();

        for (int nodeId = 1; nodeId <= eventsCount; nodeId++) {
            events.add(parseEvent(document, xPath, nodeId));
        }

        propertiesMap.put("properties.events", events);
    }

    private Event parseEvent(Document document, XPath xPath, int eventId) throws Exception {
        XPathExpression xPathExpression = xPath.compile(String.format("/properties/events/event[%s]/name/text()", eventId));
        String eventName = (String) xPathExpression.evaluate(document, XPathConstants.STRING);

        xPathExpression = xPath.compile(String.format("/properties/events/event[%s]/priority/text()", eventId));
        int eventPriority = Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));

        Collection<AbstractEventExecutionStep> steps = new ArrayList<>();
        Collection<EventCondition> conditions = new ArrayList<>();

        xPathExpression = xPath.compile(String.format("count(/properties/events/event[%s]/steps/step)", eventId));
        int stepsCount = Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));

        xPathExpression = xPath.compile(String.format("count(/properties/events/event[%s]/conditions/condition)", eventId));
        int conditionsCount = Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));

        for (int i = 1; i <= stepsCount; i++) {
            steps.add(parseEventStep(document, xPath, eventId, i));
        }

        for (int i = 1; i <= conditionsCount; i++) {
            conditions.add(parseEventCondition(document, xPath, eventId, i));
        }

        Event event = new Event(eventName, eventPriority);
        event.setConditions(conditions);
        event.setSteps(steps);

        return event;
    }

    private EventCondition parseEventCondition(Document document, XPath xPath, int eventId, int conditionId) throws Exception {
        XPathExpression xPathExpression = xPath.compile(String.format("/properties/events/event[%s]/conditions/condition[%s]/text()", eventId, conditionId));
        String conditionContent = (String) xPathExpression.evaluate(document, XPathConstants.STRING);

        return new EventCondition(conditionContent);
    }

    private AbstractEventExecutionStep parseEventStep(Document document, XPath xPath, int eventId, int stepId) throws Exception {
        XPathExpression xPathExpression = xPath.compile(String.format("/properties/events/event[%s]/steps/step[%s]/order_id/text()", eventId, stepId));
        int stepOrderId = Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));

        xPathExpression = xPath.compile(String.format("/properties/events/event[%s]/steps/step[%s]/type/text()", eventId, stepId));
        String stepTypeValue = (String) xPathExpression.evaluate(document, XPathConstants.STRING);

        xPathExpression = xPath.compile(String.format("/properties/events/event[%s]/steps/step[%s]/content/text()", eventId, stepId));
        String stepContent = (String) xPathExpression.evaluate(document, XPathConstants.STRING);

        switch (stepTypeValue) {
            case "log":
                xPathExpression = xPath.compile(String.format("/properties/events/event[%s]/steps/step[%s]/log-file/text()", eventId, stepId));
                String logFileName = (String) xPathExpression.evaluate(document, XPathConstants.STRING);

                return new LoggingEventExecutionStep(stepOrderId, logFileName, stepContent);
            case "command":
                return new CommandEventExecutionStep(stepContent, stepOrderId);
            case "execute":
                xPathExpression = xPath.compile(String.format("/properties/events/event[%s]/steps/step[%s]/args/text()", eventId, stepId));
                String args = (String) xPathExpression.evaluate(document, XPathConstants.STRING);

                return new ExecuteEventExecutionStep(stepOrderId, stepContent, args);
        }

        throw new IOException(String.format("Can not parse step '%s'", stepContent));


    }

    private void parseConnectionData(Document document, XPath xPath, Map<String, Object> propertiesMap) {
        propertiesMap.put("properties.connection.url", getPropertiesConnectionUrl(document, xPath));
        propertiesMap.put("properties.connection.auth", getPropertiesConnectionAuth(document, xPath));
        propertiesMap.put("properties.connection.host", getPropertiesConnectionHost(document, xPath));
        propertiesMap.put("properties.connection.pid", getPropertiesConnectionPid(document, xPath));
        propertiesMap.put("properties.connection.port", getPropertiesConnectionPort(document, xPath));
        propertiesMap.put("properties.connection.ssl", getPropertiesConnectionSSL(document, xPath));
    }

    private String getPropertiesConnectionUrl(Document document, XPath xPath) {
        try {
            XPathExpression xPathExpression = xPath.compile("/properties/connection/url/text()");
            return (String) xPathExpression.evaluate(document, XPathConstants.STRING);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getPropertiesConnectionPort(Document document, XPath xPath) {
        try {
            XPathExpression xPathExpression = xPath.compile("/properties/connection/port/text()");
            return Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getPropertiesConnectionPid(Document document, XPath xPath) {
        try {
            XPathExpression xPathExpression = xPath.compile("/properties/connection/pid/text()");
            return Integer.parseInt((String) xPathExpression.evaluate(document, XPathConstants.STRING));
        } catch (Exception e) {
            return null;
        }
    }

    private String getPropertiesConnectionHost(Document document, XPath xPath) {
        try {
            XPathExpression xPathExpression = xPath.compile("/properties/connection/host/text()");
            return (String) xPathExpression.evaluate(document, XPathConstants.STRING);
        } catch (Exception e) {
            return null;
        }
    }

    private String getPropertiesConnectionAuth(Document document, XPath xPath) {
        try {
            XPathExpression xPathExpression = xPath.compile("/properties/connection/auth/text()");
            return (String) xPathExpression.evaluate(document, XPathConstants.STRING);
        } catch (Exception e) {
            return null;
        }
    }

    private String getPropertiesConnectionSSL(Document document, XPath xPath) {
        try {
            XPathExpression xPathExpression = xPath.compile("/properties/connection/ssl/text()");
            return (String) xPathExpression.evaluate(document, XPathConstants.STRING);
        } catch (Exception e) {
            return null;
        }
    }

}
