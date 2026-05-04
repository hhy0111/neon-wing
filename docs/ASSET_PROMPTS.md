# Neon Wing Asset Prompts

이 문서는 `Neon Wing`을 고퀄리티 2D 슈팅처럼 보이게 만들기 위한 이미지 생성 프롬프트 모음이다.
프롬프트는 이미지 생성 모델에서 안정적으로 먹히도록 영어 중심으로 작성했고, 실제 적용 기준은 한국어로 정리했다.

## 공통 제작 기준

| 구분 | 기준 |
|---|---|
| 화면 비율 | 세로형 420x760 캔버스 기준 |
| 권장 제작 해상도 | 배경 840x1520, 스프라이트 512x512, 미사일 256x512 또는 512x512 |
| 카메라 | 세로 슈팅용 top-down / 약한 3/4 top-down, 기체는 항상 위쪽을 향함 |
| 스타일 | premium 2D mobile arcade shooter, neon sci-fi, hand-painted + clean game sprite |
| 아군 색상 | cyan, blue-white, white core light |
| 적 색상 | red, orange, magenta, violet |
| 미사일 색상 | gold 기본, 고급형은 cyan/magenta/blue-white 변형 |
| 배경 | 어두운 우주/금속/도시 네온, 너무 밝지 않게 |
| 금지 | 텍스트, 로고, 워터마크, UI 버튼, 숫자, 과도한 그림자, 실사 사진풍 |

## 공통 스타일 프롬프트

아래 문장을 각 프롬프트 뒤에 붙이면 전체 이미지 톤이 맞는다.

```text
Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, clean readable shape, luminous edge highlights, painted metal panels, subtle glow, game-ready, no text, no logo, no watermark.
```

스프라이트에는 아래 문장을 추가한다.

```text
Orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size.
```

배경에는 아래 문장을 추가한다.

```text
Seamless vertical scrolling background, portrait composition, designed for parallax layers, no text, no UI, no ships, no characters.
```

## 공통 네거티브 프롬프트

```text
text, letters, numbers, logo, watermark, UI, frame, border, realistic photograph, blurry, low resolution, noisy, messy silhouette, side view, front view, extreme perspective, cropped subject, huge shadow, white background for sprites, characters, pilots, cockpit interior.
```

## 권장 파일 구조

```text
web/assets/bg/bg_far_space.png
web/assets/bg/bg_mid_city.png
web/assets/bg/bg_near_debris.png
web/assets/ships/player_neon_wing.png
web/assets/ships/player_astra.png
web/assets/ships/support_drone.png
web/assets/enemies/enemy_scout_red.png
web/assets/enemies/enemy_striker_orange.png
web/assets/enemies/enemy_wraith_magenta.png
web/assets/enemies/enemy_elite_violet.png
web/assets/bosses/boss_01_carrier.png
web/assets/bosses/boss_02_lancer.png
web/assets/bosses/boss_03_core.png
web/assets/missiles/missile_homing_gold.png
web/assets/missiles/missile_micro_cyan.png
web/assets/missiles/missile_cluster_orange.png
web/assets/missiles/missile_plasma_magenta.png
web/assets/missiles/missile_emp_blue.png
web/assets/missiles/missile_rail_white.png
web/assets/missiles/missile_nova_gold.png
```

## 3중 패럴랙스 배경

### 1. 먼 배경: 우주/성운 레이어

권장: `840x1520`, 불투명 PNG, 가장 느리게 이동.

```text
Deep space far background for a vertical sci-fi shooter, dark navy and black starfield, distant teal nebula clouds, tiny scattered stars, subtle galaxy dust, low contrast so gameplay bullets remain readable, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, neon sci-fi, no ships, no characters, no UI, no text, no logo, no watermark.
```

### 2. 중간 배경: 네온 도시/궤도 구조물 레이어

권장: `840x1520`, 투명 PNG 또는 어두운 반투명 PNG, 중간 속도로 이동.

