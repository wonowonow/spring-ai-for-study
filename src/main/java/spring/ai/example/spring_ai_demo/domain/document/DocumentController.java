package spring.ai.example.spring_ai_demo.domain.document;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentCreateRequestDto;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentGuideLineRequestDto;
import spring.ai.example.spring_ai_demo.domain.document.dto.DocumentResponseDto;

@RestController
@Slf4j
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

    @PostMapping("/documentGuides/contents")
    public ResponseEntity<String> getDocumentGuideLineByContent(
            @RequestBody
            String request
    ) {

        return ResponseEntity.status(200).body(documentService.getDocumentGuideLineByContent(request));
    }

    @PostMapping("/documentGuides")
    public ResponseEntity<String> getDocumentGuideLine(
            @RequestBody
            DocumentGuideLineRequestDto requestDto
    ) {

        log.info("getDocumentGuideLine");
        if (requestDto.key() != null && !requestDto.key().trim().isEmpty()) {
            log.info("search with {} {}", requestDto.documentContent(), requestDto.key());
            return ResponseEntity.status(200).body(documentService.getDocumentGuideLine(requestDto.documentContent(), requestDto.key()));
        }

        log.info("search with {}", requestDto.documentContent());
        return ResponseEntity.status(200).body(documentService.getDocumentGuideLine(requestDto.documentContent()));
    }
}
