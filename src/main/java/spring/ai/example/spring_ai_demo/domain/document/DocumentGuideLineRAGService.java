package spring.ai.example.spring_ai_demo.domain.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentGuideLineRAGService {

    private final DocumentVectorRepository documentVectorRepository;

    public String classifyDocumentGuideLine(String documentType) {
        return documentVectorRepository.findDocumentGuideLine(documentType);
    }

    public String classifyDocumentGuideLine(String documentType, String key) {

        return documentVectorRepository.findDocumentGuideLine(documentType, key);
    }
}
