Original prompt: 웹으로 게임 테스트 하고 싶어 환경 만들어줘

## 2026-05-03

- Decision: Create a separate static web test harness under `web/` instead of trying to run Android Java in browser.
- Goal: Browser-playable Canvas version for movement, combat feel, economy, ad/IAP mock flows, and automated Playwright smoke testing.
- Requirement from skill: expose `window.render_game_to_text` and `window.advanceTime(ms)`.
- Added `web/index.html`, `web/style.css`, `web/game.js`, `web/server.js`, gameplay/hangar Playwright action files, and `docs/WEB_TESTING.md`.
- Note: Web harness mirrors the MVP feel and UI flows but is not a 1:1 Android runtime. Shared JSON configs should be introduced later if balance iteration becomes frequent.
- Playwright note: one gameplay iteration takes roughly 40s on this machine because Chromium startup/teardown is slow. Default `npm run web:test` uses `--iterations 1`; increase manually for long runs.
- Verified:
  - `npm run web:test` passes with no console error artifacts; `output/web-game/shot-0.png` shows active gameplay.
  - `npm run web:test:hangar` passes with no console error artifacts; final state includes `CORE upgraded`.
  - Local server is running on `http://127.0.0.1:5173`.

## 2026-05-03 Korean UI Pass

- Changed Android app label and in-game UI/status strings to Korean.
- Changed web harness UI/status strings to Korean and switched canvas/CSS fonts to Korean-capable fallbacks.
- Verified Android compile/lint: `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` passes, lint reports no issues.
- Verified web tests: `npm run web:test` and `npm run web:test:hangar` pass. Screenshots show Korean title, HUD, hangar, and button labels.
- Android emulator visual smoke was attempted, but the emulator hit unrelated system process/System UI ANRs before launching the game; APK build itself is valid.

## 2026-05-03 Asset Prompt Pass

- Added `docs/ASSET_PROMPTS.md` for high-quality 2D image generation.
- Prompt scope covers 3-layer parallax backgrounds, player ships, support drone, enemy aircraft, boss candidates, and future missile types.
- Recommended asset baseline: background layers at `840x1520`, sprites at `512x512`, missiles at `256x512` or `512x512`, with transparent PNG for sprites/projectiles.
- Next step: generate selected images, place them under `web/assets/`, then replace procedural Canvas drawing with image-backed rendering in the web harness first.

## 2026-05-03 Release Content Scale Pass

- Added `docs/CONTENT_SCALE_PLAN.md` to define release-scale content beyond a single endless background.
- Launch target now covers 5 stages: Neon Orbit, Scrap Belt, Crimson Foundry, Frost Relay, and Void Citadel.
- Each sortie is planned as a 4~6 minute run with timed phase changes: approach, perimeter, breach, core, and boss.
- Release content target: 3-layer parallax backgrounds per stage, 12 normal enemies, 5 elites, 5 bosses, 7 player missile types, and 6 enemy projectile/threat patterns.
- Extended `docs/ASSET_PROMPTS.md` with additional prompts for stage backgrounds, new enemies, elites, bosses, enemy projectiles, and map thumbnails.
- Updated `docs/GDD.md` and `docs/RELEASE_CHECKLIST.md` to reference the release content scale plan.

## 2026-05-03 Run Upgrade and Ship Shop Pass

- Added `docs/RUN_UPGRADE_AND_SHIP_PLAN.md` for sortie-only upgrades, ship-specific missiles, ship unlocks, and shop/selection screen structure.
- Decision: use score/kill-based field energy as the guaranteed upgrade path, with supply drone/elite/boss upgrade chip drops as bonus moments.
- Ship plan now differentiates Neon Wing, Raptor, Astra, Bastion, Seraph, Phantom, and Nova X by default missile, passive, exclusive upgrades, and unlock/purchase conditions.
- Added shop/selection/unlock UI requirements: hangar main, ship selection, purchase condition screen, and shop screen.
- Extended `docs/ASSET_PROMPTS.md` with prompts for additional player ships, hangar/selection UI backgrounds, upgrade panel backgrounds, and currency/upgrade icons.
- Updated `docs/GDD.md`, `docs/CONTENT_SCALE_PLAN.md`, and `docs/RELEASE_CHECKLIST.md` to reference the new system.

## 2026-05-03 Web Combat Feel Implementation Pass

