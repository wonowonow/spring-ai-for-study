package spring.ai.example.spring_ai_demo.domain.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentAnalysisResponseDto;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentCreateRequestDto;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentKeyGuide;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentResponseDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentTypeRAGService documentTypeRAGService;
    private final DocumentGuideLineRAGService documentGuideLineRAGService;
    private final DocumentVectorRepository documentVectorRepository;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DocumentResponseDto createDocument(DocumentCreateRequestDto documentCreateRequestDto) {
        String content = chatClient.prompt(new Prompt("당신은 문서 전문가 입니다. 다음 문서를 통해 어떤 종류의 문서인지 추론하고" +
                "문서의 전체적인 가이드라인을 작성하세요 또헌" +
                "이 글의 각 키와 밸류를 찾아 키에 해당하는 문서 작성 가이드라인을 작성하세요" +
                "응답은 Json 형식으로 코드 블럭 없이 json 형식만 보냅니다." +
                "documentType: String, documentGuideLine: String, keys: [key: String, guideLine: String]").getContents()).user(documentCreateRequestDto.documentContent()).call().content();

        String documentType;
        String documentGuideLine;
        System.out.println(content);
        try {
            DocumentAnalysisResponseDto result = objectMapper.readValue(content, DocumentAnalysisResponseDto.class);
            documentType = result.documentType();
            documentGuideLine = result.documentGuideLine();
            
            // 문서 타입 벡터 저장
            documentVectorRepository.saveDocumentTypeGuideLine(documentType, documentGuideLine);
            
            // 키와 가이드라인 벡터 저장
            if (!result.keys().isEmpty()) {
                for (DocumentKeyGuide keyGuide : result.keys()) {
                    String key = keyGuide.key();
                    String guideLine = keyGuide.guideLine();
                    
                    // 각 키와 가이드라인을 벡터 DB에 저장
                    documentVectorRepository.saveDocumentKeyGuideLine(documentType, key, guideLine);
                }
            }
            
        } catch (Exception e) {
            // 예외 발생 시 RAG 서비스를 통해 문서 타입 분류
            documentType = documentTypeRAGService.classifyDocumentType(documentCreateRequestDto.documentContent());
        }

        return new DocumentResponseDto(documentCreateRequestDto.documentContent(), documentType);
    }

    public String getDocumentGuideLine(String documentType, String key) {
        return documentGuideLineRAGService.classifyDocumentGuideLine(documentType, key);
    }

    public String getDocumentGuideLine(String documentType) {
        return documentGuideLineRAGService.classifyDocumentGuideLine(documentType);
    }

    public String getDocumentGuideLineByContent(String documentContent) {

        log.info("문서 내용에 따른 문서 타입 근사값 서칭");
        String closestDocumentType = documentVectorRepository.findClosestDocumentType(documentContent);
        log.info("문서 내용에 따른 근사한 문서 타입 결과: {}", closestDocumentType);

        return documentGuideLineRAGService.classifyDocumentGuideLine(closestDocumentType);
    }


}
