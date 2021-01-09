package me.boblocker.api;

import io.javalin.Javalin;

public interface WebServer {
    void start();
    void stop();
    Javalin getWebServer();
}