- Implemented a first web prototype of sortie-only field upgrades in `web/game.js`.
- Added field energy, upgrade chips, 3-choice upgrade overlay, run-only upgrade stacks, energy/chip/repair pickups, and supply drone spawns.
- Added ship collection data and web UI flow for ship selection and locked ship purchase conditions.
- Added ship-specific missile behavior for Neon Wing, Raptor, Astra, Bastion, Seraph, Phantom, and Nova X through data profiles; first playable focus remains Neon Wing/Raptor/Astra.
- Added more procedural combat VFX: missile shapes/trails, muzzle flashes, engine trails, screen flash, shield burst, phase-colored background motion, chip/energy pickup visuals, and upgraded explosion feedback.
- Added `docs/EFFECT_ASSET_PROMPTS.md` for VFX image prompts to use when effect sprites are generated later.
- Added `web/test-actions-ships.json` and `npm run web:test:ships`.
- Verified:
  - `node --check web\game.js` passes.
  - `npm run web:test` passes; final state reached `mode: "upgrade"` with 3 field upgrade choices visible.
  - `npm run web:test:hangar` passes.
  - `npm run web:test:ships` passes; final state shows locked Raptor purchase condition screen.
- Next implementation suggestion: after generated images arrive, wire `web/assets/` image loading into the same ship/missile/effect IDs before porting to Android.

## 2026-05-03 Local Leaderboard Pass

- Storage decision: first release uses device-local leaderboards, not online global ranking.
- Web storage: `localStorage` key `neon-wing-web-leaderboard-v1`.
- Android storage: `GameRepository` SharedPreferences key `leaderboard_top_50`.
- Implemented web leaderboard recording and `랭킹 TOP 50` screen in `web/game.js`.
- Implemented Android leaderboard persistence in `GameRepository` and a `LEADERBOARD` screen in `NeonWingView`.
- Added `docs/LEADERBOARD_PLAN.md`, updated `docs/GDD.md`, `docs/RELEASE_CHECKLIST.md`, and `docs/WEB_TESTING.md`.
- Added `web/test-actions-leaderboard.json` and `npm run web:test:leaderboard`.
- Verified:
  - `node --check web\game.js` passes.
  - `npm run web:test:leaderboard` passes; final state is `mode: "leaderboard"`.
  - `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` passes.
- Note: `npm run web:test`, `web:test:hangar`, and `web:test:ships` generated valid latest state/screenshot artifacts, but recent runs hit command timeout because local Chromium teardown is slow. No leftover Playwright node process remained.

## 2026-05-04 Image Asset Integration Pass

- Prepared generated PNGs from `image/ASSET_PROMPTS/` into `web/assets/` with `scripts/prepare_web_assets.ps1`.
- Applied image-backed rendering in `web/game.js` for 5 stage background sets, main player ship sprites, normal/elite/supply enemies, and all player missile profile sprites.
- Background now uses 3 scrolling parallax layers and switches stage asset sets by run time: Neon Orbit, Scrap Belt, Crimson Foundry, Frost Relay, and Void Citadel.
- Main player hit point is separated from the visual sprite: `hitR` is 12px and collisions now use `playerHitRadius()` instead of the full ship sprite size.
- Added a subtle in-game hit point ring/dot at the player core so the visual 타점 matches the actual collision center.
- Added `assets` and `player.hitR` to `window.render_game_to_text` for Playwright/state verification.
- Verified:
  - `node --check web\game.js` passes.
  - `npm run web:test` passes; latest state reports `assets.loaded: 29`, `failed: 0`, `playerSprite: true`, `hitR: 12`.
  - `npm run web:test:leaderboard` passes; latest screenshot shows image-backed parallax background and intact ranking UI.
- Note: the main ship source image has a dark metal body, so rendering applies brightness/contrast/saturation and cyan glow to keep it visible against dark space backgrounds while preserving the 12px hitbox.

## 2026-05-04 Pre-Run Magnet Upgrade Pass

- Moved pickup magnet growth out of the sortie-only upgrade pool.
- Removed `up_magnet` from web in-run field upgrade choices so combat upgrade choices stay focused on weapons, drones, defense, and nova.
- Added permanent `magnet` upgrade state to web save data and Android SharedPreferences.
- Added a `자석` upgrade row to the web and Android hangar screens before mission start.
- Pickup attraction radius now uses permanent magnet level as the primary source:
  - Web: `72 + magnetLevel * 20 + droneLevel * 4`
  - Android: same formula scaled by `unit`
