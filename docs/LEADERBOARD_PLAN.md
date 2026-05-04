# Neon Wing Leaderboard Plan

이 문서는 점수 기록과 상위 50위 랭킹 저장 위치를 정리한다.

## 저장 위치 결정

현재 프로젝트는 서버 없는 개인 개발 MVP 구조이므로 첫 출시 랭킹은 `기기 로컬 랭킹`으로 둔다.
온라인 전체 유저 랭킹은 서버 또는 Google Play Games Services 연동이 필요하므로 출시 후보 이후 별도 마일스톤으로 분리한다.

| 플랫폼 | 저장 위치 | 키 |
|---|---|---|
| Web harness | `localStorage` | `neon-wing-web-leaderboard-v1` |
| Android release | `GameRepository` SharedPreferences | `leaderboard_top_50` |

## 저장 데이터

상위 50개만 점수 내림차순으로 보관한다.

```json
{
  "id": "run-id",
  "score": 2450,
  "kills": 78,
  "time": 284.2,
  "ship": "neon_wing",
  "shipName": "네온 윙",
  "phase": "보스 접근",
  "at": "2026-05-03T12:00:00.000Z"
}
```

## 정렬 기준

1. 점수 높은 순
2. 처치 수 높은 순
3. 기록 시간이 빠른 순

## 현재 구현

웹 테스트판과 Android 디버그 빌드에 점수 기록과 `랭킹 TOP 50` 화면을 구현했다.

| 화면 | 동작 |
|---|---|
| 타이틀 | `랭킹 TOP 50` 버튼으로 로컬 랭킹 확인 |
| 게임오버 | 점수 기록 후 로컬 순위 표시 |
| 랭킹 | 10개씩 페이지 표시, 최대 50위까지 확인 |

## 다음 이식 기준

Android에서는 `GameRepository`에 아래 메서드를 추가했다.

```text
recordLeaderboardScore(score, kills, time, shipId, shipName, phase)
getLeaderboardTop50()
```

온라인 랭킹이 필요해지면 로컬 랭킹은 그대로 두고, 별도 `OnlineLeaderboardGateway`를 만들어 Google Play Games Services 또는 서버 API와 분리한다.
