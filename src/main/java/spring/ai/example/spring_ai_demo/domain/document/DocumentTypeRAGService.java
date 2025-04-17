package spring.ai.example.spring_ai_demo.domain.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentTypeRAGService {

    private final DocumentGuideVectorRepository documentGuideVectorRepository;

    public String classifyDocumentType(String documentContent) {

        return documentGuideVectorRepository.findClosestDocumentType(documentContent);
    }
}