```text
Mid parallax layer for a vertical sci-fi shooter, futuristic orbital city structures seen from above, dark metallic platforms, thin cyan and magenta neon lane lights, distant industrial panels, elegant sci-fi geometry, lots of empty dark space between structures for gameplay readability, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships, no characters, no UI, no text, no logo, no watermark.
```

### 3. 가까운 배경: 잔해/에너지 라인 레이어

권장: `840x1520`, 투명 PNG, 가장 빠르게 이동. 큰 오브젝트는 화면 가장자리 위주.

```text
Near parallax foreground layer for a vertical sci-fi shooter, small drifting metal debris, glowing cyan energy rails, faint speed streaks, tiny sparks, edge-weighted composition with the center kept clear for player and bullets, transparent background, seamless vertical scrolling portrait layer, premium 2D mobile arcade shooter asset, no ships, no characters, no UI, no text, no logo, no watermark.
```

## 플레이어 기체

### 기본 기체: Neon Wing

권장: `512x512`, 투명 PNG. 게임 내 표시 크기 약 70x90.

```text
Hero player spaceship for a vertical 2D arcade shooter, sleek triangular interceptor, cyan neon wing edges, white glowing core engine, dark gunmetal armor, symmetrical silhouette, agile and premium, small missile hardpoints under the wings, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, clean readable shape, luminous edge highlights, painted metal panels, subtle glow, game-ready, no text, no logo, no watermark.
```

### 프리미엄 기체: Astra

권장: `512x512`, 투명 PNG. 색상은 마젠타/보라 계열로 기본 기체와 구분.

```text
Premium hero spaceship named Astra for a vertical 2D arcade shooter, elegant advanced interceptor, magenta and violet neon wing blades, white plasma core, black chrome armor, sharper silhouette than the default ship, luxury sci-fi design, symmetrical, powerful but readable, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, clean readable shape, luminous edge highlights, painted metal panels, subtle glow, game-ready, no text, no logo, no watermark.
```

### 보조 드론

권장: `256x256`, 투명 PNG.

```text
Small support drone for a vertical 2D sci-fi shooter, compact circular wing drone, cyan white core light, dark metal shell, tiny side fins, friendly silhouette, designed to orbit beside the player ship, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, clean readable shape, no text, no logo, no watermark.
```

## 적 기체

### 적 1: Scout

권장: `512x512`, 투명 PNG. 작고 빠른 기본 적.

```text
Enemy scout aircraft for a vertical 2D arcade shooter, small aggressive alien drone, red neon eye core, jagged dark metal wings, compact fast silhouette, hostile but readable, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, clean readable shape, luminous red highlights, game-ready, no text, no logo, no watermark.
```

### 적 2: Striker

권장: `512x512`, 투명 PNG. 중형 탄막 적.

```text
Enemy striker aircraft for a vertical 2D arcade shooter, medium armored villain ship, orange red glowing weapon pods, dark angular armor plates, twin cannon silhouette, threatening and readable, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, clean readable shape, game-ready, no text, no logo, no watermark.
```

### 적 3: Wraith

권장: `512x512`, 투명 PNG. 회피/마젠타 계열 고급 적.

```text
Enemy wraith aircraft for a vertical 2D arcade shooter, sleek dangerous villain ship, magenta neon core, curved blade wings, stealth alien technology, fast elite silhouette, dark purple metal, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, clean readable shape, game-ready, no text, no logo, no watermark.
```

### 적 4: Elite

권장: `512x512`, 투명 PNG. 현재 게임의 큰 적/엘리트 타입.

```text
Large elite enemy gunship for a vertical 2D arcade shooter, heavy villain aircraft, violet and magenta energy core, broad armored wings, multiple glowing cannons, intimidating boss-like silhouette but still fits a normal enemy, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, clean readable shape, game-ready, no text, no logo, no watermark.
```

## 보스 후보

### 보스 1: Carrier

권장: `1024x1024`, 투명 PNG. 화면 상단에서 크게 등장.

