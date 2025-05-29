package com.studytool;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        // Create Javalin app with basic configuration
        Javalin app = Javalin.create(config -> {
            // Enable CORS for all origins (development setup)
            config.plugins.enableCors(cors -> {
                cors.add(corsContainer -> corsContainer.anyHost());
            });
        }).start(8080);
        
        // Basic routes
        app.get("/", ctx -> ctx.result("Hello World! Study Tool Backend is running."));
        
        logger.info("Study Tool Backend started on port 8080");
        logger.info("Visit: http://localhost:8080");
    }
} 