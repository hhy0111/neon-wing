# Neon Wing GDD

## 1. Requirements Analysis

| Item | Content |
|---|---|
| 담당 에이전트 | Command Agent, Communication Agent |
| 결과 | Google Play 전용 Android 세로형 2D 비행 슈팅. 개인 개발자 기준으로 서버 의존 없이 로컬 저장, 보상형 광고, Google Play Billing 연결 지점 중심으로 설계한다. |
| 문제점 | 고퀄리티 연출, 광고, IAP, 성장, QA를 모두 완성하려면 범위가 쉽게 커진다. |
| 수정 지시 | 첫 출시 범위는 Canvas 기반 전투, 기본 성장, 보상형 광고 지점, 결제 상품 구조까지로 제한한다. 온라인 랭킹, PvP, 가챠, 복잡한 서버 기능은 제외한다. |
| 승인 여부 | Approved |

## 2. Game Concept

| Item | Content |
|---|---|
| 담당 에이전트 | Game Designer, Game Design Lead |
| 결과 | "조작은 단순, 연출은 압도적"을 기준으로 드래그 이동 + 자동 사격 + 유도 미사일 + 레이저 + 광역 폭탄을 핵심 손맛으로 둔다. |
| 문제점 | 장시간 스테이지와 복잡한 탄막은 모바일 한 손 조작에서 피로도가 크다. |
| 수정 지시 | 한 판은 2~4분 체감으로 설계하고, MVP는 무한 웨이브 기반으로 재미와 성능을 먼저 검증한다. |
| 승인 여부 | Approved |

## 3. Core Loop

| Item | Content |
|---|---|
| 담당 에이전트 | Game Designer, Balance Lead |
| 결과 | 출격 -> 적 처치/스코어 획득 -> 사망/보상 -> 코인 강화 -> 재도전. 보상형 광고는 부활, 전투 보상 2배, 시작 버프, 드론 복구에만 사용한다. |
| 문제점 | 광고 보상이 너무 크면 광고 의존 성장으로 보인다. |
| 수정 지시 | 광고 미시청 유저도 기본 보상을 받고, 광고는 선택형 가속으로만 유지한다. |
| 승인 여부 | Approved |

## 4. Monetization

| Item | Content |
|---|---|
| 담당 에이전트 | Dev Agent B, Game Design Lead |
| 결과 | `AdsGateway`, `BillingGateway`, `ProductIds`, `GameRepository`로 수익화 연결 지점을 분리했다. 현재 빌드는 실제 SDK 대신 개발용 더미 지급을 사용한다. |
| 문제점 | 실제 출시에는 AdMob 앱 ID/광고 단위 ID, Play Console 상품 ID, Billing token 검증 흐름이 필요하다. |
| 수정 지시 | 출시 전 `AdsGateway`를 AdMob RewardedAd로, `BillingGateway`를 Google Play BillingClient로 교체하고 테스트 구매/복원을 검증한다. |
| 승인 여부 | Approved for MVP / Rejected for production store build until real SDK integration |

## 5. Systems

| Item | Content |
|---|---|
| 담당 에이전트 | Dev Agent A, Dev Lead |
| 결과 | Android `SurfaceView` 게임 루프, 자동 사격, 유도 미사일, 레이저, 노바 폭탄, 적 웨이브, 충돌, 폭발/파티클, 드론 지속시간과 광고 복구를 구현했다. |
| 문제점 | 현재는 절대적인 출시 밸런스가 아니라 1차 플레이 가능한 기준값이다. |
| 수정 지시 | 실제 기기 테스트 후 탄속, 스폰 간격, 적 HP, 보상량을 3회 이상 조정한다. |
| 승인 여부 | Approved for first playable |

## 6. Visual Direction

| Item | Content |
|---|---|
| 담당 에이전트 | Visual Designer, Visual Design Lead |
| 결과 | 어두운 메탈 배경, 청록 아군, 적색/마젠타 적, 금색 미사일, 흰색 코어 레이저로 역할 색상을 고정했다. 스프라이트 없이 Canvas Path, gradient, ring, beam, particle로 저비용 네온 연출을 구성한다. |
| 문제점 | 폭발과 글로우가 과하면 피격 판정이 흐려진다. |
| 수정 지시 | 적 탄은 항상 빨강/주황/마젠타, 아군 탄은 청록/파랑/흰색으로 유지하고 배경 alpha를 낮게 유지한다. |
| 승인 여부 | Conditional Approved |