```text
Massive enemy carrier boss for a vertical 2D arcade shooter, huge dark sci-fi battleship seen from top-down, red and orange reactor lines, hangar openings, layered armor plates, broad silhouette, clear weak point core in the center, designed for mobile bullet hell readability, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

### 보스 2: Lancer

권장: `1024x1024`, 투명 PNG. 레이저 패턴용 보스.

```text
Enemy lancer boss ship for a vertical 2D arcade shooter, long spear-shaped villain battleship, magenta laser emitter core, black violet armor, sharp symmetrical wings, intimidating vertical silhouette, clear front weapon barrel, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

### 보스 3: Core

권장: `1024x1024`, 투명 PNG. 최종 코어/광역 패턴 보스.

```text
Final enemy core boss for a vertical 2D arcade shooter, circular mechanical reactor ship, glowing red magenta energy core, rotating armor rings, alien machine design, powerful central weak point, symmetrical top-down silhouette, transparent background. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

## 미사일 종류

미사일은 실제 게임에서 작게 보이므로 실루엣이 가장 중요하다. 모든 미사일은 위쪽을 향한 투명 PNG로 만든다.

### 기본 유도 미사일: Homing Gold

권장: `256x512` 또는 `512x512`, 투명 PNG.

```text
Homing missile sprite for a vertical 2D sci-fi shooter, slim golden missile, cyan sensor tip, tiny wing fins, bright gold engine glow, nose pointing upward, centered on transparent background, readable at very small size. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### 소형 연발 미사일: Micro Cyan

```text
Micro missile sprite for a vertical 2D sci-fi shooter, small compact cyan missile, white glowing tip, short side fins, fast lightweight silhouette, nose pointing upward, centered on transparent background, readable at very small size. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### 분열 미사일: Cluster Orange

```text
Cluster missile sprite for a vertical 2D sci-fi shooter, chunky orange gold missile, segmented warhead, small side pods suggesting split projectiles, warm engine glow, nose pointing upward, centered on transparent background, readable at very small size. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### 플라즈마 어뢰: Plasma Magenta

```text
Plasma torpedo sprite for a vertical 2D sci-fi shooter, thick magenta energy missile, glowing plasma core visible through dark metal casing, violet exhaust, heavy powerful silhouette, nose pointing upward, centered on transparent background, readable at very small size. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### EMP 미사일: EMP Blue

```text
EMP missile sprite for a vertical 2D sci-fi shooter, blue white electric missile, circular coil details, small arcs of contained electricity, clean technical silhouette, nose pointing upward, centered on transparent background, readable at very small size. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### 관통 미사일: Rail White

```text
Rail spear missile sprite for a vertical 2D sci-fi shooter, long white silver piercing missile, narrow spear-like body, cyan rail energy lines, minimal fins, extremely sharp silhouette, nose pointing upward, centered on transparent background, readable at very small size. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### 광역 폭격: Nova Gold

```text
Nova bomb missile sprite for a vertical 2D sci-fi shooter, large golden energy bomb, compact missile body wrapped in circular glowing rings, white hot core, premium ultimate weapon look, nose pointing upward, centered on transparent background, readable at small size. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

## 미사일 한 장 합본 프롬프트

개별 파일이 아니라 한 번에 시안 확인용으로 만들 때 사용한다. 최종 적용은 개별 투명 PNG가 더 좋다.

```text
A clean sprite sheet of seven missile types for a vertical 2D sci-fi arcade shooter, arranged in one horizontal row with even spacing: golden homing missile, cyan micro missile, orange cluster missile, magenta plasma torpedo, blue EMP missile, white rail spear missile, golden nova bomb missile. All missiles point upward, transparent background, consistent top-down game sprite perspective, premium 2D mobile arcade shooter asset, high detail, crisp silhouettes, no text, no labels, no numbers, no logo, no watermark.
```

## 생성 후 적용 체크리스트

1. 배경은 세로로 이어 붙였을 때 끊김이 적은지 확인한다.
2. 스프라이트는 작은 크기에서도 실루엣이 구분되는지 확인한다.
3. 플레이어와 적 색상이 섞이지 않는지 확인한다.
4. 배경 중심부가 너무 밝으면 탄과 적이 묻히므로 어둡게 보정한다.
5. 투명 PNG 가장자리에 흰 테두리나 배경 찌꺼기가 없는지 확인한다.
6. 실제 게임 적용 전 웹에서 먼저 테스트하고, 이후 Android와 같은 파일명 규칙으로 맞춘다.

