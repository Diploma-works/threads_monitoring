package edu.ifmo.debug_tool;

import edu.ifmo.debug_tool.controller.WebServer;
import edu.ifmo.debug_tool.metrics.MetricsCollectorThread;
import edu.ifmo.debug_tool.properties.PropertiesContainer;

public class Main {
    private static boolean isConsoleMode = true;
    private static boolean isHelpMessageToBeShown = false;
    private static boolean isIncorrectValuesGiven = false;

    private static String propertiesFileName = "properties.xml";

    public static boolean isWorking = true;


    public static void main(String[] args) {
        try {
            readArgs(args);

            PropertiesContainer.init(propertiesFileName);

            if (isIncorrectValuesGiven) {
                System.err.println(PropertiesContainer.RESOURCE_BUNDLE.getString("ARGS_INCORRECT_EXCEPTION"));
                System.exit(1);
            }

            if (isHelpMessageToBeShown) {
                System.exit(0);
            }

            if (isConsoleMode) {
                runConsoleMode();
            } else {
                runWebMode();
            }

            while (isWorking) {
                Thread.sleep(1L);
            }

            PropertiesContainer.EXECUTOR_SERVICE.shutdown();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void readArgs(String[] args) {
        for (String arg : args) {
            String[] prop = arg.split("=");

            if (prop[0].length() == 0) {
                isIncorrectValuesGiven = true;
                break;
            }

            switch (prop[0]) {
                case "mode":
                    if (prop[1].equals("web") || prop[1].equals("w")) {
                        isConsoleMode = false;
                    }
                    break;
                case "help":
                    isHelpMessageToBeShown = true;
                    break;
                case "x_properties":
                    propertiesFileName = prop[1];
                    break;
                default:
                    isIncorrectValuesGiven = true;
            }
        }
    }

    private static void runConsoleMode() {
        try {
            PropertiesContainer.EXECUTOR_SERVICE.execute(new MetricsCollectorThread());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Main.isWorking = false;
        }
    }

    private static void runWebMode() throws Exception {
        try {
            PropertiesContainer.EXECUTOR_SERVICE.execute(new MetricsCollectorThread());

            WebServer server = new WebServer();
            server.create();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Main.isWorking = false;
        }
    }
}
