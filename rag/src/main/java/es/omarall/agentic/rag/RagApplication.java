package es.omarall.agentic.rag;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.autoconfigure.ollama.OllamaChatProperties;
import org.springframework.ai.autoconfigure.ollama.OllamaInitializationProperties;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RagApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

//    @Bean
//    public void ingestionPipeline(VectorStore vectorStore) {
//        Resource resource = new DefaultResourceLoader().getResource("classpath:terms.txt");
//        TextReader textReader = new TextReader(resource);
//        List<Document> docs = textReader.get();
//        List<Document> documents = new TokenTextSplitter().apply(docs);
//        vectorStore.write(documents);
//    }

    @Bean
    public ToolCallbackProvider tools(RagAgentService ragAgentService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(ragAgentService)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = OllamaChatProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
            matchIfMissing = true)
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi, OllamaChatProperties properties,
                                           OllamaInitializationProperties initProperties,
                                           ObjectProvider<ObservationRegistry> observationRegistry,
                                           ObjectProvider<ChatModelObservationConvention> observationConvention) {
        var chatModelPullStrategy = initProperties.getChat().isInclude() ? initProperties.getPullModelStrategy()
                : PullModelStrategy.NEVER;

        var chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(properties.getOptions())
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .modelManagementOptions(
                        new ModelManagementOptions(chatModelPullStrategy, initProperties.getChat().getAdditionalModels(),
                                initProperties.getTimeout(), initProperties.getMaxRetries()))
                .build();

        observationConvention.ifAvailable(chatModel::setObservationConvention);

        return chatModel;
    }
}
