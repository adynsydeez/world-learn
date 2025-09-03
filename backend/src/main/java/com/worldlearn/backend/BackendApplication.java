package com.worldlearn.backend;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class BackendApplication {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
        }).start(7000);

        app.get("/", ctx -> ctx.result("Backend is running "));
    }
}
