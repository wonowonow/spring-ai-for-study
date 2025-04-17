# Spring AI 문서 분석 시스템 문서

## 문서 목록

이 디렉토리에는 Spring AI 문서 분석 시스템에 대한 상세 문서가 포함되어 있습니다. 각 문서는 시스템의 다양한 측면을 설명합니다.

### 1. [시스템 플로우](system-flow.md)
- 시스템의 전체적인 데이터 흐름과 작동 과정을 설명합니다.
- 주요 컴포넌트 간의 상호작용 및 데이터 흐름을 다이어그램과 함께 제공합니다.

### 2. [RAG 구현 상세](rag-implementation.md)
- RAG(Retrieval-Augmented Generation) 패턴의 구현 방법을 설명합니다.
- 벡터 저장소 구성 및 사용 방법을 제공합니다.
- Spring AI 어드바이저를 활용한 RAG 구현 방법을 설명합니다.

### 3. [API 엔드포인트](api-endpoints.md)
- 시스템에서 제공하는 REST API 엔드포인트의 상세 정보를 제공합니다.
- 각 API의 요청/응답 형식 및 사용 예시를 포함합니다.
- API 흐름 다이어그램을 통해 요청 처리 과정을 시각화합니다.

## 시스템 개요

Spring AI 문서 분석 시스템은 Spring AI v1.0.0을 활용하여 문서를 분석하고 가이드라인을 생성하는 시스템입니다. 이 시스템은 다음과 같은 주요 기능을 제공합니다:

1. **문서 타입 분류**: 입력된 문서 내용을 분석하여 문서 타입을 자동으로 분류합니다.
2. **문서 가이드라인 생성**: 문서 타입에 따른 작성 가이드라인을 제공합니다.
3. **키워드별 가이드라인**: 문서 내 특정 키워드에 대한 상세 가이드라인을 제공합니다.
4. **벡터 저장소 활용**: ChromaDB를 이용한 문서 임베딩 및 유사도 검색을 지원합니다.

## 기술 스택

- **Spring Boot 3.3**: 애플리케이션 프레임워크
- **Spring AI 1.0.0-M6**: AI 통합 및 RAG 구현
- **ChromaDB**: 벡터 데이터베이스
- **OpenAI**: 문서 분석 및 가이드라인 생성
- **PostgreSQL**: 관계형 데이터베이스
- **Docker Compose**: 환경 구성

## 시스템 설치 및 실행

### 1. 저장소 클론
```bash
git clone https://github.com/wonowonow/spring-ai-for-study.git
cd spring-ai-for-study
```

### 2. Docker Compose 실행
```bash
docker-compose up -d
```

### 3. 애플리케이션 빌드 및 실행
```bash
./gradlew bootRun
```

### 4. Talend-API-Tester(Chrome Extension) 를 이용한 API 테스트
1. test.json Talend API Tester 에 Import
2. [문서 생성 및 분석] API 전부 Request
3. 검색 테스트

## 프로젝트 구조

```
spring-ai-demo/
├── src/main/java/spring/ai/example/spring_ai_demo/
│   ├── domain/
│   │   ├── document/ (문서 관련 기능)
│   │   ├── hello/ (기본 테스트 API)
│   │   └── kv/ (기타 기능)
│   ├── global/ (전역 설정)
│   └── SpringAiDemoApplication.java
├── docker-compose.yml (Docker 설정)
└── build.gradle (프로젝트 의존성)
``` 