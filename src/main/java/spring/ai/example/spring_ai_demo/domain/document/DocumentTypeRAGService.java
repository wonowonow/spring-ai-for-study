package spring.ai.example.spring_ai_demo.domain.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentTypeRAGService {

    private final DocumentVectorRepository documentVectorRepository;

    public String classifyDocumentType(String documentContent) {

        return documentVectorRepository.findClosestDocumentType(documentContent);
    }
}