## 7. MVP Definition

| Item | Content |
|---|---|
| 담당 에이전트 | Command Agent |
| 결과 | 첫 실행 가능한 MVP는 `START SORTIE`, `HANGAR`, 코인 업그레이드, 프리미엄 상품 더미 구매, 보상형 광고 더미 콜백, 게임오버 보상, 재도전을 포함한다. |
| 문제점 | 보스 3종, 스테이지 12개, 실제 결제/광고 SDK는 아직 미완성이다. |
| 수정 지시 | 다음 마일스톤에서 스테이지 데이터, 보스 패턴, 실제 SDK 연동, 사운드, 앱 아이콘/스토어 그래픽을 추가한다. |
| 승인 여부 | Approved |

## 8. Release Content Scale

| Item | Content |
|---|---|
| 담당 에이전트 | Game Design Lead, Visual Lead, Balance Lead |
| 결과 | 출시판 콘텐츠 스케일은 5개 전장, 전장별 3중 패럴랙스 배경, 한 판 4~6분, 시간 기반 배경/스폰 전환, 일반 적 12종, 엘리트 5종, 보스 5종, 플레이어 미사일 7종으로 확장한다. 세부 내용은 `docs/CONTENT_SCALE_PLAN.md`에 정리했다. |
| 문제점 | 배경 15장, 적/보스/미사일 스프라이트, 스테이지 데이터 분리까지 필요하므로 제작량이 MVP보다 크게 늘어난다. |
| 수정 지시 | 1차 구현은 `Neon Orbit` 1개 전장으로 배경 전환/적 로스터/보스전 구조를 검증하고, 이후 `Scrap Belt`, `Crimson Foundry`, `Frost Relay`, `Void Citadel` 순서로 확장한다. |
| 승인 여부 | Approved for release planning |

## 9. Run Upgrade and Ship Collection

| Item | Content |
|---|---|
| 담당 에이전트 | Game Design Lead, Economy Lead, UI Lead |
| 결과 | 한 판 안에서만 적용되는 `전장 개조` 시스템을 추가한다. 기본 획득은 점수/처치 기반 전장 에너지 레벨업으로 보장하고, 보급 드론/엘리트/보스 처치 시 개조 칩 드롭으로 보너스 선택을 제공한다. 기체마다 기본 미사일과 전용 업그레이드가 다르며, 기체 선택/상점/구매 조건 화면을 추가한다. 세부 내용은 `docs/RUN_UPGRADE_AND_SHIP_PLAN.md`에 정리했다. |
| 문제점 | 전장 개조, 기체별 미사일, 상점 UI가 동시에 들어가면 밸런스와 UI 복잡도가 올라간다. |
| 수정 지시 | 1차 구현은 `Neon Wing`, `Raptor`, `Astra` 3기와 미사일 피해/재장전/추가 발사 업그레이드만 웹에서 검증한 뒤 확장한다. |
| 승인 여부 | Approved for release planning |

## 10. Local Leaderboard

| Item | Content |
|---|---|
| 담당 에이전트 | Dev Lead, UI Lead |
| 결과 | 점수, 처치 수, 생존 시간, 기체, 단계 정보를 기록하고 상위 50위까지 보여주는 로컬 랭킹을 추가한다. 웹은 `localStorage`의 `neon-wing-web-leaderboard-v1`, Android는 `GameRepository` SharedPreferences의 `leaderboard_top_50`에 저장한다. 세부 내용은 `docs/LEADERBOARD_PLAN.md`에 정리했다. |
| 문제점 | 현재는 기기 로컬 랭킹이라 전체 유저 온라인 순위가 아니다. |
| 수정 지시 | 온라인 랭킹이 필요해지면 Google Play Games Services 또는 서버 API를 별도 Gateway로 분리한다. |
| 승인 여부 | Approved for local MVP |
