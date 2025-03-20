package es.omarall.agentic.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RagAgentService {

    private final String SYSTEM_TEMPLATE = """
            You are a knowledgeable assistant specialized in navigating and answering questions about the documentation of Agentic Enterprise.
            * Focus on queries related to the documentation of Agentic Enterprise services and products.
            * Refer to the official documentation to provide accurate and detailed answers.
            * If the answer is found in the documentation, respond clearly and accurately, quoting the relevant document or article.
            * If the information is not available in the documentation, respond by directing the customer to contact support: {contactEmail}.
            """;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    @Value("business.contact-email")
    private String contactEmail;

    public RagAgentService(final ChatClient.Builder chatClientBuilder,
                           final VectorStore vectorStore) {

        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().similarityThreshold(0.6d).topK(1).build()))
                .build();
    }

    @Tool(name = "rag-agent", description = "Provides responses from the RAG agent, which retrieves information from the corporate knowledge base.")
    public String askMe(
            @ToolParam(description = "The user input, to determine it requires information from the corporate knowledge base.")
            String userMessage) {

        String response = chatClient
                .prompt()
                .system(systemSpec -> systemSpec.text(SYSTEM_TEMPLATE).param("contactEmail", contactEmail))
                .user(userSpec -> userSpec.text(userMessage))
                .call()
                .content();

        if (response == null || response.isEmpty() || response.contains("contact support")) {
            return "No information found in the corporate knowledge base. Please contact support: " + contactEmail;
        }

        return response;
    }

}