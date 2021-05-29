package edu.ifmo.debug_tool.properties;

import edu.ifmo.debug_tool.events.Event;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PropertiesContainer {

    public static final List<Event> USER_EVENTS_LIST = new ArrayList<>();
    public static final Map<String, Object> PROPERTIES_MAP = new HashMap<>();
    public static final String WEB_PREFIX = "/debug-tool";

    public static final Map<String, Object> LOG_FILES_MONITORS_SET = new HashMap<>();
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);
    public static final Locale APP_LOCALE = Locale.getDefault();
    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("i18n.ProgramResources", APP_LOCALE);
    public static final Map<Long, Integer> THREADS_EVENTS_AMOUNT_MAP = new HashMap<>();


    private static PropertiesContainer instance;

    private String jvmConnectionHostName;
    private String jvmConnectionUrl;
    private Integer jvmConnectionPort;
    private Integer jvmConnectionPid;
    private String jvmConnectionAuth;
    private String jvmConnectionSsl;
    private Integer metricsFetchPeriod;
    private Integer metricsInMemoryAmount;
    private String webHostName;
    private Integer webPort;


    public PropertiesContainer() {
    }

    public static void init(final String propertiesFileName) {
        instance = new PropertiesContainer();
        instance.readProperties(new XMLPropertiesReader(propertiesFileName));
    }

    public static PropertiesContainer getInstance() {
        return instance;
    }

    private void readProperties(PropertiesReader reader) {
        reader.read(PROPERTIES_MAP);

        jvmConnectionAuth = (String) PROPERTIES_MAP.get("properties.connection.auth");
        jvmConnectionUrl = (String) PROPERTIES_MAP.get("properties.connection.url");
        jvmConnectionHostName = (String) PROPERTIES_MAP.get("properties.connection.host");
        jvmConnectionSsl = (String) PROPERTIES_MAP.get("properties.connection.ssl");
        jvmConnectionPid = (Integer) PROPERTIES_MAP.get("properties.connection.pid");
        jvmConnectionPort = (Integer) PROPERTIES_MAP.get("properties.connection.port");

        webHostName = (String) PROPERTIES_MAP.get("properties.web-mode.host-name");
        webPort = (Integer) PROPERTIES_MAP.get("properties.web-mode.port");

        metricsFetchPeriod = (Integer) PROPERTIES_MAP.get("metrics.period");
        metricsInMemoryAmount = (Integer) PROPERTIES_MAP.get("metrics.in-memory-amount");

        USER_EVENTS_LIST.addAll((List) PROPERTIES_MAP.get("properties.events"));
    }

    public String getJvmConnectionHostName() {
        return jvmConnectionHostName;
    }

    public String getJvmConnectionUrl() {
        return jvmConnectionUrl;
    }

    public Integer getJvmConnectionPort() {
        return jvmConnectionPort;
    }

    public Integer getJvmConnectionPid() {
        return jvmConnectionPid;
    }

    public String getJvmConnectionAuth() {
        return jvmConnectionAuth;
    }

    public String getJvmConnectionSsl() {
        return jvmConnectionSsl;
    }

    public Integer getMetricsFetchPeriod() {
        return metricsFetchPeriod;
    }

    public Integer getMetricsInMemoryAmount() {
        return metricsInMemoryAmount;
    }

    public String getWebHostName() {
        return webHostName;
    }

    public Integer getWebPort() {
        return webPort;
    }

}