## 출시 스케일 추가 프롬프트

추가 프롬프트는 [CONTENT_SCALE_PLAN.md](CONTENT_SCALE_PLAN.md)의 전장/악당/미사일 로드맵에 맞춘다.
첫 출시 기준으로 5개 전장, 전장별 3중 패럴랙스 배경, 12종 일반 적, 5종 엘리트, 5종 보스를 목표로 한다.

## 추가 권장 파일 구조

```text
web/assets/bg/neon_orbit_far.png
web/assets/bg/neon_orbit_mid.png
web/assets/bg/neon_orbit_near.png
web/assets/bg/scrap_belt_far.png
web/assets/bg/scrap_belt_mid.png
web/assets/bg/scrap_belt_near.png
web/assets/bg/crimson_foundry_far.png
web/assets/bg/crimson_foundry_mid.png
web/assets/bg/crimson_foundry_near.png
web/assets/bg/frost_relay_far.png
web/assets/bg/frost_relay_mid.png
web/assets/bg/frost_relay_near.png
web/assets/bg/void_citadel_far.png
web/assets/bg/void_citadel_mid.png
web/assets/bg/void_citadel_near.png
web/assets/enemies/enemy_shield_guard.png
web/assets/enemies/enemy_rusher.png
web/assets/enemies/enemy_mine_layer.png
web/assets/enemies/enemy_repair_pod.png
web/assets/enemies/enemy_bomber.png
web/assets/enemies/enemy_turret_node.png
web/assets/enemies/enemy_sniper.png
web/assets/enemies/enemy_emp_drone.png
web/assets/enemies/enemy_splitter.png
web/assets/elites/elite_orbital_enforcer.png
web/assets/elites/elite_scrap_reclaimer.png
web/assets/elites/elite_forge_breaker.png
web/assets/elites/elite_relay_seraph.png
web/assets/elites/elite_void_arbiter.png
web/assets/bosses/boss_carrier.png
web/assets/bosses/boss_reclaimer_titan.png
web/assets/bosses/boss_forge_hydra.png
web/assets/bosses/boss_cryo_lancer.png
web/assets/bosses/boss_void_core.png
web/assets/projectiles/enemy_mine.png
web/assets/projectiles/beam_warning_magenta.png
web/assets/projectiles/emp_ring_blue.png
web/assets/projectiles/void_shard.png
web/assets/maps/map_thumb_neon_orbit.png
web/assets/maps/map_thumb_scrap_belt.png
web/assets/maps/map_thumb_crimson_foundry.png
web/assets/maps/map_thumb_frost_relay.png
web/assets/maps/map_thumb_void_citadel.png
```

## 전장별 3중 배경 프롬프트

### Neon Orbit Far

```text
Far parallax background for Neon Orbit, a vertical sci-fi shooter stage above a futuristic planet, deep navy space, subtle teal nebula, distant curved planet horizon at the bottom edge, tiny stars, low contrast for bullet readability, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, neon sci-fi, no ships, no UI, no text, no logo, no watermark.
```

### Neon Orbit Mid

```text
Mid parallax background for Neon Orbit, top-down futuristic orbital city platforms, dark metal space station roads, thin cyan and magenta neon lanes, elegant defense gate structures near the sides, center lane mostly clear for gameplay, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships, no characters, no UI, no text, no logo, no watermark.
```

### Neon Orbit Near

```text
Near parallax layer for Neon Orbit, transparent foreground with small satellite fragments, cyan energy rails, faint magenta warning lights, edge-weighted debris and speed streaks, center kept clear, seamless vertical scrolling portrait layer, premium 2D mobile arcade shooter asset, no ships, no UI, no text, no logo, no watermark.
```

### Scrap Belt Far

```text
Far parallax background for Scrap Belt, dark outer space filled with distant asteroid silhouettes and faint amber dust clouds, cold stars behind a dangerous debris field, low contrast center, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships, no UI, no text, no logo, no watermark.
```

