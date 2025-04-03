package spring.ai.example.spring_ai_demo.domain.document;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentCreateRequestDto;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentResponseDto;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/documents")
    public ResponseEntity<DocumentResponseDto> createDocument(
            @RequestBody
            DocumentCreateRequestDto requestDto
    ) {

        return ResponseEntity.status(200).body(documentService.createDocument(requestDto));
    }

    @PostMapping("/test")
    public ResponseEntity<String> getDocumentGuideLine(
            @RequestBody
            String request
    ) {

        return ResponseEntity.status(200).body(documentService.getDocumentGuideLine(request));
    }
}
