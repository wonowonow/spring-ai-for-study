package spring.ai.example.spring_ai_demo.domain.document;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class DocumentGuideVectorRepository {

    private final VectorStore vectorStore;

    public DocumentGuideVectorRepository(
            @Qualifier("documentGuide")
            VectorStore vectorStore
    ) {
        this.vectorStore = vectorStore;
    }

    public String findDocumentGuideLine(String documentType) {
        List<Document> results = vectorStore.similaritySearch(documentType);

        if (!results.isEmpty()) {
            for (Document document : results) {
                Object documentGuideLine = document.getMetadata().get("documentGuideLine");
                if (documentGuideLine != null) {
                    return documentGuideLine.toString();
                }
            }
        }

        return null;
    }

    // 문서 타입 저장
    public void saveDocumentTypeGuideLine(String documentType, String documentGuideLine, String documentDescription) {
        Map<String, Object> metadata = Map.of(
                "type", documentType,
                "documentGuideLine", documentGuideLine,
                "documentDescription", documentDescription
        );

        Document document = new Document(UUID.randomUUID().toString(), documentDescription, metadata);
        vectorStore.add(List.of(document));
    }

    public String findClosestDocumentType(String documentContent) {
        List<Document> results = vectorStore.similaritySearch(documentContent);

        if (!results.isEmpty()) {
            Document closestDocument = results.get(0);
            Object typeObj = closestDocument.getMetadata().getOrDefault("type", "unknown");
            return typeObj != null ? typeObj.toString() : "unknown";
        }

        return "unknown";
    }
}