- Added `web:test:magnet` and `web/test-actions-magnet.json` to verify the hangar magnet upgrade flow.
- Verified:
  - `node --check web\game.js` passes.
  - `npm run web:test:magnet` passes; latest state shows `magnet: 2` and `magnetRadius: 116` after purchase.
  - `npm run web:test:hangar` passes.
  - `npm run web:test` passes; latest in-run upgrade choices do not include `up_magnet`.
  - `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` passes, lint reports no issues.

## 2026-05-04 UX Tuning Pass

- Reviewed leaderboard `이전`/`다음` buttons: they are TOP 50 pagination controls for 10 records per page.
- Updated web leaderboard so `이전`/`다음` only appear when there are 2+ pages; with 0~10 records it now shows page info and a short note instead.
- Changed pointer movement from distance-proportional following to capped-speed travel. Long clicks now move the ship at max speed instead of snapping faster across large distances.
- Matched Android movement to the same capped-speed behavior.
- Reduced web player ship render size while keeping the core hit radius at 12px.
- Replaced vector ship card markers with image-backed previews for all ship cards by reusing high-quality ship sprites with per-ship color filters.
- Updated pickup visuals so coins, energy, upgrade chips, and repair kits use distinct filled shapes instead of similar rings.
- Added a visible missile exhaust streak so missiles read differently from pickup items.
- Added `web/test-actions-ship-list.json` and `npm run web:test:ship-list` for ship-list visual regression checks.
- Verified:
  - `node --check web\game.js` passes.
  - `npm run web:test:ship-list` passes and screenshot shows image-backed ship cards.
  - `npm run web:test:leaderboard` passes; screenshot confirms pagination buttons are hidden for one page.
  - `npm run web:test:ships` passes; screenshot confirms image-backed ship detail.
  - `npm run web:test` passes; screenshot shows smaller player ship and differentiated pickup shapes.
  - `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` passes, lint reports no issues.

## 2026-05-04 Missile Upgrade Feel and Exit Pass

- Reworked missile run upgrades so they feel less like minor stat bumps:
  - `고폭 탄두`: damage +28% per stack and stronger impact visuals.
  - `고속 장전기`: reload -18% and immediately primes another missile shot after selection.
  - `다연장 포드`: clearer extra launch lanes and less severe secondary missile damage penalty.
  - `분열 탄두`: more fragments, visible shard shots, and chain-burst feedback.
  - Added `추적 코어`, `미사일 폭풍`, and `초신성 탄두`.
- Missile visuals now scale with upgrade power: larger sprite scale, longer trails, glow rings, stronger exhaust streaks, bigger splash, shockwaves, and screen shake.
- Added a small in-HUD missile stack meter after missile-related upgrades are selected.
- Added a player ship aura/engine glow layer to improve premium feel without requiring new image files.
- Added `나가기` during gameplay on web and Android.
  - Web exits to the gameover/reward screen with current score, best score, and leaderboard recording.
  - Android uses the same `전투 종료` gameover/reward flow.
- Added `web/test-actions-exit.json` and `web/test-actions-missile-upgrade.json` plus package scripts `web:test:exit` and `web:test:missile-upgrade`.
- Verified:
  - `node --check web\game.js` passes.
  - `npm run web:test:exit` passes; screenshot shows `전투 종료`.
  - `npm run web:test` passes; upgrade choices include the new missile upgrades.
  - `npm run web:test:missile-upgrade` passes; screenshot shows active play after a missile upgrade, missile meter, upgraded missile trail, and `나가기` button.
  - `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` passes, lint reports no issues.

## 2026-05-04 Economy Icon and Balance Pass

- Replaced top HUD coin/gem/best text labels with compact canvas-drawn icon pills on web and Android.
- Changed reward and upgrade cost rows to use currency icons where the UI is displaying currency quantities.
- Added a green cash pickup/object type to combat:
  - Web: cash pickups spawn from enemy kills and are reported in `render_game_to_text`.
  - Android: cash pickups use the same score-reward behavior and distinct green bill icon.
- Increased visible combat density toward the requested 30~50 object feel:
  - Web caps: player bullets 190, enemy bullets 150, enemies 30, particles 520, pickups 130, float texts 32.
  - Android caps now match those limits.
  - Enemy wave count ramps earlier and later waves can add more bodies.
