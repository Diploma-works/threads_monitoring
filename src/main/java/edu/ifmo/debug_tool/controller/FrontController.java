package edu.ifmo.debug_tool.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.ifmo.debug_tool.properties.PropertiesContainer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FrontController implements HttpHandler {

    private final ApiController apiController;

    private final Map<String, String> propagateMap = new HashMap<>();

    public FrontController() {
        apiController = new ApiController();

        for (String key : PropertiesContainer.RESOURCE_BUNDLE.keySet()) {
            propagateMap.put("#{" + key + "}", PropertiesContainer.RESOURCE_BUNDLE.getString(key));
        }

        String port = String.valueOf(PropertiesContainer.getInstance().getWebPort());
        String host = PropertiesContainer.getInstance().getWebHostName();
        propagateMap.put("#{PORT}", port);
        propagateMap.put("#{HOSTNAME}", host);
        propagateMap.put("#{GUI_URL_PREFIX}", host + ":" + port + PropertiesContainer.WEB_PREFIX);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGet(exchange);
            } else {
                handleError(exchange, 405);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void handleGet(HttpExchange exchange) throws Exception {
        String uri = exchange.getRequestURI().toString().replace(PropertiesContainer.WEB_PREFIX, "");

        if (Pattern.compile("\\/api\\/.*").matcher(uri).matches()) {
            apiController.handle(exchange);
        } else if (uri.contains(".html") || uri.contains(".css") || uri.contains(".js")) {
            String fileName = "/html/error.html";

            if (uri.contains(".html")) {
                fileName = "/html" + uri;
            } else if (uri.contains(".css")) {
                fileName = "/css" + uri;
            } else if (uri.contains(".js")) {
                fileName = "/js" + uri;
            }

            if (fileName.contains("?")) {
                fileName = fileName.split("\\?")[0];
            }

            sendFile("gui" + fileName, exchange, 200);
        } else {
            handleError(exchange, 404);
        }
    }

    private void handleError(HttpExchange exchange, int code) throws Exception {
        sendFile("gui/html/error.html", exchange, code);
    }

    private synchronized void sendHeaders(HttpExchange exchange, int code, long length) throws IOException {
        exchange.sendResponseHeaders(code, length);
    }

    private void sendFile(String name, HttpExchange exchange, int code) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();

        String fileContent = getResourceFileAsString(name);

        if (fileContent == null) {
            fileContent = "";
        }

        fileContent = propagateTemplateSubstitution(fileContent);
        byte[] bs = fileContent.getBytes(StandardCharsets.UTF_8);

        sendHeaders(exchange, code, bs.length);

        outputStream.write(bs);
        outputStream.flush();
        outputStream.close();
    }

    private String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    private String propagateTemplateSubstitution(String initialPage) {
        StringBuilder builder = new StringBuilder(initialPage);

        for (String key : propagateMap.keySet()) {
            while (builder.indexOf(key) >= 0) {
                int lr = builder.indexOf(key);
                int rr = lr + key.length();

                builder.replace(lr, rr, propagateMap.get(key));
            }
        }

        return builder.toString();
    }
}
