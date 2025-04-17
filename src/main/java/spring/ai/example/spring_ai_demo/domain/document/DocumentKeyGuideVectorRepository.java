package spring.ai.example.spring_ai_demo.domain.document;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class DocumentKeyGuideVectorRepository {

    private final VectorStore vectorStore;

    public DocumentKeyGuideVectorRepository(
            @Qualifier("documentKeyGuide")
            VectorStore vectorStore
    ) {
        this.vectorStore = vectorStore;
    }

    public String findDocumentGuideLine(String documentType, String key) {
        // 문서 타입과 키를 결합하여 검색
        String query = documentType + " " + key;
        List<Document> results = vectorStore.similaritySearch(query);
        
        if (!results.isEmpty()) {
            for (Document document : results) {
                Object typeObj = document.getMetadata().get("type");
                Object keyObj = document.getMetadata().get("key");
                
                // 타입과 키가 모두 일치하는 문서 찾기
                if (typeObj != null && keyObj != null && 
                    documentType.equals(typeObj.toString()) && 
                    key.equals(keyObj.toString())) {
                    
                    Object guideLine = document.getMetadata().get("guideLine");
                    if (guideLine != null) {
                        return guideLine.toString();
                    }
                }
            }
        }
        
        return null;
    }
    
    // 문서 키와 작성 가이드라인 저장
    public void saveDocumentKeyGuideLine(String documentType, String key, String guideLine) {
        Map<String, Object> metadata = Map.of(
            "type", documentType,
            "key", key,
            "guideLine", guideLine
        );
        
        // forEmbedding
        String forEmbedding = documentType + " " + key + ": " + guideLine;

        Document document = new Document(UUID.randomUUID().toString(), forEmbedding, metadata);
        vectorStore.add(List.of(document));
    }
}
