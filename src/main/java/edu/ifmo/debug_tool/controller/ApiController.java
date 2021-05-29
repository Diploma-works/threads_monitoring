package edu.ifmo.debug_tool.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.ifmo.debug_tool.controller.api.GetListOfThreadsNames;
import edu.ifmo.debug_tool.controller.api.GetThreadInfosByThreadIdAndTimestamp;
import edu.ifmo.debug_tool.controller.api.GetThreadIsActiveByNameAndId;
import edu.ifmo.debug_tool.controller.api.GetThreadsAmountController;

import java.io.IOException;

public class ApiController implements HttpHandler {

    public ApiController() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        if (path.contains("threads_total_amount")) {
            new GetThreadsAmountController(exchange).process();
        } else if (path.contains("threads_names_list")) {
            new GetListOfThreadsNames(exchange).process();
        } else if (path.contains("threads_infos_by_thread_id")) {
            new GetThreadInfosByThreadIdAndTimestamp(exchange).process();
        } else if (path.contains("thread_is_active")) {
            new GetThreadIsActiveByNameAndId(exchange).process();
        } else {
            throw new RuntimeException("500");
        }

    }

}
