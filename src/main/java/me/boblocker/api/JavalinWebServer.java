package me.boblocker.api;

import io.javalin.Javalin;
import me.boblocker.core.annotation.InjectValue;
import me.boblocker.core.annotation.Singleton;

@Singleton
public class JavalinWebServer implements WebServer {
    private final Javalin webServer;
    @InjectValue
    private int port;

    public JavalinWebServer() {
        this.webServer = Javalin.create();
    }

    @Override
    public void start() {
        this.webServer.start(port);
    }

    @Override
    public void stop() {
        this.webServer.stop();
    }

    @Override
    public Javalin getWebServer() {
        return webServer;
    }
}
