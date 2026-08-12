# Java Spring Boot & Next.js 기반 Multi-Agent 쇼츠 자동화 파이프라인 명세

## 1. 개요 및 시스템 구현 아키텍처

본 문서는 @WorkFluffs 채널의 3D 애니메이션 쇼츠 자동 제작을 위해 구현된 Java (Spring Boot 3.x), LangChain4j, Next.js 15, PostgreSQL 아키텍처 및 REST API / SSE 명세서이다.

---

## 2. 데이터베이스 스키마 명세 (PostgreSQL + Spring Data JPA)

### Projects 테이블 (`projects`)
- `id` (BIGINT, Primary Key)
- `title` (VARCHAR): 쇼츠 기획 제목
- `description` (TEXT): 아이디어 대본 및 1인칭 훅/본능/펀치라인 요약
- `status` (VARCHAR): `IDEA_GENERATED`, `IMAGE_PROMPT_GENERATED`, `IMAGE_APPROVED`, `VIDEO_PROMPT_GENERATED`, `VIDEO_GENERATING`, `COMPLETED`, `FAILED`
- `created_at`, `updated_at` (TIMESTAMP)

### Prompt Histories 테이블 (`prompt_histories`)
- `id` (BIGINT, Primary Key)
- `project_id` (BIGINT, Foreign Key -> `projects.id`)
- `type` (VARCHAR): `IMAGE_PROMPT`, `VIDEO_PROMPT`
- `content` (TEXT): AI 에이전트가 작성한 긍정 프롬프트 (영문)
- `negative_content` (TEXT): `CRITICAL:` 키워드로 분리 파싱된 부정 제약사항 (Negative Prompt)
- `version` (INT): Human-in-the-Loop 피드백에 따른 프롬프트 버전 (v1, v2...)
- `is_approved` (BOOLEAN): 사용자 승인 여부

### Assets 테이블 (`assets`)
- `id` (BIGINT, Primary Key)
- `project_id` (BIGINT, Foreign Key)
- `type` (VARCHAR): `THUMBNAIL_IMAGE`, `SCENE_IMAGE`, `VIDEO_CLIP`
- `file_url` (VARCHAR): 로컬 스토리지 (`/uploads/`) 저장 경로 또는 스토리지 URL
- `prompt_used_id` (BIGINT, Foreign Key)
- `status` (VARCHAR): `PENDING`, `GENERATING`, `SUCCESS`, `FAILED`

---

## 3. LangChain4j Multi-Agent 구성

1. **Agent 1 (IdeaAgent & ImagePromptAgent)**:
   - 소재 및 대본 도출 (`IdeaAgent`)
   - 9:16 비율 픽사 스타일 영문 프롬프트 작성 및 `CRITICAL:` 부정 프롬프트 분리 파싱 (`Agent1Service`)
2. **Agent 2 (VideoPromptAgent & VideoGenerationService)**:
   - 숏폼 비디오 모션 프롬프트 및 `CRITICAL WARNING` 통제 규칙 강제
   - `@Async` 기반 비동기 영상 생성 처리 및 실시간 SSE 알림

---

## 4. 실시간 통신 및 저장소 명세

1. **SSE (Server-Sent Events) 엔드포인트**:
   - `GET /api/v1/sse/video-status/{projectId}`
   - 영상 생성 진행 상태 (`GENERATING`, `SUCCESS`, `FAILED`) 및 결과 파일 URL 실시간 스트리밍전달.
2. **로컬 스토리지 (`FileStorageService`)**:
   - 개발 및 검수 단계에서는 `/uploads` 디렉토리에 `.mp4` 및 `.png` 미디어 파일을 임시 저장 및 제공.

---

## 5. Human-in-the-Loop UI 및 피드백 흐름

1. **Step 1 (소재 도출)**: 주제 입력 ➔ AI 아이디어 3종 도출 ➔ 사용자 선택
2. **Step 2 (이미지 프롬프트 승인/수정)**: 긍정/부정 프롬프트 확인 ➔ 피드백 입력 시 버전업(`v1 -> v2`) ➔ 승인(Approve)
3. **Step 3 (모션 프롬프트 승인/수정)**: 모션 및 경고 규칙 확인 ➔ 피드백 수정 ➔ 영상 생성 요청
4. **Step 4 (SSE 수신 & 다운로드)**: 실시간 SSE 진행률 모니터링 ➔ 9:16 비디오 프리뷰 ➔ 최종 패키지 다운로드
