# 3D 애니메이션 쇼츠 제작 자동화 파이프라인 청사진 (Multi-Agent 기반)

본 청사진은 NotebookLM에서 제공된 요구사항(워크플로우, 프롬프트 템플릿, API 스펙)을 Java 생태계(Spring Boot, LangChain4j)와 Next.js 프론트엔드를 결합하여 구현하는 시스템 아키텍처 및 개발 로드맵입니다.

## User Review Required

> [!IMPORTANT]
> - 본 계획은 승인이 필요합니다. 아래 설계 및 로드맵을 검토하시고 **'Proceed(진행)'** 버튼을 눌러주시거나 피드백을 남겨주세요.
> - 기존 NotebookLM 문서에는 Supabase가 언급되었으나, 요청하신 조건에 따라 **PostgreSQL + Spring Data JPA** 기반으로 아키텍처를 재설계했습니다.

## 시스템 아키텍처

### 1. Backend (Spring Boot 3.x)
- **프레임워크**: Spring Boot 3.x, Spring WebFlux (또는 WebMVC + `@Async`)
- **데이터베이스 연동**: Spring Data JPA를 사용하여 PostgreSQL과 연동. 비동기 환경에서의 DB 접근을 고려하여 커넥션 풀(HikariCP)을 최적화하고 명확한 트랜잭션 경계를 설정합니다.
- **보안**: 
  - Spring Security를 통한 API 보호.
  - 외부 API 키(Gemini, Vertex AI 등)는 시스템 환경변수 또는 로컬 `.env` 파일을 통해 런타임 주입하며, 소스 코드 내 하드코딩을 원천 차단합니다.

### 2. AI Pipeline (LangChain4j)
- **역할**: Multi-Agent 오케스트레이션 및 LLM(Gemini) 통신
- **Agent 1 (기획 및 이미지 프롬프트)**: 최신 트렌드를 반영하여 쇼츠 아이디어를 도출하고, 9:16 비율, 1인칭 POV, 털/질감 묘사를 포함한 프롬프트를 작성합니다.
- **Agent 2 (영상 프롬프트 및 모션 제어)**: 승인된 이미지와 연계하여 동적 모션(Flow/Veo API용) 프롬프트를 작성하며, 원치 않는 효과를 차단하는 `CRITICAL WARNING` 통제 규칙을 강제합니다.

### 3. Frontend (Next.js)
- **프레임워크**: Next.js (App Router 기반)
- **역할**: Spring Boot REST API와 통신하는 독립된 프론트엔드 대시보드
- **기능**: 기획(아이디어) 선택, 프롬프트 피드백(수정 요청), 생성된 미디어 에셋(이미지/영상) 갤러리 뷰어, 최종 승인 및 다운로드 인터페이스 제공.

## DB 스키마 설계 (PostgreSQL + Spring Data JPA)

```java
// 1. Project Entity: 전체 작업 상태 관리
@Entity
@Table(name = "projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title; // 프로젝트(쇼츠) 기획 제목
    @Column(columnDefinition = "TEXT")
    private String description; // 기획 내용 및 대본
    
    @Enumerated(EnumType.STRING)
    private ProjectStatus status; // IDEA_GENERATED, IMAGE_APPROVED, VIDEO_GENERATING, COMPLETED 등
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// 2. PromptHistory Entity: 프롬프트 생성 히스토리 및 피드백 기록
@Entity
@Table(name = "prompt_histories")
public class PromptHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @Enumerated(EnumType.STRING)
    private PromptType type; // IMAGE_PROMPT, VIDEO_PROMPT
    
    @Column(columnDefinition = "TEXT")
    private String content; // 긍정 프롬프트 내용 (영문)
    
    @Column(columnDefinition = "TEXT")
    private String negativeContent; // CRITICAL 파싱으로 추출된 부정 프롬프트
    
    private Integer version; // 피드백에 따른 버전 관리용
    private Boolean isApproved;
    
    private LocalDateTime createdAt;
}

// 3. Asset Entity: 생성된 미디어 파일 관리 (이미지/비디오)
@Entity
@Table(name = "assets")
public class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @Enumerated(EnumType.STRING)
    private AssetType type; // THUMBNAIL_IMAGE, SCENE_IMAGE, VIDEO_CLIP
    
    private String fileUrl; // 스토리지에 저장된 객체 URL 또는 로컬 경로
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_history_id")
    private PromptHistory promptUsed; // 생성에 사용된 프롬프트 매핑
    
    @Enumerated(EnumType.STRING)
    private AssetStatus status; // PENDING, GENERATING, SUCCESS, FAILED
    
    private LocalDateTime createdAt;
}
```