- Increased player ship sprite render size again while keeping the small hit radius unchanged for fair collisions.
- Strengthened permanent missile progression:
  - Higher damage scaling per missile level.
  - More launch lanes at higher levels.
  - Level 5+ adds mini salvo missiles.
  - Visual scale, splash, speed, and turn bonuses now grow with permanent missile level as well as run upgrades.
- Added optional prompt entries in `docs/EFFECT_ASSET_PROMPTS.md` for gem currency, cash pickup, and best-score trophy icons if PNG UI assets are generated later.
- Verified:
  - `node --check web\game.js` passes.
  - `npm run web:test` passes; screenshot confirms icon HUD, larger player sprite, coin/cash pickups, and object density.
  - `npm run web:test:hangar` passes; screenshot confirms icon HUD outside combat.
  - `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` passes, lint reports no issues.
- Note: `npm run web:test:missile-upgrade` hung in the Playwright client during this pass and was stopped manually. The earlier basic client test covered live combat rendering, but the missile-upgrade-specific scenario should be retried after checking the local Playwright client/browser teardown behavior.

## 2026-05-04 Recheck Notes

- Retried `npm run web:test:missile-upgrade`.
  - The command still timed out during Playwright client shutdown and was stopped manually.
  - Before hanging, it produced fresh `output/web-missile-upgrade/shot-0.png` and `state-0.json`.
  - The screenshot/state confirm the requested runtime changes are active: icon HUD, `up_missile_extra` stack, missile upgrade choices including `미사일 폭풍` and `분열 탄두`, 37 pickups visible, and cash pickups present in `render_game_to_text`.
  - No `errors-*.json` console error artifact was generated.
- Re-ran `npm run web:test:exit`; it exits normally and screenshot confirms icon reward display.
- Re-ran `node --check web\game.js`; passes.
- Re-ran `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon`; build and lint pass.
- Confirmed no leftover `web_game_playwright_client` / `web:test` Node processes remained after stopping the hung missile-upgrade test.

## 2026-05-04 English Title Logo Prompt and Hook

- Added an English title logo prompt to `docs/ASSET_PROMPTS.md` for `web/assets/ui/title_logo_neon_wing_en.png`.
- Web now optionally loads `assets/ui/title_logo_neon_wing_en.png` and uses it on the title screen if present.
- Missing title logo PNG does not count as an asset failure; the web game keeps an English `NEON WING` fallback title until the generated image is added.
- Updated browser title/aria text to English and changed Android title fallback text to `NEON WING`.
- Verified:
  - `node --check web\game.js` passes.
  - Playwright title screenshot in `output/web-title/shot-0.png` confirms the English fallback title is visible and not overlapping controls.
  - `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` passes, lint reports no issues.

## 2026-05-04 Generated Effect Icon Integration

- Processed the new generated PNGs in `image/EFFECT_ASSET_PROMPTS/` into `web/assets/`:
  - `icons/icon_coin.png`
  - `icons/icon_gem.png`
  - `icons/icon_cash.png`
  - `icons/icon_trophy.png`
  - `icons/icon_upgrade_chip.png`
  - `icons/icon_field_energy.png`
  - `icons/icon_repair.png`
  - `effects/ship_unlock_burst.png`
- Updated `scripts/prepare_web_assets.ps1` so those effect/icon assets can be regenerated from the `image` folder.
- Updated `web/game.js` so HUD currency icons and pickup objects use the generated PNG icons first, with the previous canvas-drawn icons as fallback.
- Confirmed no English title logo image was present in `image/`; title remains the English fallback text until `title_logo_neon_wing_en.png` is added.
- Verified:
  - `node --check web\game.js` passes.
  - Playwright title screenshot confirms generated coin/gem/trophy HUD icons render.
  - `npm run web:test` passes; state reports `assets.loaded: 36`, `failed: 0`, and screenshot confirms generated coin/cash pickup icons render in gameplay.

## 2026-05-04 Opening Splash and Remaining Prompt Pack

- Added a first-run opening/splash screen before the title screen on web and Android.
  - It displays `NEON WING` and `클릭하여 실행`.
  - Click/tap transitions to the existing title/menu screen.
  - Web also supports Enter/Space on the splash screen.
