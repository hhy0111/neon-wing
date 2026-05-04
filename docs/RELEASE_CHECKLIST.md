# Release Checklist

| Stage | 담당 에이전트 | 결과 | 문제점 | 수정 지시 | 승인 여부 |
|---|---|---|---|---|---|
| 요구사항 분석 | Command Agent | Android/Google Play/광고+IAP/2D 슈팅/개인 개발 범위로 정리 | 전체 요구가 큼 | MVP와 출시 후보 범위 분리 | Approved |
| 게임 컨셉 확정 | Game Design Lead | Neon energy vertical shooter | 콘텐츠 소모 빠름 | 반복 가능한 전투 루프 우선 | Approved |
| GDD 작성 | Communication Agent | `docs/GDD.md` 작성 | 실제 스테이지 데이터 부족 | 다음 마일스톤에서 stage data 추가 | Approved |
| 수익 구조 설계 | Dev Agent B | 광고/결제 Gateway 분리 | 실제 SDK 미연동 | AdMob/Billing SDK 교체 필요 | Conditional Approved |
| 시스템 설계 | Dev Lead | SurfaceView 단일 루프, 로컬 저장 | 장기 유지보수용 모듈화 추가 필요 | Stage/Enemy config 분리 예정 | Approved |
| 아트 방향 정의 | Visual Lead | Canvas 네온/폭발/레이저 방향 확정 | 스프라이트 자산 없음 | 출시 전 아이콘/스토어 이미지 제작 | Conditional Approved |
| MVP 정의 | Command Agent | 플레이/성장/광고 더미/IAP 더미 포함 | 스테이지/보스/사운드 부족 | 다음 반복에서 콘텐츠 확장 | Approved |
| 출시 콘텐츠 스케일 | Game Design Lead | 5개 전장, 시간 기반 배경 전환, 일반 적 12종, 엘리트 5종, 보스 5종, 미사일 7종으로 정리 | 제작 에셋과 스테이지 데이터가 많음 | `docs/CONTENT_SCALE_PLAN.md` 기준으로 1개 전장씩 구현 | Approved for planning |
| 전장 개조/기체 상점 | Game Design Lead | 점수/처치 기반 전장 개조 + 특수 드롭 보너스, 기체별 미사일, 기체 선택/구매 조건 화면으로 정리 | 전투 밸런스와 UI 복잡도 증가 | `docs/RUN_UPGRADE_AND_SHIP_PLAN.md` 기준으로 웹에서 3기체/기본 업그레이드부터 검증 | Approved for planning |
| 로컬 랭킹 | Dev Lead | 웹 localStorage와 Android SharedPreferences에 상위 50위 로컬 랭킹 저장/표시 | 온라인 전체 유저 랭킹은 아님 | 출시 후 필요 시 Google Play Games Services 또는 서버 Gateway 추가 | Approved for local MVP |
| 개발 진행 | Dev Agent A/B | Android 프로젝트와 첫 플레이어블 구현, `assembleDebug` 성공 | 실제 SDK와 사운드 없음 | 다음 마일스톤에서 SDK/사운드/스테이지 데이터 추가 | Approved for MVP |
| 내부 검수 | Dev Lead | 엔티티 수 제한, 단순 렌더링, lint 통과 | 실제 기기 성능 미측정 | `adb shell dumpsys gfxinfo`와 실제 기기 5분 플레이로 확인 | Conditional Approved |
| QA 1회차 | QA Agent A | Pixel_7_API_35 설치/실행/입력 smoke QA 통과 | 실제 기기 미검증 | 실제 Android 기기 1개에서 회귀 | Approved for smoke QA |
| QA 2회차 | QA Agent B | 플레이 감각 테스트 예정 | 밸런스 데이터 없음 | 3~5분 플레이 로그 기록 | Pending |
| QA 3회차 | QA Lead | 출시 후보 검증 예정 | 실제 SDK 미연동 | SDK 연동 후 회귀 | Pending |
| 밸런스 조정 | Balance Lead | 초벌 난이도 계수 구현 | 유지율 판단 불가 | 보상/스폰/HP 조정 | Pending |
| 출시 준비 | Command Agent | 체크리스트 생성 | 서명키, 스토어 등록, 정책 문서 필요 | release keystore와 Play Console 설정 | Pending |
