package spring.ai.example.spring_ai_demo.domain.hello;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @PostMapping("/hello")
    public String hello() {
        return helloService.hello();
    }
}
