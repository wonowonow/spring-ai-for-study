package spring.ai.example.spring_ai_demo.domain.document;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentGuideLineRAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public DocumentGuideLineRAGService(@Qualifier("documentGuide") ChromaVectorStore chromaVectorStore, ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                                    .defaultAdvisors(
                                            new QuestionAnswerAdvisor(
                                                    chromaVectorStore, SearchRequest.builder()
                                                                                    .similarityThreshold(0.8d)
                                                                                    .topK(6).build()))
                                    .build();
        this.vectorStore = chromaVectorStore;
    }

    public String classifyDocumentGuideLine(String documentType) {

        log.info("documentType 에 따른 GuideLine RAG 검색");

        log.info("similar: {}", vectorStore.similaritySearch(SearchRequest.builder().query(documentType).similarityThreshold(0.8d).topK(6).build()));

        String content = chatClient.prompt(new Prompt("documentType 에 따른 DocumentGuideLine 을 작성해주세요")).user(documentType).call().content();
        log.info("content: {}", content);

        return content;
    }

    public String classifyDocumentGuideLine(String documentType, String key) {

        log.info("documentType 과 Key 에 따른 GuideLine RAG 검색");
        String content = chatClient.prompt(new Prompt("documentType 과 Key 에 따라 Key 의 guideLine 을 작성해주세요")).user(documentType + ", " + key).call().content();
        log.info("content: {}", content);

        return content;
    }
}
