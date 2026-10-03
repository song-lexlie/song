# 📚 책등 책장 앱 — 개발 프롬프트 계획 (v2)

> 읽은 책을 ISBN(바코드/책 이름)으로 검색해 **책등(spine) 이미지**로 가상 책장에 꽂는 개인용 안드로이드 앱.
> 대상 기기: **Galaxy S24+** (6.7", 기본 FHD+ 1080×2340, 논리 해상도 약 **412×892dp**)

AI 코딩 도구(Claude Code 등)에 **순서대로 붙여넣을 프롬프트**입니다. 한 단계씩 실행해 동작을 확인한 뒤 다음으로 넘어가세요.

---

## 확정된 결정

| 항목 | 결정 |
|---|---|
| 플랫폼 | 안드로이드 네이티브 — Kotlin + Jetpack Compose |
| 사용 범위 | 나 혼자 쓰는 개인용, APK 직접 설치 (스토어 출시 없음) |
| 저장 | 1단계: 폰에만(Room). 2단계: 클라우드 동기화 → 처음부터 UUID id, `updatedAt`, `deleted` 플래그 포함 |
| 책 검색 | 주로 한국 책. **알라딘 API**(TTB 키) 1순위 → 실패 시 **카카오 책 검색** |
| 책등 이미지 | ① **예스24 오픈 API** 책등 이미지 → ② 사용자 업로드 사진 → ③ 기본: **빈 회색 책등 + "사진 등록" 유도** (설정/책별로 **자동 생성 책등**으로 전환 가능) |
| 두께 | 알라딘 실측(mm) → 예스24 책등 이미지 비율로 추정 → `쪽수 × 0.06mm`(5~80mm) → 사용자 수정 가능 |
| 책장 | **세로 3칸 × 가로 1칸**, 책장 하나가 화면 한 장에 스크롤 없이 표시. 칸 너비 실제 약 50cm(칸당 약 25권) |
| 꽉 찼을 때 | 칸 수 고정. 넘치면 **"○○ 2" 새 책장 생성을 제안**하고 넘친 책을 옮김 |
| 책장 폴더 | 한 책을 여러 책장에 꽂기 가능, 책장 복제로 다른 버전 정리 |
| 정렬 | 등록순 / 이름순 / 읽은 날짜순 / 내 순서. 정렬 상태에서 드래그하면 **보이던 순서로 '내 순서' 자동 전환** + 안내 |
| 독서 기록 | 읽은 날짜, 별점(1~5) |
| 책장 스타일 | 책장마다 선택: 원목 / 화이트 / 블랙 |

### 책등 이미지에 대해
- **알라딘·교보문고**: 공개 API로 책등 이미지를 제공하지 않음 (표지만).
- **예스24**: 오픈 API가 2026-10-01부터 책등·뒷면 이미지 URL을 제공. 형식 예: `https://image.yes24.com/goods/{상품번호}/SIDE/L`
  - 일부 책은 URL은 있지만 실제로는 **'이미지 준비중' 대체 그림** → 크기·비율·해시로 판별해 걸러야 함
  - 공식 문서로 필드명·키 발급 조건을 직접 확인한 뒤 구현할 것. 키가 없으면 이 단계는 건너뜀

### 책장 화면 크기 계산
- 화면 높이 892dp − 상태바 − 상단바(책장 칩·정렬) − 하단 내비게이션 ≈ **660dp → 3칸 + 선반 판**
- `scale(dp/mm) = min(가용너비 / 500mm, 칸 안쪽 높이dp / 280mm)` → 어느 화면에서도 한 장에 맞음
- 책은 선반 바닥에 맞춰 정렬(키 다른 책이 들쭉날쭉), 칸보다 큰 책은 칸 높이에 맞게 축소
- 칸 너비를 넘으면 다음 칸으로, 3칸을 넘으면 새 책장 제안 (추가·두께 수정·이동 모두 같은 규칙)

---

## 프롬프트 0 — 프로젝트 컨텍스트 (저장소 루트 `CLAUDE.md`로 저장 권장)

```
너는 안드로이드 네이티브 앱을 만드는 시니어 개발자야. 단계별로 기능을 요청할 테니 이 컨텍스트를 기억해.

[앱 개요]
읽은 책을 ISBN 바코드 스캔 또는 제목 검색으로 찾아서, 실제 책장에 꽂은 것처럼
"책등(spine)" 이미지로 가상 책장에 보여주는 개인용 앱. 한국어 UI.

[대상 기기]
Galaxy S24+ (Android 14+). 세로 고정. 논리 해상도 약 412x892dp.
펀치홀 상단·제스처 바 하단 인셋 처리(enableEdgeToEdge + WindowInsets). 다크/라이트 모드.

[기술 스택]
- Kotlin, Jetpack Compose(Material 3), Navigation Compose
- Hilt(DI), Room(DB), DataStore(설정)
- Retrofit + OkHttp + kotlinx.serialization, Coil(이미지)
- CameraX + ML Kit Barcode Scanning (EAN-13)
- androidx.palette(대표색 추출), CanHub Android-Image-Cropper(크롭)
- API 키(알라딘 TTB, 카카오 REST, 예스24)는 local.properties → BuildConfig 주입, 하드코딩 금지
- 단일 모듈, 패키지는 기능별: feature/shelf, feature/search, feature/scan, feature/book, core/data, core/ui

[데이터 모델] (나중 클라우드 동기화 대비: 모든 엔티티에 UUID id, updatedAt, deleted)
- Book: id, isbn13, title, author, publisher, pubDate, pageCount,
        thicknessMm, thicknessSource(ALADIN|YES24_RATIO|PAGES|USER),
        heightMm, widthMm, coverUrl, coverLocalPath,
        yes24SpineUrl, spineLocalPath(사용자 업로드), spineColor(자동 추출 색),
        spineMode(DEFAULT|BLANK|AUTO), readDate, rating(1~5, nullable), createdAt
- Shelf: id, name, style(WOOD|WHITE|BLACK), rowCount=3, rowWidthMm=500, rowHeightMm=280,
        sortMode(REGISTERED|TITLE|READ_DATE|CUSTOM), sortDesc, position, createdAt
- ShelfItem: id, shelfId, bookId, position, addedAt  (한 책이 여러 책장에 들어갈 수 있음)

[코드 규칙]
- MVVM + UDF(ViewModel이 StateFlow로 UI 상태 노출)
- 순수 로직(레이아웃 계산, 정렬, 두께 추정)은 안드로이드 의존 없는 클래스로 분리하고 JUnit 테스트 작성
- 각 단계 끝에 실행 방법과 확인할 점을 알려줘
```

---

## 프롬프트 1 — 프로젝트 셋업 & 데이터 계층

```
프롬프트 0 컨텍스트로 안드로이드 프로젝트를 만들어줘.

1. Gradle(Version Catalog) 설정, 위 라이브러리 추가, minSdk 26 / targetSdk 최신, 세로 고정, 카메라 권한
2. Room: Book, Shelf, ShelfItem 엔티티와 DAO
   - ShelfItem은 (shelfId, position) 순 조회, 책장 삭제 시 ShelfItem도 삭제(책은 유지)
   - 삭제는 deleted=true 소프트 삭제, 조회 시 제외
3. BookRepository, ShelfRepository (Flow 반환)
4. 첫 실행 시 "내 책장"(원목 스타일) 1개 자동 생성
5. 하단 내비게이션: [책장] [검색] [설정] + 어디서든 바코드 스캔 FAB
6. 설정(DataStore): 책등 없는 책 기본 표시 = 빈 책등 / 자동 생성
7. S24+ 에뮬레이터(412x892dp)에서 빌드·실행 확인
```

---

## 프롬프트 2 — 책 검색 (알라딘 → 카카오) + 예스24 책등

```
책 검색 기능을 만들어줘.

[검색 API]
- BookSearchSource 인터페이스, 구현 2개(폴백 순서):
  1) 알라딘 Open API: ItemSearch(제목), ItemLookUp(ISBN13, OptResult에 packing 포함)
     → 쪽수, sizeDepth(두께mm), sizeHeight, sizeWidth, 표지(cover 큰 사이즈)
  2) 카카오 책 검색 API (Authorization: KakaoAK {키})
- 공통 BookCandidate 모델로 변환, 검색어가 숫자 10/13자리면 ISBN 조회로 처리(ISBN-10은 13으로 변환)

[예스24 책등]
- Yes24SpineSource: ISBN으로 예스24 오픈 API를 조회해 책등 이미지 URL 획득
  (응답 필드명·인증 방식은 예스24 공식 문서를 확인해서 맞춰줘. 키가 비어 있으면 건너뜀)
- 받은 이미지가 '이미지 준비중' 대체 그림인지 판별: 다운로드 후 크기/가로세로비/해시 비교
  → 대체 그림이면 null 처리
- 책등 이미지가 유효하면 두께 추정: 이미지 가로/세로 비율 × heightMm

[두께 결정 순서] ThicknessEstimator (유닛 테스트 포함)
알라딘 실측 → 예스24 비율 → pageCount × 0.06mm(5~80mm) → 20mm, thicknessSource 기록

[UI - 검색 탭]
- 검색창 + 결과 리스트(표지, 제목, 저자, 출판사, 예상 두께, 책등 이미지 유무 아이콘)
- 항목 탭 → 바텀시트: 책등 미리보기, 꽂을 책장 선택(복수), 읽은 날짜(기본 오늘), 별점 → 저장
- 이미 등록된 ISBN이면 "이미 있는 책" 표시, 책장 추가만 가능
- 저장 시 표지·책등 이미지를 앱 내부 저장소로 다운로드
- 로딩/에러/결과 없음 상태 처리
```

---

## 프롬프트 3 — 바코드 스캔

```
CameraX + ML Kit로 ISBN 바코드 스캔 화면을 만들어줘.

- EAN-13만 인식, 978/979로 시작하는 것만 ISBN으로 처리
- 중앙에 가로로 긴 스캔 가이드, 나머지 어둡게
- 인식 시 진동 + 분석 일시정지 → ISBN 조회 → 프롬프트 2의 저장 바텀시트로
- 플래시 토글, 권한 거부 시 설정 화면 안내
- "연속 스캔" 토글: 마지막으로 고른 책장에 바로 저장하고 다음 스캔 (여러 권 빠르게 등록)
- 같은 바코드 2초 내 중복 인식 방지
```

---

## 프롬프트 4 — 책등 표시 & 사진 업로드

```
책등 컴포저블과 사진 업로드 기능을 만들어줘.

[BookSpine(book, widthDp, heightDp)] 표시 우선순위
1) spineLocalPath(사용자 업로드) 2) 예스24 책등 이미지 3) spineMode에 따라:
   - BLANK(기본): 회색 책등 + 작은 카메라 아이콘, 두께가 충분하면 세로 제목을 옅게
   - AUTO: 표지 대표색(Palette, spineColor로 캐시) 배경 + 세로 제목/저자,
           배경 명도에 따라 글자색 자동, 얇으면(<12dp) 제목만, 아주 얇으면 글자 생략
   - DEFAULT: 설정의 앱 전체 기본값을 따름
- 공통: 좌우 가장자리 그라데이션 그림자로 입체감
- 한글 제목은 글자를 세워서 위→아래, 영문은 90도 회전

[책 상세 화면]
- 책등 크게 보기 + 표지, 제목/저자/출판사, 읽은 날짜·별점 편집
- "책등 사진 등록": 카메라/갤러리 → 크롭(비율 두께:높이 기본, 자유 비율 허용) → 저장
- "표지 사진 등록": 표지 없을 때, 등록 후 spineColor 재추출
- 책등 표시 방식 선택: 기본값 따름 / 빈 책등 / 자동 생성 / (업로드 사진 삭제)
- 두께·높이(mm) 수정: 슬라이더 + 숫자 입력, 미리보기 실시간 반영
  (수정으로 책장이 넘치면 프롬프트 5의 넘침 처리 실행)
- 업로드 이미지는 최대 높이 1200px로 리사이즈해 저장
```

---

## 프롬프트 5 — 책장 레이아웃 (3칸 고정, 넘치지 않게)

```
책장 화면의 레이아웃 엔진과 렌더링을 만들어줘. 계산은 순수 Kotlin 클래스 + JUnit 테스트.

[ShelfLayoutEngine]
입력: 정렬된 책 리스트, rowCount(3), rowWidthMm(500), rowHeightMm(280), 가용 영역(dp)
- scale = min(가용너비 / rowWidthMm, (가용높이/rowCount - 선반판두께) / rowHeightMm)
- 책 너비 = thicknessMm × scale, 높이 = min(heightMm, rowHeightMm × 0.95) × scale
- 왼쪽부터 채우다가 칸 너비를 넘으면 다음 칸으로
- 칸 너비보다 두꺼운 책 한 권은 칸 너비에 맞게 축소
- 출력: rows[{ items[{bookId, x, width, height}], usedMm, freeMm }], overflow: List<bookId>
테스트: 넘침 없음, 순서 유지, 빈 리스트, 아주 두꺼운 책, 정확히 꽉 찬 경우, overflow 계산

[넘침 처리 - 칸 수 고정]
- 책 추가/두께 수정/다른 책장에서 이동 후 overflow가 생기면 다이얼로그:
  "책장이 꽉 찼어요. '{이름} 2' 책장을 만들어 넘친 책 N권을 옮길까요?"
  → 예: 같은 스타일로 새 책장 생성 후 overflow 책 이동 / 아니오: 작업 취소(되돌림)
- 이름이 이미 있으면 3, 4…로 증가

[렌더링]
- 책장 하나 = 화면 한 장(스크롤 없음), 책장 간 전환은 좌우 스와이프(HorizontalPager)
- 스타일별 그리기(Canvas): 원목(나뭇결+따뜻한 톤) / 화이트 / 블랙, 각 칸 아래 선반 판 그림자
- 책은 선반 바닥 정렬, 남은 공간은 비워 둠
- 상단: 책장 이름, 권수, 남은 공간(%) 표시
- 책 탭 → 책 상세 / 길게 누르기 → 드래그(프롬프트 7)
```

---

## 프롬프트 6 — 책장 폴더 & 정렬

```
여러 책장과 정렬 기능을 만들어줘.

[책장 관리]
- 상단 책장 칩(가로 스크롤) + "+" 새 책장, 스와이프와 칩 선택 동기화
- 새 책장: 이름, 스타일(원목/화이트/블랙) 선택, "기존 책장 복제" 옵션
  (복제 시 책과 순서를 그대로 복사 → 다른 버전으로 정리)
- 책장 이름 변경/스타일 변경/삭제/순서 변경 (삭제 확인, 책 데이터는 유지)
- 책 상세에서 "다른 책장에도 꽂기 / 이 책장에서 빼기 / 다른 책장으로 옮기기"
  (꽂는 책장이 넘치면 프롬프트 5의 넘침 처리)

[정렬] 책장별 저장(Shelf.sortMode, sortDesc)
- 등록순(addedAt) / 이름순(한글 가나다 → 영문 → 숫자, Collator(Locale.KOREAN)) /
  읽은 날짜순(readDate, 없는 책은 뒤로) / 내 순서(position)
- 오름/내림 토글
- 정렬이 바뀌면 레이아웃 재계산(권수가 같으니 넘침은 생기지 않음)
- 등록순/이름순/읽은 날짜순에서 드래그하면: 현재 보이는 순서를 position으로 저장 →
  CUSTOM으로 전환 → 스낵바 "내 순서로 바뀌었어요"
```

---

## 프롬프트 7 — 드래그로 순서 변경 & 저장

```
책장 안에서 책을 드래그해 순서를 바꾸고 저장하는 기능을 Compose로 만들어줘.
(두께가 제각각이라 LazyList 재정렬 라이브러리 대신 레이아웃 엔진 좌표 기반 커스텀 구현)

- pointerInput + detectDragGesturesAfterLongPress, 길게 누르면 햅틱 + 책이 들려 올라감(확대+그림자)
- 드래그 중: 손가락 위치로 삽입 지점 계산(칸 사이 이동 포함), 세로 인디케이터 표시,
  다른 책은 animate*AsState로 자리 비킴
- 놓으면: 리스트 재배열 → 레이아웃 재계산 → 해당 책장 ShelfItem.position 전체를
  Room @Transaction으로 한 번에 업데이트 (sortMode가 CUSTOM이 아니면 프롬프트 6 규칙으로 전환)
- 하단 "책장에서 빼기" 영역에 놓으면 제거 + 실행 취소 스낵바
- 왼쪽/오른쪽 화면 끝에 머물면 이웃 책장으로 옮기기 (넘치면 넘침 처리)
- 테스트: 재배열 로직 유닛 테스트, 앱 재시작 후 순서 유지 확인(Room 인메모리 테스트)
```

---

## 프롬프트 8 — S24+ UI 다듬기

```
Galaxy S24+ 기준으로 UI를 다듬어줘.

- 412x892dp 기준, 다른 크기에서도 책장 scale 자동 대응(한 화면에 3칸 유지)
- Edge-to-edge, 펀치홀·제스처 바 인셋 침범 금지
- 엄지 영역: 스캔 FAB, 정렬 버튼은 화면 하단 쪽
- 120Hz 부드러움: 드래그 중 불필요한 recomposition 제거(derivedStateOf, key, graphicsLayer)
- 책장 스타일별 색 토큰을 라이트/다크 모드 모두 정의
- 빈 책장: 빈 선반 + "첫 책을 스캔해보세요" 버튼
- 디버그 메뉴: 테스트용 책 80권 생성(넘침·새 책장 제안 흐름 확인용)
```

---

## 프롬프트 9 — 마무리 (백업·개인용 빌드)

```
마무리 작업을 해줘.

- 설정: 백업/복원 — Room DB + 이미지 폴더를 zip으로 내보내기/가져오기 (SAF 사용)
- 설정: 책등 없는 책 기본 표시 방식, 두께 추정 계수(0.06mm/쪽), API 키 상태 표시
- 앱 아이콘, 스플래시(SplashScreen API)
- 전체 유닛 테스트 통과 확인
- 개인용 서명 키 생성과 release APK 빌드·폰 설치 방법 안내 (스토어 출시 준비는 제외)
```

---

## 이후 확장 (2단계)
- **클라우드 동기화**: Firebase(Firestore + Storage) 또는 Supabase, 구글 로그인. `updatedAt`·`deleted` 기반 병합
- 책장 화면을 이미지로 저장·공유
- 책등 여러 권을 한 장에 찍어 자동으로 나누기

## 진행 팁
- 시작 전에 준비할 것: 알라딘 TTB 키, 카카오 REST API 키, (가능하면) 예스24 오픈 API 키
- 한 번에 한 프롬프트씩, 실행 확인 후 다음 단계로
- 버그는 "프롬프트 N의 X가 Y 상황에서 Z처럼 동작해. 원인 찾아 고쳐줘"처럼 구체적으로