### Scrap Belt Mid

```text
Mid parallax background for Scrap Belt, abandoned spaceship graveyard seen from above, broken hull plates, rusted industrial beams, dim orange hazard lights, large derelict structures along the edges, center path readable and dark, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships in active combat, no UI, no text, no logo, no watermark.
```

### Scrap Belt Near

```text
Near parallax layer for Scrap Belt, transparent foreground of fast drifting metal shards, small sparks, loose cables, rust orange glints, edge-weighted debris with the center mostly empty, seamless vertical scrolling portrait layer, premium 2D mobile arcade shooter asset, no UI, no text, no logo, no watermark.
```

### Crimson Foundry Far

```text
Far parallax background for Crimson Foundry, dark industrial planet-side foundry from above, black smoke haze, distant red reactor glow, molten orange rivers far below, low contrast gameplay center, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships, no UI, no text, no logo, no watermark.
```

### Crimson Foundry Mid

```text
Mid parallax background for Crimson Foundry, massive sci-fi weapon factory seen from top-down, black metal conveyor rails, glowing red furnace channels, orange warning strips along the sides, mechanical symmetry, center route clear, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships, no UI, no text, no logo, no watermark.
```

### Crimson Foundry Near

```text
Near parallax layer for Crimson Foundry, transparent foreground with heat shimmer, ember sparks, small metal plates, red laser guide lines at the edges, high speed industrial motion feeling, center kept clear, seamless vertical scrolling portrait layer, premium 2D mobile arcade shooter asset, no UI, no text, no logo, no watermark.
```

### Frost Relay Far

```text
Far parallax background for Frost Relay, blue white ice nebula in deep space, distant frozen asteroids, subtle electric storms, cold dark atmosphere, low contrast center for bullet readability, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships, no UI, no text, no logo, no watermark.
```

### Frost Relay Mid

```text
Mid parallax background for Frost Relay, top-down satellite relay array, icy metallic platforms, blue white antenna grids, cyan electric arcs contained in machinery along the edges, elegant cold sci-fi geometry, center path clear, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no active ships, no UI, no text, no logo, no watermark.
```

### Frost Relay Near

```text
Near parallax layer for Frost Relay, transparent foreground with small ice crystals, blue electric particles, faint scanning lines, tiny frozen debris moving fast near the screen edges, center kept clear, seamless vertical scrolling portrait layer, premium 2D mobile arcade shooter asset, no UI, no text, no logo, no watermark.
```

### Void Citadel Far

```text
Far parallax background for Void Citadel, dark alien space fortress environment, black violet nebula, distant red magenta core glow, unsettling geometric silhouettes, low contrast center, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships, no UI, no text, no logo, no watermark.
```

### Void Citadel Mid

```text
Mid parallax background for Void Citadel, top-down alien citadel walls, biomechanical black armor plates, magenta energy veins, red reactor channels, ominous symmetrical architecture along the sides, center gameplay lane readable, seamless vertical scrolling portrait background, premium 2D mobile arcade shooter asset, no ships, no UI, no text, no logo, no watermark.
```

### Void Citadel Near

```text
Near parallax layer for Void Citadel, transparent foreground with floating alien shards, magenta energy cracks, small red sparks, warped speed streaks, edge-weighted composition, center kept clear for player and bullets, seamless vertical scrolling portrait layer, premium 2D mobile arcade shooter asset, no UI, no text, no logo, no watermark.
```

## 추가 일반 적 프롬프트

### Shield Guard

