package es.omarall.agentic.monitor;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SystemStatusService {

    @Tool(name = "system-status", description = "Provides the runtime status of the infrastructure, system, or application, including server status, disk space information, and API usage metrics. Use this tool to check if the system is UP, DOWN, or CAIDO, monitor service quality, and get infrastructure runtime information.")
    public Map<String, Object> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        Map<String, Object> details = new HashMap<>();
        details.put("diskSpace", createDiskSpaceDetails());
        health.put("details", details);
        return health;
    }

    private Map<String, Object> createDiskSpaceDetails() {
        Map<String, Object> diskSpace = new HashMap<>();
        diskSpace.put("status", "UP");
        diskSpace.put("total", 1000000);
        diskSpace.put("free", 500000);
        diskSpace.put("threshold", 10000);
        return diskSpace;
    }
}