- Added optional web support for `web/assets/ui/opening_splash.png`; if present via manifest it will replace the fallback splash art.
- Added `web/assets/asset_manifest.json` and updated `scripts/prepare_web_assets.ps1` to list optional UI/map assets only when their source files exist, avoiding 404 errors for not-yet-generated assets.
- Updated web Playwright action files to click through the splash screen before their existing flows.
- Added `docs/MISSING_IMAGE_PROMPTS_ONLY.md`, a separate 15-item prompt pack containing only images that are still missing:
  - opening splash,
  - English title logo,
  - five remaining player ships,
  - four UI backgrounds/panels,
  - four remaining map thumbnails.
- Verified:
  - `node --check web\game.js` passes.
  - All web test action JSON files parse.
  - Playwright splash screenshot in `output/web-splash/shot-0.png` confirms the opening screen.
  - Gameplay Playwright flow passes after clicking through the splash screen.
  - `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` passes, lint reports no issues.

## 2026-05-05 Upgrade Fallback Reward Pass

- Fixed the web upgrade overlay blank-state in `web/game.js`.
- When every sortie-only upgrade is already at max, `buildUpgradeChoices()` now returns three fallback reward choices instead of an empty array:
  - score bonus,
  - coin bonus,
  - mixed score/coin salvage bonus.
- Reward choices are handled separately from stack-based upgrades, update score/save data immediately, and no longer render a fake `Lv.N` line.
- Updated `render_game_to_text` so upgrade choices include `reward` payloads for QA visibility.
- Verified:
  - `node --check web\game.js` passes.
  - `npm run web:test` produced a fresh `output/web-game/shot-0.png` and `state-0.json`; the client timed out during shutdown again, but the gameplay upgrade overlay rendered correctly and assets/state output were refreshed.
- Note:
  - The exact "all upgrades exhausted" case was verified from the new source path (`available.length === 0` and empty weighted fallback) rather than a full automated endgame run, because exhausting the entire upgrade pool through Playwright is too slow for the current harness.

## 2026-05-05 Missile Balance and Premium Challenge Pass

- Slightly reduced baseline missile output in web and Android:
  - lowered the permanent missile base damage formula,
  - reduced secondary missile lane damage a bit,
  - reduced mini-missile damage a bit.
- Added a ship-specific `challenge` bonus on web for purchased higher-tier ships so stronger ships keep their premium feel while enemy pressure rises with them.
  - Applied as a small increase to normal enemy HP scaling, elite HP scaling, wave density chance, and spawn/elite timer pressure.
- Mirrored the same intent on Android with an Astra-owned premium challenge bonus and a small missile cadence/damage reduction.
- Added `ship.challenge` to web `render_game_to_text` for QA visibility.
- Added `web/test-actions-premium-balance.json` for a premium-ship balance scenario, though the generic Playwright client remained unreliable for that menu-heavy route on this machine.
- Verified:
  - `node --check web\game.js` passes.
  - `.\gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon` reports `BUILD SUCCESSFUL`.
  - `npm run web:test` still times out during Playwright shutdown, but it refreshed `output/web-game/shot-0.png` and `state-0.json`.
  - Ran an additional targeted Playwright script using the installed skill runtime to inject save data and compare:
    - `output/web-balance/default-neon-wing.json`
    - `output/web-balance/premium-astra.json`
    - matching screenshots in the same folder.

## 2026-05-05 Release AAB Build

- Verified release signing config is present in `local.properties` and the upload key exists at `release/neonwing-upload-key.jks`.
- Built a signed release Android App Bundle with:
  - `.\gradlew.bat :app:bundleRelease --no-daemon`
- Copied the fresh bundle to:
  - `release/NeonWing-v0.1.1-code2-release-20260505.aab`
- SHA-256:
  - `9B0414D959B9ECDF431EFFB8A7612632303DE45F7B526111D4427249F68728A0`
- Note:
  - Current app version is still `versionCode 2`, `versionName 0.1.1`. If code 2 has already been uploaded to Play Console before, the next store upload will require a higher `versionCode`.

## 2026-05-05 Release AAB Rebuild for Used Version Code

- Play Console rejected the previous bundle because `versionCode 2` was already used.
- Updated `app/build.gradle`:
  - `versionCode 3`
  - `versionName 0.1.1` unchanged
- Rebuilt the signed release AAB with:
  - `.\gradlew.bat :app:bundleRelease --no-daemon`
- Copied the fresh upload artifact to:
  - `release/NeonWing-v0.1.1-code3-release-20260505.aab`
- SHA-256:
  - `A8CFDFD8F366FB9096250F10CB9196A5DD410CA64700F7842BD0DC58D9B970B7`
