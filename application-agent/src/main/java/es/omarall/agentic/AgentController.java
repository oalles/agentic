package es.omarall.agentic;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AgentController {

    private final static String SYSTEM_TEMPLATE = """
            You are an AI assistant specialized in handling requests for infrastructure monitoring and retrieving information from the corporate knowledge base.
            Your goal is to assist the user in formulating a clear and specific question related to these areas. Use the provided tools to gather and deliver accurate data.
            * Engage in a conversation with the user to understand their needs.
            * Guide the user to formulate a clear and specific question related to infrastructure monitoring or the corporate knowledge base.
            * Only forward the question to the appropriate tool once you are sure it is well-defined.
            * For infrastructure monitoring, use the server-status service to fetch real-time status and metrics.
            * For knowledge base queries, use the RAG agent tool to retrieve relevant information.
            * If the user’s input is not a question, politely ask them to clarify or provide more details.
            * If the information requested is about system status, service status, API status, or infrastructure status, use the server-status service.
            * Respond in the detected language of the user.
            """;

    private final ChatClient chatClient;

    public AgentController(final ChatClient.Builder chatClientBuilder, List<ToolCallback> toolCallbacks) {
        this.chatClient = chatClientBuilder
//                .defaultAdvisors(new SimpleLoggerAdvisor(), new PromptChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultTools(toolCallbacks)
                .build();
    }

    @GetMapping("/agentic")
    public String askMe(@RequestParam("q") String userMessage) {

        return chatClient
                .prompt()
                .system(systemSpec -> systemSpec.text(SYSTEM_TEMPLATE))
                .user(userSpec -> userSpec.text(userMessage))
                .call()
                .content();

    }
}
