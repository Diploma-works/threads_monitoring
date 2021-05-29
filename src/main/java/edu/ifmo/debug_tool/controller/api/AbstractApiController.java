package edu.ifmo.debug_tool.controller.api;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractApiController {

    protected final HttpExchange exchange;
    protected Map<String, String> queryParametersMap = new HashMap<>();
    protected String result = "";

    public AbstractApiController(HttpExchange exchange) {
        this.exchange = exchange;
    }

    protected void prepareParams() throws Exception {
        queryParametersMap = queryToMap(exchange.getRequestURI().getQuery());
    }

    protected abstract void doJob() throws Exception;

    protected void prepareResult() throws Exception {
        byte[] bs = result.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, bs.length);
        exchange.getResponseBody().write(bs);
    }

    protected final Map<String, String> queryToMap(String query) {
        if (query == null) return new HashMap<>();

        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public final void process() throws IOException {
        try {
            prepareParams();
            doJob();
            prepareResult();

        } catch (Exception e) {
            e.printStackTrace(System.err);

            if (e.getMessage().equals("404")) {
                exchange.sendResponseHeaders(404, 0);
            } else if (e.getMessage().equals("400")) {
                exchange.sendResponseHeaders(400, 0);
            } else {
                exchange.sendResponseHeaders(500, 0);
            }
        } finally {
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        }
    }
}
