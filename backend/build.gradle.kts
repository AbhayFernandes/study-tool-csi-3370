plugins {
    application
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.studytool"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Javalin web framework
    implementation("io.javalin:javalin:5.6.3")
    
    // Google Guava for dependency injection
    implementation("com.google.inject:guice:7.0.0")
    implementation("com.google.guava:guava:32.1.3-jre")
    
    // Jackson for JSON serialization
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.slf4j:slf4j-api:2.0.9")
    
    // ScyllaDB Java Driver
    implementation("com.scylladb:java-driver-core:4.18.1.0")
    implementation("com.scylladb:java-driver-query-builder:4.18.1.0")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("io.javalin:javalin-testtools:5.6.3")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.assertj:assertj-core:3.24.2")

    // Google GenAI SDK
    implementation(platform("com.google.cloud:libraries-bom:26.62.0"))
    implementation("com.google.cloud:google-cloud-vertexai")

    // PDF text extraction
    implementation("org.apache.pdfbox:pdfbox:2.0.30")
}

application {
    mainClass.set("com.studytool.Main")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.shadowJar {
    archiveBaseName.set("study-tool-backend")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.register("dev") {
    dependsOn("run")
    group = "application"
    description = "Run the application in development mode"
} 