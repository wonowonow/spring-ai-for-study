package spring.ai.example.spring_ai_demo.domain.hello;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelloService {

    private final ChatClient chatClient;

    public String hello() {
        return chatClient.prompt().user("Say hello to me and give some 10 jokes").call().content();
    }
}