```text
Enemy shield guard aircraft for a vertical 2D arcade shooter, defensive villain drone with a hexagonal shield projector, red orange enemy core, dark metal armor, faint cyan shield panels attached to the sides, sturdy readable silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Rusher

```text
Enemy rusher aircraft for a vertical 2D arcade shooter, fast melee-like villain drone, sharp saw-tooth front armor, rusty dark metal, orange engine glow, aggressive arrow silhouette built for charge attacks, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, no text, no logo, no watermark.
```

### Mine Layer

```text
Enemy mine layer aircraft for a vertical 2D arcade shooter, bulky round villain ship, yellow orange hazard lights, rear mine dispenser pods, dark industrial metal, slow heavy silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, no text, no logo, no watermark.
```

### Repair Pod

```text
Enemy repair pod drone for a vertical 2D arcade shooter, small support unit, red medical-style energy core without any symbol, mechanical arms, tiny repair beam emitters, dark metal shell with orange lights, readable support silhouette, orthographic top-down game sprite, centered composition, transparent background, nose pointing downward, no text, no logo, no watermark.
```

### Bomber

```text
Enemy bomber aircraft for a vertical 2D arcade shooter, heavy villain ship with armored bomb bay, red orange reactor vents, thick wings, visible underside payload pods, slow dangerous silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, no text, no logo, no watermark.
```

### Turret Node

```text
Enemy turret node for a vertical 2D arcade shooter, floating satellite turret, circular dark metal base, rotating twin red cannons, orange targeting lens, no wings, compact readable silhouette, orthographic top-down game sprite, centered composition, transparent background, readable at small size. Premium 2D mobile arcade shooter asset, no text, no logo, no watermark.
```

### Sniper

```text
Enemy sniper aircraft for a vertical 2D arcade shooter, long narrow villain ship with a prominent forward rail cannon, blue magenta targeting lens, black metal armor, elegant cold silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, no text, no logo, no watermark.
```

### EMP Drone

```text
Enemy EMP drone for a vertical 2D arcade shooter, compact electric villain drone, blue white coil rings, dark metal shell, contained lightning arcs, round technical silhouette, orthographic top-down game sprite, centered composition, transparent background, nose pointing downward, readable at small size. Premium 2D mobile arcade shooter asset, no text, no logo, no watermark.
```

### Splitter

```text
Enemy splitter aircraft for a vertical 2D arcade shooter, alien villain ship with cracked black armor, magenta energy seams, body visually split into three connected segments, designed to break into smaller enemies, sharp readable silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing downward, no text, no logo, no watermark.
```

## 엘리트 적 프롬프트

### Orbital Enforcer

```text
Elite enemy gunship Orbital Enforcer for a vertical 2D arcade shooter, large dark orbital police-like warship without text or symbols, red orange weapon pods, cyan stolen shield generator details, broad intimidating silhouette, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Scrap Reclaimer

```text
Elite enemy ship Scrap Reclaimer for a vertical 2D arcade shooter, heavy industrial salvage warship, rusted armor plates, magnetic claw arms, orange furnace glow, debris collector silhouette, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Forge Breaker

```text
Elite enemy ship Forge Breaker for a vertical 2D arcade shooter, brutal factory assault craft, black iron armor, red molten reactor lines, large front ram, explosive side cannons, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Relay Seraph

```text
Elite enemy ship Relay Seraph for a vertical 2D arcade shooter, cold elegant satellite-wing warship, blue white electric halo devices, long sniper cannon, icy dark metal panels, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Void Arbiter

```text
Elite enemy ship Void Arbiter for a vertical 2D arcade shooter, alien judge-like warship, black violet biomechanical armor, magenta energy blades, split-wing silhouette, ominous central red core, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

## 출시 보스 프롬프트

### Carrier

```text
Stage one boss Carrier for a vertical 2D arcade shooter, massive orbital carrier battleship, dark blue black armor, cyan magenta city-light details, red hangar ports releasing drones, wide readable top-down silhouette, central weak point core, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Reclaimer Titan

```text
Stage two boss Reclaimer Titan for a vertical 2D arcade shooter, enormous salvage battleship made of scrap metal armor, magnetic claws, rust orange reactor glow, mine launcher bays, asymmetrical but readable silhouette, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Forge Hydra

```text
Stage three boss Forge Hydra for a vertical 2D arcade shooter, giant factory war machine with three cannon heads, black molten armor, red orange furnace cores, multiple laser barrels, aggressive symmetrical silhouette, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Cryo Lancer

```text
Stage four boss Cryo Lancer for a vertical 2D arcade shooter, long spear-shaped ice relay battleship, blue white electric reactor, magenta rail laser emitter, crystalline armor fins, elegant dangerous silhouette, transparent background, nose pointing downward. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Void Core