## 단계별 개발 로드맵

### Phase 1: Spring Boot 초기 세팅 및 Next.js 프론트엔드 연동 뼈대 구축
- [ ] Spring Boot 3.x 프로젝트 초기화 및 PostgreSQL, Spring Data JPA 설정
- [ ] Next.js 프로젝트 초기화 (`npx create-next-app`) 및 기본 레이아웃 구성
- [ ] 백엔드 CORS 설정 및 API 응답 포맷/예외 처리 공통화 (Global Exception Handler)
- [ ] JPA Entity 설계 코딩 및 테이블 매핑 검증

### Phase 2: LangChain4j 기반 에이전트 1 (소재 및 이미지 프롬프트 생성)
- [ ] LangChain4j 설정 및 Gemini API 연동 모듈 개발
- [ ] **Agent 1 로직**: NotebookLM의 템플릿(01, 02) 룰을 System Message에 주입하여 쇼츠 대본 아이디어 도출 API 개발
- [ ] **프롬프트 분리 로직**: 생성된 이미지 프롬프트에서 `CRITICAL:` 키워드를 파싱하여 긍정 파라미터와 부정 파라미터(`negativePrompt`)로 분리 저장하는 로직 구현
- [ ] 프론트엔드 연동: 아이디어 3가지 중 1개 선택 UI, 프롬프트 내용 확인 및 승인 뷰어 구성

### Phase 3: LangChain4j 기반 에이전트 2 (동영상 생성 비동기 API & Webhook)
- [ ] **Agent 2 로직**: 사용자가 승인한 이미지와 연계하여 동적 모션 프롬프트를 작성하는 체인 구성 (강력한 통제 규칙 반영)
- [ ] **비동기 영상 생성**: 긴 대기 시간이 발생하는 Google Veo / Vertex AI Video API 호출부를 비동기로 래핑 (`@Async` 또는 WebClient 활용)
- [ ] **상태 관리 체계**: 영상 생성 시작 시 상태를 `GENERATING`으로 업데이트하고, 처리가 완료되면 상태 갱신 및 파일 URL을 DB에 저장
- [ ] 프론트엔드에서 영상 생성 상태를 조회할 수 있는 Polling(또는 SSE) API 구축

### Phase 4: Human-in-the-Loop 피드백 UI 연동 및 최종 다운로드 패키징
- [ ] **Human-in-the-Loop 피드백 처리**: 생성된 이미지나 영상이 마음에 들지 않을 경우, 프론트엔드에서 텍스트 피드백을 받아 에이전트가 프롬프트를 재작성하는 루프 구현
- [ ] 프롬프트 히스토리(버전) 조회 UI 구현 (이전 프롬프트와 수정된 프롬프트 비교)
- [ ] 최종 검수 완료 시 영상 및 이미지 파일을 로컬/클라우드 환경에 다운로드할 수 있는 기능 추가
- [ ] 보안 점검 (API Key 유출 여부, 비정상 접근 제어 등) 및 전체 테스트

## 보안 및 비동기 예외 처리 방안

> [!WARNING]
> - **API 키 은닉**: 애플리케이션의 모든 API Key(Gemini, DB 인증정보)는 소스 코드 외부에 두어야 하며, Spring Config를 통한 `.env` 매핑 또는 배포 환경의 Secret 기능을 활용합니다.
> - **비동기 타임아웃 및 재시도 (Resilience)**: 외부 영상/이미지 생성 API 호출은 Timeout이 길게 발생하거나 네트워크 오류가 생길 가능성이 높습니다. LangChain4j 내부 및 WebClient 통신 시 적절한 Read/Connect Timeout을 명시하고, 오류 시 `AssetStatus`를 `FAILED`로 안전하게 업데이트해야 합니다.
> - **오류 피드백 루프**: API 호출 실패나 할당량(Quota) 초과 시 발생한 예외 상황을 캐치하여, 대시보드 사용자에게 "생성 실패 - 원인(예: API 한도 초과)"을 명확하게 노출하는 에러 핸들링을 적용합니다.

## Open Questions

- 비동기로 처리되는 영상 생성 결과를 프론트엔드에 전달하기 위해 주기적으로 API를 호출하는 Polling 방식을 사용할까요, 아니면 SSE(Server-Sent Events)나 WebSocket을 구성하는 것을 선호하시나요?
- 생성된 이미지 및 동영상 에셋을 임시로 프로젝트 내부 폴더에 저장할까요, 아니면 초반부터 외부 스토리지(예: AWS S3, Supabase Storage) 연동을 염두에 둘까요?
