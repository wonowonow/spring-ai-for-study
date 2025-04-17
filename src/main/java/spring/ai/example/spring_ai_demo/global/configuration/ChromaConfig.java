package spring.ai.example.spring_ai_demo.global.configuration;

import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

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

    @Bean
    @Qualifier("documentKeyGuide")
    public ChromaVectorStore chromaDocumentKeyGuideVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName(documentTypeKeyGuideCollectionName)
                .initializeSchema(true)
                .build();
    }

    @Bean
    @Qualifier("documentGuide")
    public ChromaVectorStore chromaDocumentGuideVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName(documentTypeGuideCollectionName)
                .initializeSchema(true)
                .build();
    }
}
