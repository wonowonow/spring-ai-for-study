package spring.ai.example.spring_ai_demo.domain.document.dto;

import java.util.List;

public record DocumentAnalysisResponseDto(
        String documentType,
        String documentGuideLine,
        List<DocumentKeyGuide> keys
) {
}
