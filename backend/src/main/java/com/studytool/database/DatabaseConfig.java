package com.studytool.database;

import java.net.InetSocketAddress;

/**
 * Configuration class for ScyllaDB database connection settings.
 */
public class DatabaseConfig {
    
    // Default configuration values
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 9042;
    public static final String DEFAULT_DATACENTER = "datacenter1";
    public static final String DEFAULT_KEYSPACE = "studytool";
    
    private final String host;
    private final int port;
    private final String datacenter;
    private final String keyspace;
    
    /**
     * Creates a DatabaseConfig with default values.
     */
    public DatabaseConfig() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_DATACENTER, DEFAULT_KEYSPACE);
    }
    
    /**
     * Creates a DatabaseConfig with specified values.
     * 
     * @param host The ScyllaDB host
     * @param port The ScyllaDB port
     * @param datacenter The datacenter name
     * @param keyspace The keyspace name
     */
    public DatabaseConfig(String host, int port, String datacenter, String keyspace) {
        this.host = host;
        this.port = port;
        this.datacenter = datacenter;
        this.keyspace = keyspace;
    }
    
    /**
     * Creates a DatabaseConfig from environment variables or uses defaults.
     * Uses both Docker Compose style (DB_*) and direct style (SCYLLA_*) environment variables.
     * 
     * @return DatabaseConfig instance
     */
    public static DatabaseConfig fromEnvironment() {
        // Try Docker Compose style environment variables first, then fall back to SCYLLA_* style
        String host = System.getenv("DB_HOST");
        if (host == null || host.isEmpty()) {
            host = System.getenv("SCYLLA_HOST");
        }
        if (host == null || host.isEmpty()) {
            host = DEFAULT_HOST;
        }
        
        String portStr = System.getenv("DB_PORT");
        if (portStr == null || portStr.isEmpty()) {
            portStr = System.getenv("SCYLLA_PORT");
        }
        int port = DEFAULT_PORT;
        if (portStr != null && !portStr.isEmpty()) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                // Use default port if parsing fails
                port = DEFAULT_PORT;
            }
        }
        
        String datacenter = System.getenv("DB_DATACENTER");
        if (datacenter == null || datacenter.isEmpty()) {
            datacenter = System.getenv("SCYLLA_DATACENTER");
        }
        if (datacenter == null || datacenter.isEmpty()) {
            datacenter = DEFAULT_DATACENTER;
        }
        
        String keyspace = System.getenv("DB_KEYSPACE");
        if (keyspace == null || keyspace.isEmpty()) {
            keyspace = System.getenv("SCYLLA_KEYSPACE");
        }
        if (keyspace == null || keyspace.isEmpty()) {
            keyspace = DEFAULT_KEYSPACE;
        }
        
        return new DatabaseConfig(host, port, datacenter, keyspace);
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getDatacenter() {
        return datacenter;
    }
    
    public String getKeyspace() {
        return keyspace;
    }
    
    public InetSocketAddress getContactPoint() {
        return new InetSocketAddress(host, port);
    }
    
    @Override
    public String toString() {
        return "DatabaseConfig{" +
               "host='" + host + '\'' +
               ", port=" + port +
               ", datacenter='" + datacenter + '\'' +
               ", keyspace='" + keyspace + '\'' +
               '}';
    }
} 