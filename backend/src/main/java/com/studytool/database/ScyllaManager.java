package com.studytool.database;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

/**
 * Manages the ScyllaDB connection and session lifecycle.
 * This class handles the initialization and cleanup of the CqlSession.
 */
public class ScyllaManager {
    private static final Logger logger = LoggerFactory.getLogger(ScyllaManager.class);
    
    private CqlSession session;
    private final String datacenter;
    private final String keyspace;
    private final InetSocketAddress contactPoint;
    
    /**
     * Creates a new ScyllaManager instance.
     * 
     * @param contactPoint The ScyllaDB contact point (host and port)
     * @param datacenter The local datacenter name
     * @param keyspace The keyspace to use
     */
    public ScyllaManager(InetSocketAddress contactPoint, String datacenter, String keyspace) {
        this.contactPoint = contactPoint;
        this.datacenter = datacenter;
        this.keyspace = keyspace;
    }
    
    /**
     * Initializes the connection to ScyllaDB.
     * This method should be called during application startup.
     */
    public void initialize() {
        try {
            logger.info("Initializing ScyllaDB connection to {}:{} in datacenter '{}' with keyspace '{}'", 
                       contactPoint.getHostName(), contactPoint.getPort(), datacenter, keyspace);
            
            CqlSessionBuilder builder = CqlSession.builder()
                .addContactPoint(contactPoint)
                .withLocalDatacenter(datacenter);
                
            if (keyspace != null && !keyspace.isEmpty()) {
                builder = builder.withKeyspace(keyspace);
                logger.debug("Using keyspace: {}", keyspace);
            } else {
                logger.debug("No keyspace specified, will connect without default keyspace");
            }
            
            logger.debug("Attempting to build CqlSession...");
            session = builder.build();
            
            // Test the connection by executing a simple query
            logger.debug("Testing connection with a simple query...");
            session.execute("SELECT now() FROM system.local");
            
            logger.info("Successfully connected to ScyllaDB cluster");
            logger.info("Session metadata: {}", session.getMetadata().getClusterName());
        } catch (Exception e) {
            logger.error("Failed to initialize ScyllaDB connection to {}:{} in datacenter '{}': {}", 
                        contactPoint.getHostName(), contactPoint.getPort(), datacenter, e.getMessage());
            logger.debug("Full connection error details:", e);
            throw new RuntimeException("Failed to connect to ScyllaDB", e);
        }
    }
    
    /**
     * Gets the current CqlSession.
     * 
     * @return The active CqlSession
     * @throws IllegalStateException if the session is not initialized
     */
    public CqlSession getSession() {
        if (session == null) {
            throw new IllegalStateException("ScyllaDB session not initialized. Call initialize() first.");
        }
        return session;
    }
    
    /**
     * Closes the ScyllaDB session.
     * This method should be called during application shutdown.
     */
    public void close() {
        if (session != null) {
            logger.info("Closing ScyllaDB session");
            try {
                CompletionStage<Void> closeFuture = session.closeAsync();
                closeFuture.toCompletableFuture().get(); // Wait for close to complete
                logger.info("ScyllaDB session closed successfully");
            } catch (Exception e) {
                logger.warn("Error while closing ScyllaDB session", e);
            } finally {
                session = null;
            }
        }
    }
    
    /**
     * Checks if the session is initialized and connected.
     * 
     * @return true if session is active, false otherwise
     */
    public boolean isConnected() {
        return session != null && !session.isClosed();
    }
} 