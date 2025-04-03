package spring.ai.example.spring_ai_demo.domain.document;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DocumentVectorRepository {

    private final ChromaVectorStore chromaVectorStore;

    public String findClosestDocumentType(String documentContent) {
        List<Document> results = chromaVectorStore.similaritySearch(documentContent);
        
        if (!results.isEmpty()) {
            Document closestDocument = results.get(0);
            Object typeObj = closestDocument.getMetadata().getOrDefault("type", "unknown");
            return typeObj != null ? typeObj.toString() : "unknown";
        }
        
        return "unknown";
    }

    public String findDocumentGuideLine(String documentType) {
        List<Document> results = chromaVectorStore.similaritySearch(documentType);

        if (!results.isEmpty()) {
            for (Document document : results) {
                Object guideLine = document.getMetadata().get("guideLine");
                if (guideLine != null) {
                    return guideLine.toString();
                }
            }
        }

        return null;
    }
    
    public String findDocumentGuideLine(String documentType, String key) {
        // 문서 타입과 키를 결합하여 검색
        String query = documentType + " " + key;
        List<Document> results = chromaVectorStore.similaritySearch(query);
        
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
    
    // 문서 타입 저장
    public void saveDocumentType(String documentType, String content) {
        Map<String, Object> metadata = Map.of(
            "type", documentType,
            "content", content
        );
        
        Document document = new Document(UUID.randomUUID().toString(), content, metadata);
        chromaVectorStore.add(List.of(document));
    }
    
    // 문서 키와 작성 가이드라인 저장
    public void saveDocumentKeyGuideLine(String documentType, String key, String guideLine) {
        Map<String, Object> metadata = Map.of(
            "type", documentType,
            "key", key,
            "guideLine", guideLine
        );
        
        // 문서 타입과 키를 결합하여 내용으로 사용 (벡터 저장을 위해)
        String content = documentType + " " + key + ": " + guideLine;
        Document document = new Document(UUID.randomUUID().toString(), content, metadata);
        chromaVectorStore.add(List.of(document));
    }
}
