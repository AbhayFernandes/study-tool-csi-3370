# Study Tool CSI 3370

A comprehensive studying app powered by Google Gemini AI for CSI 3370.

## 🏗️ Architecture

This is a **cloud-native monorepo** containing:

### **Frontend (React + TypeScript)**
- **Framework**: React 18 with TypeScript
- **UI Components**: Material-UI / Tailwind CSS
- **State Management**: React Context + Hooks
- **HTTP Client**: Axios for API communication
- **Routing**: React Router for SPA navigation
- **Build Tool**: Create React App with optimizations

### **Backend (Java + Javalin)**
- **Framework**: Javalin (lightweight web framework)
- **Language**: Java 17
- **Build Tool**: Gradle with Kotlin DSL
- **Dependency Injection**: Google Guice
- **Database Driver**: DataStax Java Driver for ScyllaDB
- **API Documentation**: OpenAPI/Swagger (future)

### **Database (ScyllaDB)**
- **Type**: NoSQL, Cassandra-compatible
- **Performance**: High-throughput, low-latency
- **Scalability**: Horizontal scaling support
- **Consistency**: Tunable consistency levels
- **Use Case**: Perfect for study material storage and user analytics

### **AI Integration (Google Gemini)**
- **Content Parsing**: Extract key information from documents
- **Question Generation**: Create practice questions automatically
- **Content Summarization**: Generate study summaries
- **Smart Recommendations**: Personalized study suggestions

### **Infrastructure (Docker + Docker Compose)**
- **Containerization**: All services containerized
- **Orchestration**: Docker Compose for local development
- **Networking**: Internal Docker network for service communication
- **Volumes**: Persistent storage for database
- **Health Checks**: Automated service health monitoring

## 📊 Database Schema

### Core Tables:
- **`study_materials`**: Store uploaded documents and Gemini analysis
- **`questions`**: AI-generated and manual practice questions
- **`study_sessions`**: Track user study activities and progress
- **`user_progress`**: Monitor learning progress per material
- **`gemini_requests`**: Log API usage for monitoring and optimization

## 🚀 Quick Start

### Prerequisites
- **Docker & Docker Compose** (required)
- **Node.js 18+** (for local frontend development)
- **Java 17+** (for local backend development)
- **Google Gemini API key** (for AI features)

### 🐳 Production Setup (Docker)

1. **Clone and setup:**
   ```bash
   git clone <repository-url>
   cd study-tool-csi-3370
   cp .env.example .env
   # Edit .env and add your Gemini API key
   ```

2. **Start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - **Frontend**: http://localhost:3000
   - **Backend API**: http://localhost:8080
   - **ScyllaDB**: localhost:9042 (CQL), localhost:10000 (REST API)

### 🛠️ Development Setup

#### Backend Development:
```bash
cd backend
gradle run
```

#### Frontend Development:
```bash
cd frontend
npm install
npm start
```

#### Database Access:
```bash
# Connect to ScyllaDB with cqlsh
docker exec -it study-tool-scylladb cqlsh

# Or use the REST API
curl http://localhost:10000/storage_service/keyspaces
```

## 📋 Important Commands

### Docker Management:
```bash
# Start all services
docker-compose up -d

# Rebuild and start
docker-compose up --build

# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v

# View logs
docker-compose logs -f backend
docker-compose logs -f scylladb

# Scale services (future)
docker-compose up --scale backend=3
```

### Database Operations:
```bash
# Connect to ScyllaDB
docker exec -it study-tool-scylladb cqlsh

# Backup database
docker exec study-tool-scylladb nodetool snapshot studytool

# Monitor database
docker exec study-tool-scylladb nodetool status
docker exec study-tool-scylladb nodetool info

# View table structure
docker exec -it study-tool-scylladb cqlsh -e "DESCRIBE studytool.study_materials;"
```

### Development Commands:
```bash
# Backend
cd backend
gradle build          # Build project
gradle run            # Run development server
gradle test           # Run tests
gradle shadowJar       # Create fat JAR

# Frontend
cd frontend
npm install           # Install dependencies
npm start            # Development server
npm test             # Run tests
npm run build        # Production build
npm run eject        # Eject from CRA (⚠️ irreversible)
```

### Monitoring & Debugging:
```bash
# Monitor resource usage
docker stats

# View container details
docker inspect study-tool-backend
docker inspect study-tool-scylladb

# Access container shells
docker exec -it study-tool-backend bash
docker exec -it study-tool-scylladb bash
```