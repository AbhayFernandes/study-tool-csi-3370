package com.studytool;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.studytool.auth.AuthInterface;
import com.studytool.auth.LoginRequest;
import com.studytool.auth.LoginResponse;
import com.studytool.auth.ScyllaAuthService;
import com.studytool.database.DatabaseConfig;
import com.studytool.database.ScyllaManager;
import com.studytool.filestorage.FileStorageService;
import com.studytool.filestorage.FileUploadController;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    private static ScyllaManager scyllaManager;
    private static AuthInterface authService;
    
    public static void main(String[] args) {
        // Initialize database connection
        initializeDatabase();
        
        // Initialize file storage service
        String fileStoragePath = System.getenv("FILE_STORAGE_PATH");
        if (fileStoragePath == null || fileStoragePath.trim().isEmpty()) {
            fileStoragePath = "./uploads"; // Default fallback
        }
        FileStorageService fileStorageService = new FileStorageService(fileStoragePath);
        FileUploadController fileUploadController = new FileUploadController(fileStorageService);
        
        // Create Javalin app with basic configuration
        Javalin app = Javalin.create(config -> {
            // Enable CORS for all origins (development setup)
            config.plugins.enableCors(cors -> {
                cors.add(corsContainer -> corsContainer.anyHost());
            });
            
            // Configure multipart upload settings
            config.http.maxRequestSize = 10 * 1024 * 1024L; // 10MB max request size
        }).start(8080);
        
        // Add shutdown hook to properly close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down application...");
            if (scyllaManager != null) {
                scyllaManager.close();
            }
            app.stop();
        }));
        
        // Basic routes
        app.get("/hello", ctx -> ctx.result("Hello World! Study Tool Backend is running."));
        
        // Authentication routes
        app.post("/api/login", Main::handleLogin);
        app.post("/api/validate", Main::handleValidateToken);
        app.post("/api/register", Main::handleRegister);
        
        // Register file upload routes
        fileUploadController.registerRoutes(app);
        
        logger.info("Study Tool Backend started on port 8080");
        logger.info("Visit: http://localhost:8080");
        logger.info("Login endpoint: POST http://localhost:8080/api/login");
        logger.info("Register endpoint: POST http://localhost:8080/api/register");
        logger.info("File upload endpoint: POST http://localhost:8080/api/files/upload");
        logger.info("File storage path: {}", fileStoragePath);
    }
    
    /**
     * Initialize database connection and authentication service
     */
    private static void initializeDatabase() {
        try {
            DatabaseConfig dbConfig = DatabaseConfig.fromEnvironment();
            logger.info("Initializing database with config: {}", dbConfig);
            
            scyllaManager = new ScyllaManager(
                dbConfig.getContactPoint(),
                dbConfig.getDatacenter(),
                dbConfig.getKeyspace()
            );
            
            scyllaManager.initialize();
            authService = new ScyllaAuthService(scyllaManager);
            
            logger.info("Database initialized successfully with ScyllaDB");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
        }
    }
    
    /**
     * Handle user registration requests
     */
    private static void handleRegister(Context ctx) {
        try {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
            
            if (request.getUsername() == null || request.getPassword() == null) {
                ctx.status(400).json(LoginResponse.failure("Username and password are required"));
                return;
            }
            
            // Only available with ScyllaAuthService
            if (authService instanceof ScyllaAuthService) {
                ScyllaAuthService scyllaAuth = (ScyllaAuthService) authService;
                boolean userCreated = scyllaAuth.createUser(request.getUsername(), request.getPassword());
                
                if (userCreated) {
                    ctx.status(201).json(LoginResponse.success(null, request.getUsername()));
                    logger.info("User '{}' registered successfully", request.getUsername());
                } else {
                    ctx.status(409).json(LoginResponse.failure("User already exists or registration failed"));
                }
            } else {
                ctx.status(503).json(LoginResponse.failure("Registration not available with current authentication service"));
            }
        } catch (Exception e) {
            logger.error("Error handling registration request", e);
            ctx.status(500).json(LoginResponse.failure("Internal server error"));
        }
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