# Spring AI v1.0.0 연습 레포지토리

## 프로젝트 소개
이 프로젝트는 Spring AI v1.0.0을 활용한 문서 분석 및 가이드라인 생성 시스템입니다. OpenAI와 ChromaDB를 활용하여 RAG(Retrieval-Augmented Generation) 기반의 문서 처리 기능을 제공합니다.

## 주요 기능
- 문서 타입 분류: 입력된 문서 내용을 분석하여 문서 타입을 자동으로 분류
- 문서 가이드라인 생성: 문서 타입에 따른 작성 가이드라인 제공
- 키워드별 가이드라인: 문서 내 특정 키워드에 대한 상세 가이드라인 제공
- 벡터 저장소 활용: ChromaDB를 이용한 문서 임베딩 및 유사도 검색

## 기술 스택
- Spring Boot 3.3
- Spring AI 1.0.0-M6
- ChromaDB 벡터 저장소
- OpenAI 통합
- PostgreSQL 데이터베이스
- Docker Compose

## 설치 및 실행 방법
1. 저장소 클론
```bash
git clone https://github.com/wonowonow/spring-ai-for-study.git
cd spring-ai-for-study
```

2. Docker Compose 실행 PostgreSQL 및 Chroma Database
```bash
docker-compose up -d
```

3. 애플리케이션 빌드 및 실행
```bash
./gradlew bootRun
```

## API 엔드포인트
- `POST /documents`: 문서 생성 및 분석
- `POST /documentGuides/contents`: 문서 내용 기반 가이드라인 조회
- `POST /hello`: 기본 테스트 엔드포인트

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

## 의존성
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring AI Chroma Store
- Spring AI OpenAI
- PostgreSQL
- Lombok