```text
Final boss Void Core for a vertical 2D arcade shooter, huge alien mechanical core ship, circular rotating armor rings, black violet biomechanical plates, red magenta central reactor eye, multiple floating weapon nodes, final boss silhouette, transparent background. Premium 2D mobile arcade shooter asset, high detail, crisp silhouette, no text, no logo, no watermark.
```

## 적 탄/위협 이미지 프롬프트

### Enemy Mine

```text
Enemy space mine projectile for a vertical 2D arcade shooter, compact floating mine, dark metal sphere, orange hazard glow, small triangular spikes, transparent background, centered composition, readable at very small size, no text, no logo, no watermark.
```

### Beam Warning

```text
Magenta laser warning effect sprite for a vertical 2D arcade shooter, thin vertical targeting beam line, transparent background, glowing magenta edges, subtle pulsing core, designed as a warning before a sniper laser, no text, no logo, no watermark.
```

### EMP Ring

```text
Blue EMP expanding ring effect for a vertical 2D arcade shooter, circular electric shockwave, blue white arcs, transparent background, clean readable ring, game-ready VFX sprite, no text, no logo, no watermark.
```

### Void Shard

```text
Void shard projectile for a vertical 2D arcade shooter, sharp black violet alien crystal shard, magenta glowing cracks, nose pointing downward, transparent background, readable at very small size, no text, no logo, no watermark.
```

## 전장 선택 썸네일 프롬프트

전장 선택 화면이나 챕터 카드에 쓸 수 있는 이미지다. 실제 게임 배경과 달리 썸네일은 더 화려해도 되지만 텍스트는 넣지 않는다.

### Neon Orbit Thumbnail

```text
Map selection thumbnail for Neon Orbit, futuristic orbital city above a planet, cyan and magenta neon lights, heroic vertical shooter mood, premium 2D sci-fi game art, no text, no logo, no UI, no watermark.
```

### Scrap Belt Thumbnail

```text
Map selection thumbnail for Scrap Belt, dangerous asteroid debris field and abandoned spaceship graveyard, orange sparks and dark metal wreckage, premium 2D sci-fi game art, no text, no logo, no UI, no watermark.
```

### Crimson Foundry Thumbnail

```text
Map selection thumbnail for Crimson Foundry, massive sci-fi weapon factory with red molten reactor channels and black metal machinery, premium 2D sci-fi game art, no text, no logo, no UI, no watermark.
```

### Frost Relay Thumbnail

```text
Map selection thumbnail for Frost Relay, icy blue satellite relay station in a frozen nebula, electric arcs and white crystalline details, premium 2D sci-fi game art, no text, no logo, no UI, no watermark.
```

### Void Citadel Thumbnail

```text
Map selection thumbnail for Void Citadel, ominous alien fortress in black violet space, red magenta core glow, final stage atmosphere, premium 2D sci-fi game art, no text, no logo, no UI, no watermark.
```

## 기체 상점/선택 추가 프롬프트

이 섹션은 `RUN_UPGRADE_AND_SHIP_PLAN.md`의 기체별 미사일/상점 구조에 맞춘다.
기체 이미지는 전부 투명 PNG로 만들고, UI 배경 이미지는 버튼/글자 없이 장식용으로만 만든다.

### 추가 파일 구조

```text
web/assets/ships/player_raptor.png
web/assets/ships/player_bastion.png
web/assets/ships/player_seraph.png
web/assets/ships/player_phantom.png
web/assets/ships/player_nova_x.png
web/assets/ui/hangar_shop_bg.png
web/assets/ui/ship_select_bg.png
web/assets/ui/title_logo_neon_wing_en.png
web/assets/ui/ship_unlock_panel_bg.png
web/assets/ui/upgrade_choice_panel_bg.png
web/assets/icons/icon_coin.png
web/assets/icons/icon_gem.png
web/assets/icons/icon_upgrade_chip.png
web/assets/icons/icon_field_energy.png
```

