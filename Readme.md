# AI Environment Demo Project

A Spring Boot application that provides REST APIs for ONNX model inference and Weka machine learning operations.

## Project Overview

This project combines:
- ONNX model inference for AI predictions
- Weka machine learning for decision trees and traditional ML algorithms
- REST API endpoints for easy integration
- File-based data management with support for both small and large datasets

## Directory Structure

The project follows a standard Maven directory layout:

```
demo-ai-environment/
├── src/main/
│   ├── java/com/ai/environment/demo/
│   │   ├── DemoApplication.java
│   │   ├── Controller/
│   │   │   ├── AIController.java       # ONNX model endpoints
│   │   │   └── WekaController.java     # Weka ML endpoints
│   │   ├── Service/
│   │   │   ├── ServiceAI.java          # ONNX model service
│   │   │   └── WekaService.java        # Weka ML service
│   │   └── ReqDTO/
│   │       └── PredictReq.java         # Request DTOs
│   └── resources/
│       ├── application.properties
│       └── SmollData/
│           ├── Weka.arff              # Small dataset
│           └── WekaTrained.model      # Trained Weka model
├── BigData/
│   └── Weka.arff                      # Large dataset
├── weights/
│   ├── model.onnx                     # ONNX model file
│   └── tokenizer.json                 # Tokenizer configuration
└── pom.xml
```

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- Valid ONNX model file in weights/model.onnx
- ARFF dataset files for Weka operations

### Installation

1. Clone or download the project:
   ```bash
   cd "d:\Colleges Files\demo-ai-environment"
   ```

2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Verify the application is running:
   - Application starts on port 9000
   - Health check: http://localhost:9000/v1/health

## API Endpoints

### ONNX Model Endpoints (/v1)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | /v1/predict | Make prediction using ONNX model | JSON with input data |
| GET | /v1/health | Health check | None |
| GET | /v1/model-info | Get ONNX model information | None |
| GET | /v1/model-status | Check if ONNX model is loaded | None |
| POST | /v1/reload-model | Reload ONNX model | None |
| GET | /v1/weka/tree-ai | Show Weka tree (via AI controller) | None |

### Weka ML Endpoints (/v1/weka)

| Method | Endpoint | Description | Data Source |
|--------|----------|-------------|-------------|
| GET | /v1/weka/tree2 | Train and show decision tree | BigData/Weka.arff |
| GET | /v1/weka/predict | Predict using trained model | SmollData/Weka.arff + WekaTrained.model |

## Usage Examples

### ONNX Model Prediction
```bash
curl -X POST http://localhost:9000/v1/predict \
  -H "Content-Type: application/json" \
  -d '{"input": [1.0, 2.0, 3.0]}'
```

### Weka Decision Tree
```bash
curl http://localhost:9000/v1/weka/tree2
```

### Health Check
```bash
curl http://localhost:9000/v1/health
```

### Model Status
```bash
curl http://localhost:9000/v1/model-status
```

## Data Management

### Small Data (Resources)
- Location: `src/main/resources/SmollData/`
- Files: `Weka.arff`, `WekaTrained.model`
- Usage: Loaded as classpath resources for predictions and small operations

### Large Data (External)
- Location: `BigData/`
- Files: `Weka.arff`
- Usage: Loaded by file path for training and large operations

### Model Files
- ONNX Models: `weights/model.onnx`, `weights/tokenizer.json`
- Weka Models: `src/main/resources/SmollData/WekaTrained.model`

## Configuration

### Server Port
- The server port can be configured in the `application.properties` file
- Default port is `9000`
- To change: Add `server.port=9001` in application.properties

### Environment Variables
- Set JAVA_HOME to your Java installation
- Ensure Maven is in your system PATH

## Development

### Adding New Endpoints
1. Create methods in appropriate controllers (AIController or WekaController)
2. Use unique paths to avoid conflicts
3. Add proper error handling and logging

### Model Management
- ONNX models: Place in weights/ folder
- Weka models: Train and save to SmollData/ folder
- Datasets: Use ARFF format for Weka operations

## Troubleshooting

### Common Issues

**Port 9000 already in use:**
```
# Change port in application.properties
server.port=9001
```

**ONNX model not found:**
- Ensure weights/model.onnx exists and is valid
- Check file permissions

**Weka data file not found:**
- Verify ARFF files exist in correct locations
- Check file naming and extensions

**Endpoint conflicts:**
- Ensure unique paths across all controllers
- Avoid duplicate @GetMapping annotations

### Logs and Debugging
- Check console output for detailed error messages
- Use `mvn spring-boot:run -e` for full stack traces
- Enable debug logging if needed

## Dependencies

### Core Dependencies
- Spring Boot 3.5.4 - Web framework and dependency injection
- Weka 3.8.6 - Machine learning library
- ONNX Runtime - ONNX model inference
- Jackson - JSON processing
- Lombok - Code generation

### Build Tool
- Maven - Dependency management and build automation

## Quick Start Commands

```bash
# Start the application
mvn spring-boot:run

# Test endpoints
curl http://localhost:9000/v1/health
curl http://localhost:9000/v1/weka/tree2

# Build without running
mvn clean package

# Clean build
mvn clean install
```

## Support

For issues and questions:
1. Check the troubleshooting section above
2. Verify all file paths and dependencies
3. Ensure Java and Maven are properly installed
4. Check application logs for detailed error messages

---
Start making predictions and training models through the REST API.

