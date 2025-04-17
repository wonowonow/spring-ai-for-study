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
    private final DocumentGuideVectorRepository documentGuideVectorRepository;
    private final DocumentKeyGuideVectorRepository documentKeyGuideVectorRepository;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DocumentResponseDto createDocument(DocumentCreateRequestDto documentCreateRequestDto) {

        String content = chatClient.prompt(new Prompt(
                "당신은 문서 전문가 입니다. 다음 문서를 통해 어떤 종류의 문서인지 추론하고 " +
                        "문서의 전체적인 가이드라인을 작성하세요. 또한 " +
                        "이 글의 각 키와 밸류를 찾아 키에 해당하는 문서 작성 가이드라인을 작성하세요. " +
                        "응답은 Json 형식으로 코드 블럭 없이 json 형식만 보냅니다. " +
                        "문서에 이미 적혀있는 내용이 가이드라인으로 판단되면 가이드 라인으로 작성해주세요. " +
                        "\n\n예제 1: \n" +
                        "문서: 회사명: 테크솔루션\n설립일: 2022년 3월\n대표자: 김기술\n사업 분야: AI 솔루션\n투자 요청액: 3억원\n시장 분석: 국내 AI 시장은 연간 20% 성장률을 보이고 있으며...\n수익 모델: 구독형 서비스 및 기업 맞춤형 솔루션... " +
                        "\n응답: {\"documentContent\":\"사업계획서\",\"documentGuideLine\":\"사업의 핵심 정보와 투자 유치를 위한 정보를 체계적으로 정리한 문서입니다. 전문적이고 객관적인 데이터를 기반으로 작성합니다.\",\"documentDescription\":\"AI 솔루션 기업의 투자 유치를 위한 사업계획서로, 회사 정보, 사업 분야, 시장 분석 및 수익 모델이 포함되어 있습니다.\",\"keys\":[{\"key\":\"회사명\",\"guideLine\":\"기업의 공식 상호를 정확하게 기재합니다.\"},{\"key\":\"설립일\",\"guideLine\":\"회사의 법적 설립일을 년월 형식으로 기재합니다.\"},{\"key\":\"대표자\",\"guideLine\":\"회사의 법적 대표자 이름을 기재합니다.\"},{\"key\":\"사업 분야\",\"guideLine\":\"회사의 주요 사업 영역을 간결하게 명시합니다.\"},{\"key\":\"투자 요청액\",\"guideLine\":\"필요한 투자금액을 구체적인 숫자로 표기합니다.\"},{\"key\":\"시장 분석\",\"guideLine\":\"해당 산업의 시장 동향과 성장률을 객관적 데이터로 제시합니다.\"},{\"key\":\"수익 모델\",\"guideLine\":\"회사의 수익 창출 방식을 명확하게 설명합니다.\"}]}" +

                        "\n\n예제 2: \n" +
                        "문서: 제목: 기후변화가 농업에 미치는 영향\n연구기관: 환경연구소\n연구기간: 2023.01-2023.06\n연구방법: 현장조사 및 데이터 분석\n주요 발견: 평균 기온 1도 상승 시 작물 생산량 8% 감소\n결론: 농업 분야의 기후변화 적응 전략 시급... " +
                        "\n응답: {\"documentContent\":\"연구보고서\",\"documentGuideLine\":\"연구의 목적, 방법, 결과를 객관적으로 기술하는 문서입니다. 학술적이고 논리적인 문체로 작성하며, 데이터와 증거에 기반한 서술이 필요합니다.\",\"documentDescription\":\"기후변화가 농업 생산성에 미치는 영향을 분석한 6개월간의 연구 결과물입니다.\",\"keys\":[{\"key\":\"제목\",\"guideLine\":\"연구의 내용을 명확하게 반영하는 제목을 작성합니다.\"},{\"key\":\"연구기관\",\"guideLine\":\"연구를 수행한 기관명을 정확히 기재합니다.\"},{\"key\":\"연구기간\",\"guideLine\":\"연구의 시작일과 종료일을 명시합니다.\"},{\"key\":\"연구방법\",\"guideLine\":\"데이터 수집 및 분석 방법을 간결하게 설명합니다.\"},{\"key\":\"주요 발견\",\"guideLine\":\"연구의 핵심적인 발견사항을 수치와 함께 제시합니다.\"},{\"key\":\"결론\",\"guideLine\":\"연구 결과를 바탕으로 한 결론과 제언을 명확히 서술합니다.\"}]}" +

                        "\n\n이제 다음 문서를 분석해주세요:" // few-shot prompting 지원 안 되는 거 같아서 prompt 에 다 적음
        )).user(documentCreateRequestDto.documentContent()).call().content();

        String documentType;
        String documentGuideLine;
        String documentDescription;
        System.out.println(content);
        try {
            DocumentAnalysisResponseDto result = objectMapper.readValue(content, DocumentAnalysisResponseDto.class);
            documentType = result.documentType();
            documentGuideLine = result.documentGuideLine();
            documentDescription = result.documentDescription();
            
            // 문서 타입 벡터 저장
            documentGuideVectorRepository.saveDocumentTypeGuideLine(documentType, documentGuideLine, documentDescription);
            
            // 키와 가이드라인 벡터 저장
            if (!result.keys().isEmpty()) {
                for (DocumentKeyGuide keyGuide : result.keys()) {
                    String key = keyGuide.key();
                    String guideLine = keyGuide.guideLine();
                    
                    // 각 키와 가이드라인을 벡터 DB에 저장
                    documentKeyGuideVectorRepository.saveDocumentKeyGuideLine(documentType, key, guideLine);
                }
            }
            
        } catch (Exception e) {
            // 예외 발생 시 RAG 서비스를 통해 문서 타입 분류
            documentType = documentTypeRAGService.classifyDocumentType(documentCreateRequestDto.documentContent());
        }

        return new DocumentResponseDto(documentCreateRequestDto.documentContent(), documentType);
    }

    public String getDocumentGuideLine(String documentContent, String key) {
        return documentGuideLineRAGService.classifyDocumentGuideLine(documentContent, key);
    }

    public String getDocumentGuideLine(String documentType) {
        return documentGuideLineRAGService.classifyDocumentGuideLine(documentType);
    }

    public String getDocumentGuideLineByContent(String documentContent) {

        log.info("문서 내용에 따른 문서 타입 근사값 서칭");
        String closestDocumentType = documentGuideVectorRepository.findClosestDocumentType(documentContent);
        log.info("문서 내용에 따른 근사한 문서 타입 결과: {}", closestDocumentType);

        return documentGuideLineRAGService.classifyDocumentGuideLine(closestDocumentType);
    }


}
