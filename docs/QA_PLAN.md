# QA Plan

## QA Loop 1: Functional Test

| Item | Content |
|---|---|
| 담당 에이전트 | QA Agent A |
| 결과 | Pixel_7_API_35 에뮬레이터에서 설치, 실행, START SORTIE 탭, 드래그 입력, 프로세스 생존, 앱 PID 기준 crash/ANR 로그 없음 확인. 증거: `qa/neonwing_smoke.png`. |
| 문제점 | 실제 기기 입력 지연과 장기 플레이 성능은 아직 확정할 수 없다. |
| 수정 지시 | 실제 Android 기기 1개에서 5분 플레이, 백그라운드/복귀, 반복 재시작을 추가 검증한다. |
| 승인 여부 | Approved for smoke QA |

## QA Loop 2: Play Test

| Item | Content |
|---|---|
| 담당 에이전트 | QA Agent B |
| 결과 | 3~5분 플레이 기준으로 탄 가독성, 사망 원인 인지, 보상감, 재도전 욕구를 확인한다. |
| 문제점 | 현재 수치는 개발자 기준 초벌값이라 무료 유저 3일 유지 판단은 불가능하다. |
| 수정 지시 | 첫 세션 8~10분, 첫 3판 내 업그레이드 1회 가능, 광고 미시청 진행 가능 여부를 체크한다. |
| 승인 여부 | Pending |

## QA Loop 3: Release Candidate

| Item | Content |
|---|---|
| 담당 에이전트 | QA Lead |
| 결과 | 10회 재시작, 5분 연속 플레이, 백그라운드/복귀, 저사양 성능, 결제/광고 테스트 모드를 확인한다. |
| 문제점 | 실제 AdMob/Billing SDK가 아직 더미라 스토어 출시 검증은 완료되지 않았다. |
| 수정 지시 | 실제 SDK 연결 후 테스트 광고 ID, 라이선스 테스터, 구매 복원, 네트워크 실패, 광고 미로드 상태를 재검증한다. |
| 승인 여부 | Rejected for production / Approved for MVP QA |

## Bug Report Format

```text
Bug ID:
Title:
Build:
Device / OS:
Severity: Blocker / Critical / Major / Minor
Frequency: Always / Often / Sometimes / Once
Preconditions:
Steps:
1.
2.
3.
Expected:
Actual:
Evidence:
Owner:
Status: Open / Fixed / Retest / Closed
```

## Release Gate

| Item | Content |
|---|---|
| 담당 에이전트 | QA Lead, Dev Lead, Balance Lead |
| 결과 | Blocker/Critical 0개, 30fps 이하 장기 하락 없음, 광고/IAP 실패가 진행을 막지 않음, 무료 유저 성장 가능성이 검증되면 출시 후보로 승인한다. |
| 문제점 | 현재는 MVP 구현 직후라 QA 반복이 아직 완료되지 않았다. |
| 수정 지시 | 빌드 성공 후 3회 QA 로그를 갱신한다. |
| 승인 여부 | Pending |
