package spring.ai.example.spring_ai_demo.domain.document;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DocumentKeyGuideLineRAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public DocumentKeyGuideLineRAGService(@Qualifier("documentKeyGuide") ChromaVectorStore chromaVectorStore, ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                                    .defaultAdvisors(
                                            new QuestionAnswerAdvisor(
                                                    chromaVectorStore, SearchRequest.builder()
                                                                                    .similarityThreshold(0.85d)
                                                                                    .topK(3).build()))
                                    .build();
        this.vectorStore = chromaVectorStore;
    }

    public String classifyDocumentGuideLine(String documentContent, String key) {

        log.info("documentContent 과 Key 에 따른 GuideLine RAG 검색");

        log.info("similar: {}", vectorStore.similaritySearch(SearchRequest.builder().query(documentContent + ", " + key).similarityThreshold(0.8d).topK(6).build()));

        String content = chatClient.prompt(new Prompt("documentContent 과 Key 에 따라 간결한 guideLine 3-4문장 이내로 작성해주세요")).user(documentContent + ", " + key).call().content();
        log.info("content: {}", content);

        return content;
    }
}
