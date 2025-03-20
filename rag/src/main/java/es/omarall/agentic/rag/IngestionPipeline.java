package es.omarall.agentic.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngestionPipeline {

    private final VectorStore vectorStore;
    private final Path directoryPath = Paths.get("/tmp/rag");

    @Scheduled(fixedRate = 10000)
    public void ingestFiles() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, "*.txt")) {
            for (Path entry : stream) {
                processFile(entry);
                Files.delete(entry); // Delete the file after processing
            }
        } catch (Exception e) {
            try {
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error creating directory", ex);
            }
        }
    }

    private void processFile(Path filePath) {
        Resource resource = new PathMatchingResourcePatternResolver().getResource(filePath.toUri().toString());
        TextReader textReader = new TextReader(resource);
        List<Document> docs = textReader.get();
        List<Document> documents = new TokenTextSplitter().apply(docs);
        vectorStore.write(documents);
    }
}