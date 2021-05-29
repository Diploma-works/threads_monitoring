package edu.ifmo.debug_tool.metrics;

import edu.ifmo.debug_tool.Main;
import edu.ifmo.debug_tool.events.EventsHandler;
import edu.ifmo.debug_tool.properties.PropertiesContainer;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import static java.lang.management.ManagementFactory.THREAD_MXBEAN_NAME;

public class MetricsCollectorThread implements Runnable {

    private javax.management.remote.JMXConnector jmxConnector;
    private final ThreadMXBean threadMXBean;

    public MetricsCollectorThread() throws IOException {
        String url = String.format(
                "service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi",
                PropertiesContainer.getInstance().getJvmConnectionHostName(),
                PropertiesContainer.getInstance().getJvmConnectionPort()
        );
        JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
        javax.management.remote.JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        threadMXBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection, THREAD_MXBEAN_NAME, ThreadMXBean.class);
    }

    @Override
    protected void finalize() throws Throwable {
        jmxConnector.close();
        super.finalize();
    }

    @Override
    public void run() {
        while (Main.isWorking) {
            try {
                ThreadInfo[] tinfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);

                EventsHandler.getInstance().handle(tinfos);

                for (int i = 0; i < PropertiesContainer.getInstance().getMetricsFetchPeriod(); i++) {
                    Thread.sleep(1L);
                }

            } catch (Exception e) {
                e.printStackTrace(System.err);
                Main.isWorking = false;
            }
        }
    }
}
