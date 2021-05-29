package edu.ifmo.debug_tool.controller;

import com.sun.net.httpserver.HttpServer;
import edu.ifmo.debug_tool.properties.PropertiesContainer;

import java.net.InetSocketAddress;

public class WebServer {

    public WebServer() {
    }

    public void create() throws Exception {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(
                        PropertiesContainer.getInstance().getWebHostName(),
                        PropertiesContainer.getInstance().getWebPort()
                ), 0);

        server.createContext(
                PropertiesContainer.WEB_PREFIX,
                new FrontController()
        );

        server.setExecutor(PropertiesContainer.EXECUTOR_SERVICE);
        server.start();
    }
}
