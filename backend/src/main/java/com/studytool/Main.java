package com.studytool;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.studytool.auth.AuthInterface;
import com.studytool.auth.LoginRequest;
import com.studytool.auth.LoginResponse;
import com.studytool.auth.SimpleAuthService;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    // Inject the auth service - can be swapped for different implementations
    private static final AuthInterface authService = new SimpleAuthService();
    
    public static void main(String[] args) {
        // Create Javalin app with basic configuration
        Javalin app = Javalin.create(config -> {
            // Enable CORS for all origins (development setup)
            config.plugins.enableCors(cors -> {
                cors.add(corsContainer -> corsContainer.anyHost());
            });
        }).start(8080);
        
        // Basic routes
        app.get("/hello", ctx -> ctx.result("Hello World! Study Tool Backend is running."));
        
        // Authentication routes
        app.post("/api/login", Main::handleLogin);
        app.post("/api/validate", Main::handleValidateToken);
        
        logger.info("Study Tool Backend started on port 8080");
        logger.info("Visit: http://localhost:8080");
        logger.info("Login endpoint: POST http://localhost:8080/api/login");
    }
    
    /**
     * Handle login requests
     */
    private static void handleLogin(Context ctx) {
        try {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
            
            if (request.getUsername() == null || request.getPassword() == null) {
                ctx.status(400).json(LoginResponse.failure("Username and password are required"));
                return;
            }
            
            boolean isAuthenticated = authService.authenticate(request.getUsername(), request.getPassword());
            
            if (isAuthenticated) {
                String token = authService.generateToken(request.getUsername());
                if (token != null) {
                    ctx.status(200).json(LoginResponse.success(token, request.getUsername()));
                    logger.info("User '{}' logged in successfully", request.getUsername());
                } else {
                    ctx.status(500).json(LoginResponse.failure("Failed to generate token"));
                }
            } else {
                ctx.status(401).json(LoginResponse.failure("Invalid username or password"));
                logger.warn("Failed login attempt for user '{}'", request.getUsername());
            }
        } catch (Exception e) {
            logger.error("Error handling login request", e);
            ctx.status(500).json(LoginResponse.failure("Internal server error"));
        }
    }
    
    /**
     * Handle token validation requests
     */
    private static void handleValidateToken(Context ctx) {
        try {
            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.status(401).json(Map.of("valid", false, "message", "Missing or invalid authorization header"));
                return;
            }
            
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            boolean isValid = authService.validateToken(token);
            
            if (isValid) {
                String username = authService.getUsernameFromToken(token);
                ctx.status(200).json(Map.of("valid", true, "username", username));
            } else {
                ctx.status(401).json(Map.of("valid", false, "message", "Invalid token"));
            }
        } catch (Exception e) {
            logger.error("Error validating token", e);
            ctx.status(500).json(Map.of("valid", false, "message", "Internal server error"));
        }
    }
} 