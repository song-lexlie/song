# 내 책장

읽은 책을 바코드나 제목으로 찾아 **책등 이미지로 책장에 꽂아 두는** 개인용 안드로이드 앱입니다.
개발 계획: [`docs/PROMPT_PLAN.md`](docs/PROMPT_PLAN.md)

## 현재 단계
- [x] 프롬프트 1: 프로젝트 셋업, Room 데이터 계층, 기본 책장, 하단 탭
- [ ] 프롬프트 2: 책 검색 (알라딘 → 카카오, 예스24 책등)
- [ ] 프롬프트 3~9

## 빌드하기
1. [Android Studio](https://developer.android.com/studio) 최신 버전 설치
2. `File > Open` 으로 이 폴더 열기 → Gradle Sync 가 끝날 때까지 기다리기
3. `local.properties.example` 을 참고해 `local.properties` 에 API 키 추가 (없어도 앱은 실행됨)
4. 실행할 기기 선택 후 ▶ Run

### Galaxy S24+ 에뮬레이터 만들기
`Device Manager > + > New Hardware Profile`
- 화면: 6.7", 해상도 1080 × 2340 (기본 FHD+ 설정과 같음, 약 412×892dp)
- 시스템 이미지: Android 14 (API 34) 이상

### 실제 폰에 설치
폰에서 `설정 > 휴대전화 정보 > 소프트웨어 정보 > 빌드번호` 7번 탭 → 개발자 옵션에서 USB 디버깅 켜기 → USB 연결 후 Run.

## 테스트
```bash
./gradlew connectedAndroidTest   # 에뮬레이터나 폰이 연결된 상태에서
```
