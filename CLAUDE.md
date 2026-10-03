# 내 책장 (Bookshelf) — 프로젝트 컨텍스트

읽은 책을 ISBN 바코드 스캔 또는 제목 검색으로 찾아서, 실제 책장에 꽂은 것처럼 **책등(spine)** 이미지로 가상 책장에 보여주는 **개인용** 안드로이드 앱. 한국어 UI.
전체 계획과 단계별 프롬프트는 `docs/PROMPT_PLAN.md` 참고.

## 대상 기기
Galaxy S24+ (Android 14+), 세로 고정, 논리 해상도 약 412×892dp. Edge-to-edge, 다크/라이트 모드.

## 기술 스택
- Kotlin, Jetpack Compose (Material 3), Navigation Compose (문자열 route)
- Hilt, Room, DataStore(Preferences)
- 이후 단계: Retrofit/OkHttp + kotlinx.serialization, Coil, CameraX + ML Kit 바코드, androidx.palette, CanHub Image Cropper
- API 키(`ALADIN_TTB_KEY`, `KAKAO_REST_KEY`, `YES24_API_KEY`)는 `local.properties` → `BuildConfig`. 하드코딩 금지
- 버전은 `gradle/libs.versions.toml` 한 곳에서 관리

## 구조 (`app/src/main/java/com/song/bookshelf`)
- `core/data/model` — enum (ThicknessSource, SpineMode, SpineDefault, ShelfStyle, SortMode)
- `core/data/local` — Room 엔티티·DAO·AppDatabase
- `core/data/repository` — BookRepository, ShelfRepository, SettingsRepository
- `core/ui/theme` — 테마
- `di` — Hilt 모듈
- `navigation` — 하단 탭 + NavHost
- `feature/{shelf,search,scan,settings}` — 화면 + ViewModel

## 데이터 규칙
- 모든 엔티티: UUID `id`, `updatedAt`, `deleted`(소프트 삭제) — 나중 클라우드 동기화 대비. 조회 시 `deleted = 0` 필터
- 한 책이 여러 책장에 꽂힐 수 있다: Book / Shelf / ShelfItem(position)
- 책장: 세로 3칸, 칸 너비 500mm, 칸 안쪽 높이 280mm. 넘치면 새 책장 제안(칸 수 고정)
- 두께 우선순위: 알라딘 실측 → 예스24 책등 이미지 비율 → 쪽수×0.06mm(5~80mm) → 20mm
- 책등 표시 우선순위: 사용자 업로드 → 예스24 책등 → 빈 책등(기본) 또는 자동 생성

## 코드 규칙
- MVVM + 단방향 데이터 흐름 (ViewModel 이 StateFlow 노출)
- 순수 로직(레이아웃 계산, 정렬, 두께 추정)은 안드로이드 의존 없는 클래스로 분리하고 JUnit 테스트
- 여러 테이블을 바꾸는 작업은 `db.withTransaction`
