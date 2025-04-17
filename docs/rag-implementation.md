# Spring AI RAG(Retrieval-Augmented Generation) 구현 상세

## RAG 구현 개요

이 프로젝트는 Spring AI를 활용하여 RAG(Retrieval-Augmented Generation) 패턴을 구현합니다. RAG는 대규모 언어 모델(LLM)의 응답 생성 능력과 검색 시스템을 결합하여, 보다 정확하고 맥락에 맞는 응답을 생성하는 기법입니다.

## RAG 구현 컴포넌트

### 1. 벡터 저장소 (Vector Store)

시스템은 ChromaDB를 활용한 두 개의 벡터 저장소를 구현합니다:

#### DocumentGuideVectorRepository
```java
@Component
public class DocumentGuideVectorRepository {

    private final VectorStore vectorStore;

    public DocumentGuideVectorRepository(@Qualifier("documentGuide") ChromaVectorStore chromaVectorStore) {
        this.vectorStore = chromaVectorStore;
    }

    // 문서 타입과 가이드라인 저장
    public void saveDocumentTypeGuideLine(String documentType, String guideLine, String description) {
        // 문서 타입을 벡터로 저장
        vectorStore.add(
                List.of(
                        new Document(
                                documentType,
                                Map.of("documentType", documentType, 
                                       "documentGuideLine", guideLine, 
                                       "documentDescription", description)
                        )
                )
        );
    }

    // 문서 내용과 가장 유사한 문서 타입 찾기
    public String findClosestDocumentType(String documentContent) {
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(documentContent)
                        .topK(1)
                        .build()
        );

        if (similarDocuments.isEmpty()) {
            return "알 수 없는 문서 타입";
        }

        return similarDocuments.get(0).getMetadata().get("documentType").toString();
    }
}
```

#### DocumentKeyGuideVectorRepository
```java
@Component
public class DocumentKeyGuideVectorRepository {

    private final VectorStore vectorStore;

    public DocumentKeyGuideVectorRepository(@Qualifier("documentKeyGuide") ChromaVectorStore chromaVectorStore) {
        this.vectorStore = chromaVectorStore;
    }

    // 문서 타입, 키, 가이드라인 저장
    public void saveDocumentKeyGuideLine(String documentType, String key, String guideLine) {
        // 키와 가이드라인을 벡터로 저장
        vectorStore.add(
                List.of(
                        new Document(
                                key,
                                Map.of("documentType", documentType, 
                                       "key", key, 
                                       "guideLine", guideLine)
                        )
                )
        );
    }

    // 키에 대한 가이드라인 검색
    public String findGuideLineForKey(String documentType, String key) {
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(key)
                        .filter(Map.of("documentType", documentType))
                        .topK(1)
                        .build()
        );

        if (similarDocuments.isEmpty()) {
            return "해당 키에 대한 가이드라인이 없습니다.";
        }

        return similarDocuments.get(0).getMetadata().get("guideLine").toString();
    }
}
```

### 2. RAG 서비스 구현

#### DocumentGuideLineRAGService
```java
@Slf4j
@Service
public class DocumentGuideLineRAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public DocumentGuideLineRAGService(@Qualifier("documentGuide") ChromaVectorStore chromaVectorStore, ChatModel chatModel) {
        // QuestionAnswerAdvisor를 통한 RAG 구현
        this.chatClient = ChatClient.builder(chatModel)
                                    .defaultAdvisors(
                                            new QuestionAnswerAdvisor(
                                                    chromaVectorStore, 
                                                    SearchRequest.builder()
                                                       .similarityThreshold(0.85d)
                                                       .topK(3)
                                                       .build()))
                                    .build();
        this.vectorStore = chromaVectorStore;
    }

    // 문서 내용에 따른 가이드라인 생성
    public String classifyDocumentGuideLine(String documentContent) {
        log.info("documentContent 에 따른 GuideLine RAG 검색");

        // 유사 문서 검색 결과 로깅
        log.info("similar: {}", vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(documentContent)
                        .similarityThreshold(0.8d)
                        .topK(6)
                        .build()));

        // RAG를 통한 응답 생성
        String content = chatClient.prompt(new Prompt("documentContent 에 따른 간결한 DocumentGuideLine을 3-4문장 이내로 작성해주세요"))
                .user(documentContent)
                .call()
                .content();
                
        log.info("content: {}", content);
        return content;
    }

    // 문서 내용 및 키에 따른 가이드라인 생성
    public String classifyDocumentGuideLine(String documentContent, String key) {
        log.info("documentContent 과 Key 에 따른 GuideLine RAG 검색");
        
        // RAG를 통한 응답 생성
        String content = chatClient.prompt(new Prompt("documentContent 과 Key 에 따라 간결한 guideLine 3-4문장 이내로 작성해주세요"))
                .user(documentContent + ", " + key)
                .call()
                .content();
                
        log.info("content: {}", content);
        return content;
    }
}
```

