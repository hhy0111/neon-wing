# Web Testing Harness

| Item | Content |
|---|---|
| 담당 에이전트 | Dev Agent A, Dev Agent B, QA Agent A |
| 결과 | `web/` 아래에 브라우저용 Canvas 테스트 하네스를 추가했다. Android 빌드를 기다리지 않고 전투 조작감, UI 플로우, 성장/상점 mock, 광고/결제 mock을 확인할 수 있다. |
| 문제점 | 웹 버전은 Android Java 코드의 1:1 런타임이 아니라 테스트용 포트다. 실제 출시 검증은 Android APK/AAB에서 다시 해야 한다. |
| 수정 지시 | 밸런스 수치를 바꾸면 Android `NeonWingView.java`와 웹 `game.js`를 함께 갱신한다. 장기적으로는 stage/enemy/economy 설정을 JSON으로 분리해 양쪽에서 공유한다. |
| 승인 여부 | Approved for web testing |

## 실행

```powershell
npm run web
```

브라우저에서 접속:

```text
http://127.0.0.1:5173
```

## 자동 테스트

서버가 실행 중인 상태에서:

```powershell
npm run web:test
npm run web:test:hangar
npm run web:test:ships
npm run web:test:leaderboard
```

테스트 산출물:

```text
output/web-game/
output/web-hangar/
output/web-ships/
output/web-leaderboard/
```

더 긴 전투 반복이 필요하면 `web:test` 명령의 `--iterations` 값을 수동으로 늘린다. 현재 로컬 환경에서는 Chromium 실행 비용이 커서 기본값을 빠른 smoke 테스트용 1회로 둔다.

## 웹 전용 테스트 API

브라우저 콘솔이나 Playwright에서 사용할 수 있다.

```javascript
window.render_game_to_text()
window.advanceTime(1000)
```

`render_game_to_text()`는 현재 모드, 플레이어 HP/위치, 적/탄 개수, 버튼 좌표, 경제 상태를 JSON 문자열로 반환한다.