### Raptor

```text
Player spaceship Raptor for a vertical 2D arcade shooter, fast lightweight interceptor, sharp cyan blue wing fins, compact aerodynamic body, white micro missile launchers, dark gunmetal armor, built for speed and rapid fire, symmetrical silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Bastion

```text
Player spaceship Bastion for a vertical 2D arcade shooter, heavy defensive gunship, broad armored wings, orange gold missile pods, cyan cockpit core, dark reinforced metal plates, sturdy tank-like silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Seraph

```text
Player spaceship Seraph for a vertical 2D arcade shooter, elegant control-focused interceptor, blue white electric wing halos, slim silver dark armor, EMP coil details, calm premium silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Phantom

```text
Player spaceship Phantom for a vertical 2D arcade shooter, stealth piercing interceptor, black violet armor, thin white rail cannon along the center, subtle cyan energy lines, narrow aggressive silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Nova X

```text
Player spaceship Nova X for a vertical 2D arcade shooter, endgame ultimate ship, black chrome armor, golden nova reactor core, circular energy ring details around the wings, white hot engine glow, powerful premium silhouette, orthographic top-down game sprite with a slight cinematic 3/4 tilt, centered composition, transparent background, nose pointing upward, readable at small size. Premium 2D mobile arcade shooter asset, neon sci-fi, high detail, crisp silhouette, no text, no logo, no watermark.
```

### Hangar Shop Background

```text
Hangar shop background for a vertical 2D mobile sci-fi shooter, premium spaceship bay interior, dark metal floor, cyan and magenta neon edge lights, display platform in the center, subtle depth, clean empty areas for UI panels, no text, no logos, no buttons, no characters, no watermark.
```

### Ship Select Background

```text
Ship selection screen background for a vertical 2D mobile sci-fi shooter, futuristic hangar showroom, central spotlight platform for a spaceship, dark gunmetal walls, cyan holographic accents, elegant premium arcade mood, empty space for UI overlays, no text, no logos, no buttons, no characters, no watermark.
```

### Ship Unlock Panel Background

```text
Decorative panel background for a locked spaceship purchase condition screen, vertical mobile game UI asset without text, dark translucent sci-fi glass panel, cyan neon border, small progress slot shapes, premium clean interface background, no letters, no numbers, no logo, no watermark.
```

### Upgrade Choice Panel Background

```text
Decorative background for an in-run upgrade choice panel in a vertical sci-fi shooter, three empty sci-fi card slots, dark translucent glass, cyan gold neon accents, compact mobile game UI asset, no text, no icons with letters, no numbers, no logo, no watermark.
```

### English Title Logo

```text
English title logo wordmark for a premium 2D mobile sci-fi shooter, exact readable text "NEON WING", bold futuristic arcade lettering, cyan white neon core, subtle magenta edge glow, metallic bevel highlights, transparent background, centered horizontal composition, designed to sit over a dark space shooter title screen, no extra words, no subtitle, no icon-only version, no watermark.
```

### Coin Icon

```text
Game currency coin icon for a sci-fi mobile shooter, small gold energy coin, beveled metal rim, glowing center, transparent background, centered icon, readable at small size, no text, no numbers, no logo, no watermark.
```

### Gem Icon

```text
Premium currency gem icon for a sci-fi mobile shooter, cyan magenta crystal gem, luminous facets, transparent background, centered icon, readable at small size, no text, no numbers, no logo, no watermark.
```

### Upgrade Chip Icon

```text
Upgrade chip pickup icon for a vertical sci-fi shooter, small hexagonal circuit chip, cyan gold glow, dark metal frame, transparent background, centered icon, readable at small size, no text, no numbers, no logo, no watermark.
```

### Field Energy Icon

```text
Field energy icon for a vertical sci-fi shooter, small glowing blue energy cell, cylindrical sci-fi battery shape, cyan white core, transparent background, centered icon, readable at small size, no text, no numbers, no logo, no watermark.
```
