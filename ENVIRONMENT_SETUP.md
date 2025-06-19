# Environment Variables Setup

This document describes how to configure environment variables for the Study Tool application, particularly for the Vertex AI integration.

## Docker Compose Configuration

The `docker-compose.yml` file has been updated to include all necessary environment variables for Vertex AI integration. Here's how to set them up:

### Required Environment Variables

Create a `.env` file in the project root with the following variables:

```bash
# =============================================================================
# VERTEX AI CONFIGURATION (Required for AI features)
# =============================================================================

# Your Google Cloud Project ID (REQUIRED)
VERTEX_PROJECT_ID=your-google-cloud-project-id

# Path to your service account key file (required for authentication)
VERTEX_AI_KEY_PATH=./vertex-ai-key.json

# =============================================================================
# OPTIONAL CONFIGURATION
# =============================================================================

# Vertex AI location/region (optional, defaults to us-central1)
VERTEX_LOCATION=us-central1

# Text model to use (optional, defaults to text-bison-001)
VERTEX_TEXT_MODEL=text-bison-001

# Database keyspace (optional, defaults to studytool)
DB_KEYSPACE=studytool

# Legacy Gemini API key (optional, for backward compatibility)
# GEMINI_API_KEY=your-gemini-api-key
```

### Example .env File

```bash
# Example configuration
VERTEX_PROJECT_ID=my-study-tool-project-123
VERTEX_AI_KEY_PATH=./my-vertex-ai-key.json
VERTEX_LOCATION=us-central1
VERTEX_TEXT_MODEL=text-bison-001
DB_KEYSPACE=studytool
```

## Setup Steps

### 1. Create Service Account Key

First, create and download your service account key:

```bash
# Create service account
gcloud iam service-accounts create vertex-ai-service \
  --display-name="Vertex AI Service Account"

# Grant permissions
gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:vertex-ai-service@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/aiplatform.user"

# Create and download key
gcloud iam service-accounts keys create vertex-ai-key.json \
  --iam-account=vertex-ai-service@YOUR_PROJECT_ID.iam.gserviceaccount.com
```

### 2. Place Key File

Place the downloaded key file in your project root:

```bash
# Your project structure should look like:
study-tool-csi-3370/
├── vertex-ai-key.json          # Your service account key
├── docker-compose.yml
├── .env                        # Your environment variables
└── ...
```

### 3. Create .env File

Create a `.env` file in the project root:

```bash
# Copy the example above and replace with your actual values
VERTEX_PROJECT_ID=your-actual-project-id
VERTEX_AI_KEY_PATH=./vertex-ai-key.json
```

### 4. Run with Docker Compose

```bash
# Start all services
docker-compose up -d

# Check logs
docker-compose logs backend
```

## Environment Variable Reference

### Vertex AI Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `VERTEX_PROJECT_ID` | Google Cloud Project ID | `your-project-id` | Yes |
| `VERTEX_LOCATION` | Vertex AI region | `us-central1` | No |
| `VERTEX_TEXT_MODEL` | Text model to use | `text-bison-001` | No |
| `VERTEX_AI_KEY_PATH` | Path to service account key | `./vertex-ai-key.json` | Yes |
| `GOOGLE_CLOUD_PROJECT` | Alternative to VERTEX_PROJECT_ID | Uses VERTEX_PROJECT_ID | No |

### Database Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_KEYSPACE` | ScyllaDB keyspace | `studytool` | No |
| `DB_USERNAME` | Database username | None | No |
| `DB_PASSWORD` | Database password | None | No |

### Other Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `GEMINI_API_KEY` | Legacy Gemini API key | None | No |

## Docker Compose Volume Mapping

The docker-compose file automatically maps your service account key file into the container:

```yaml
volumes:
  - ${VERTEX_AI_KEY_PATH:-./vertex-ai-key.json}:/app/vertex-ai-key.json:ro
```

This means:
- The file specified in `VERTEX_AI_KEY_PATH` (or `./vertex-ai-key.json` by default) is mounted
- It's available inside the container at `/app/vertex-ai-key.json`
- The container's `GOOGLE_APPLICATION_CREDENTIALS` points to this location

## Troubleshooting

### Common Issues

1. **File not found errors**
   - Ensure your service account key file exists at the path specified in `VERTEX_AI_KEY_PATH`
   - Check file permissions (should be readable)

2. **Authentication errors**
   - Verify your service account has the correct permissions
   - Ensure the key file is valid JSON

3. **Project ID errors**
   - Double-check your `VERTEX_PROJECT_ID` matches your Google Cloud project
   - Ensure Vertex AI API is enabled in your project

### Verification

To verify your setup:

```bash
# Check if environment variables are loaded
docker-compose exec backend env | grep VERTEX

# Check if key file is mounted
docker-compose exec backend ls -la /app/vertex-ai-key.json

# Test the AI endpoints
curl -X POST http://localhost:8080/api/ai/explain \
  -H "Content-Type: application/json" \
  -d '{"concept":"photosynthesis","context":"biology"}'
```

## Security Notes

- Never commit your service account key file to version control
- Add `*.json` to your `.gitignore` file
- Consider using more secure authentication methods in production (e.g., Workload Identity)
- Rotate service account keys regularly

## Production Considerations

For production deployments, consider:

1. **Workload Identity** instead of service account keys
2. **Secret management** systems for sensitive values
3. **Environment-specific** configuration files
4. **Monitoring and logging** of API usage and costs 