#### DocumentTypeRAGService
```java
@Service
public class DocumentTypeRAGService {

    private final ChatClient chatClient;

    public DocumentTypeRAGService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String classifyDocumentType(String documentContent) {
        // 문서 내용을 바탕으로 문서 타입 분류
        return chatClient.prompt("입력된 문서 내용을 분석하여 문서 타입을 분류해주세요").user(documentContent).call().content();
    }
}
```

## RAG 작동 원리

1. **문서 분석 및 벡터 저장**
   - 사용자가 제공한 문서를 OpenAI를 통해 분석
   - 분석된 문서 타입, 가이드라인, 키워드를 벡터화하여 ChromaDB에 저장

2. **유사 문서 검색**
   - 새로운 문서 내용 입력 시, 벡터 유사도 검색을 통해 가장 유사한 문서 타입 찾기
   - 검색 파라미터:
     - `topK`: 상위 K개의 가장 유사한 문서 반환
     - `similarityThreshold`: 유사도 임계값 (0.8~0.85)

3. **컨텍스트 기반 응답 생성**
   - 검색된 문서 타입 또는 키워드 정보를 바탕으로 OpenAI 모델에 프롬프트 전송
   - 검색 결과를 컨텍스트로 활용하여 보다 정확한 가이드라인 생성

## 벡터 저장소 설정

### ChromaConfig 클래스
```java
@Configuration
public class ChromaConfig {

    @Value("${spring.ai.vectorstore.chroma.client.host}")
    private String chromaClientHost;

    @Value("${spring.ai.vectorstore.chroma.client.port}")
    private int chromaClientPort;

    @Value("${spring.ai.vectorstore.document-collection-names.document-type-key-guide}")
    private String documentTypeKeyGuideCollectionName;

    @Value("${spring.ai.vectorstore.document-collection-names.document-type-guide}")
    private String documentTypeGuideCollectionName;

    @Bean
    public RestClient.Builder builder() {
        return RestClient.builder().requestFactory(new SimpleClientHttpRequestFactory());
    }

    @Bean
    public ChromaApi chromaApi(RestClient.Builder restClientBuilder) {
        String chromaUrl = chromaClientHost + ":" + chromaClientPort;
        return new ChromaApi(chromaUrl, restClientBuilder);
    }

    // 문서 키워드 가이드라인 벡터 저장소
    @Bean
    @Qualifier("documentKeyGuide")
    public ChromaVectorStore chromaDocumentKeyGuideVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName(documentTypeKeyGuideCollectionName)
                .initializeSchema(true)
                .build();
    }

    // 문서 타입 가이드라인 벡터 저장소
    @Bean
    @Qualifier("documentGuide")
    public ChromaVectorStore chromaDocumentGuideVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName(documentTypeGuideCollectionName)
                .initializeSchema(true)
                .build();
    }
}
```

## Spring AI 어드바이저 활용

`QuestionAnswerAdvisor`를 통해 RAG 패턴을 구현합니다:

```java
ChatClient.builder(chatModel)
    .defaultAdvisors(
        new QuestionAnswerAdvisor(
            chromaVectorStore, 
            SearchRequest.builder()
                .similarityThreshold(0.85d)
                .topK(3)
                .build()
        )
    )
    .build();
```

이 어드바이저는:
1. 사용자 쿼리를 벡터화
2. 벡터 저장소에서 유사한 문서 검색
3. 검색된 문서를 컨텍스트로 활용하여 LLM에 전달
4. LLM이 컨텍스트를 바탕으로 더 정확한 응답 생성

## RAG의 장점

1. **정확성 향상**: 오픈 도메인 질문에서도 구체적인 정보 제공 가능
2. **최신 정보 활용**: 벡터 저장소에 새로운 정보 추가 가능
3. **환각(Hallucination) 감소**: 검색된 사실 기반 응답 생성
4. **도메인 특화**: 특정 분야(이 경우 문서 가이드라인)에 특화된 응답 제공

## 구현 고려 사항

1. **임베딩 품질**: 문서와 쿼리 간 의미적 유사성을 정확히 포착하는 임베딩 필요
2. **검색 최적화**: 유사도 임계값과 topK 값의 적절한 설정
3. **컨텍스트 창 제한**: LLM의 컨텍스트 창 크기를 고려한 검색 결과 수 제한
4. **프롬프트 엔지니어링**: 검색 결과를 효과적으로 활용하는 프롬프트 설계 