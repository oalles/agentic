# Agentic Enterprise

## Description

Agentic is a sample system designed to offer intelligent business services through an agent-based architecture. 
The project consists of multiple services working together using MCP (Model Control Protocol) to provide comprehensive solution.

## Architecture

The system architecture is based on three main components that communicate with each other:

- A central application agent that receives user requests
- A RAG (Retrieval Augmented Generation) service to answer queries with contextual knowledge, exposed via MCP.
- A monitoring service to provide system status information, exposed via MCP.

[![Watch the video](https://www.loom.com/embed/8f2e07544fa1403c9f6fb2e07c817ad0)](https://www.loom.com/share/8f2e07544fa1403c9f6fb2e07c817ad0)

## Modules

### application-agent

Main agent that receives user requests, via rest endpoint, and orchestrates the other services.

**Configuration**:
- **Port**: 8080
- **Dependencies**: Connects to `rag` and `monitor` services via MCP clients

**Configuration file**: [application.yml](application-agent/src/main/resources/application.yml)

### rag

Retrieval Augmented Generation (RAG) agent that provides responses based on stored knowledge.
Redis vector store is used for efficient indexing and retrieval of information.

**Configuration**:
- **Port**: 8081
- **Type**: MCP Server
- **Storage**: Redis Vector Store for efficient indexing and retrieval

**Configuration file**: [application.yml](rag/src/main/resources/application.yml)

### monitor

**Role**: Monitoring service that provides information about the system status.

**Configuration**:
- **Port**: 8082
- **Type**: MCP Server
- **Functionality**: Collects fict data about system health and performance

**Configuration file**: [application.yml](monitor/src/main/resources/application.yml)

## Running the Project

### Redis

```bash
cd environment;
docker-compose up -d
```

### Starting Services

**1. Start the RAG service**:
```bash
cd rag
mvn spring-boot:run
```

**2. Start the monitor service**:
```bash
cd monitor
mvn spring-boot:run
```

**3. Start the application agent**:
```bash
cd application-agent
mvn spring-boot:run
```

## Usage

Send requests to the application agent on port 8080. The system will coordinate communication with the RAG and monitor services to retrieve 
the necessary information and respond accordingly.

## System Diagram

The system architecture is based on a central agent that communicates via MCP clients with specialized services, each running on its own port and offering specific capabilities.
